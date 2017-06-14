package com.plugin.demo1

import javassist.ClassPool
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtField
import javassist.CtMethod
import javassist.CtNewMethod
import javassist.Modifier
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils;
import org.apache.maven.wagon.PathUtils;

/**
 * Created by jiangbenpeng on 12/06/2017.
 * @author benpeng.jiang
 * @version 1.0.0
 */
class CodeInjectByJavassist {
    // 不在这里做判断，是因为还有可能存在类找不到等错误
    public synchronized static byte[] inject(ClassPool pool, String className) throws Exception {
        try {
            CtClass cc = pool.get(className);
            cc.defrost();

            Collection<String> refClasses = cc.getRefClasses();
            if (refClasses.contains("com.taobao.verify.Verifier")) {
                return cc.toBytecode();
            }
            boolean flag = false;
            if (className.equalsIgnoreCase("com.ut.mini.crashhandler.IUTCrashCaughtListner")) {
                flag = true;
            }

            if (cc.isInterface()) {
                final CtClass defClass = pool.get(Class.class.getName());
                CtField defField = new CtField(defClass, "_inject_field__", cc);
                defField.setModifiers(Modifier.STATIC | Modifier.PUBLIC | Modifier.FINAL);
                cc.addField(defField,
                        CtField.Initializer.byExpr("Boolean.TRUE.booleanValue()?java.lang.String.class:com.taobao.verify.Verifier.class;"));

            } else {
                CtConstructor[] ctConstructors = cc.getDeclaredConstructors();
                if (null != ctConstructors && ctConstructors.length > 0) {
                    CtConstructor ctConstructor = ctConstructors[0];
                    ctConstructor.insertBeforeBody("if(Boolean.FALSE.booleanValue()){java.lang.String.valueOf(com.taobao.verify.Verifier.class);}");
                } else {
                    final CtClass defClass = pool.get(Class.class.getName());
                    CtField defField = new CtField(defClass, "_inject_field__", cc);
                    defField.setModifiers(Modifier.STATIC);
                    cc.addField(defField,
                            CtField.Initializer.byExpr("Boolean.TRUE.booleanValue()?java.lang.String.class:com.taobao.verify.Verifier.class;"));
                }

                insertLog(cc);
            }
            return cc.toBytecode();
        } catch (Throwable e) {
            throw new Exception("[InjectError]:" + className + ",reason:" + e.getMessage());
        }
    }

    public static void insertLog(CtClass cc) throws Exception {
        CtMethod[] ctMethods = cc.getMethods();
        if (ctMethods != null) {
            for (int i = 0; i < ctMethods.length; i++) {
                CtMethod ctMethod = ctMethods[i];
                if (cc.equals(ctMethod.getDeclaringClass())) {
                    String methodName = ctMethod.getLongName();
                    ctMethod.insertBefore("{ System.out.println(\"" + methodName + ": before\"); }");
                    ctMethod.insertAfter("{ System.out.println(\"" + methodName + ": end\"); }");
                }
            }
        }

        cc.writeFile();
    }

    /**
     * 对指定的folder中的class文件进行注入替换操作
     *
     * @param pool
     * @param folder
     * @param outFolder
     * @return
     * @throws IOException
     * @throws javassist.CannotCompileException
     * @throws javassist.NotFoundException
     * @throws Exception
     */
    public static List<String> injectFolder(ClassPool pool, File folder, File outFolder) {
        List<String> errorFiles = new ArrayList<String>();

        if (folder.exists() && folder.isDirectory()) {
            println "injectFolder: ${folder.name}"

            String[] names = ["class"]
            Collection<File> classFiles = FileUtils.listFiles(folder,  names, true);
            for (File classFile : classFiles) {
                String className = PathUtils.toRelative(folder, classFile.getAbsolutePath());
                println "injectFolder className: ${className}"
                File outClassFile = new File(outFolder, className);
                outClassFile.getParentFile().mkdirs();
                className = StringUtils.replace(className, File.separator, ".");
                className = className.substring(0, className.length() - 6);
                byte[] codes;
                try {
                    codes = inject(pool, className);
                    FileUtils.writeByteArrayToFile(outClassFile, codes);
                } catch (Throwable e) {
                    System.err.println(e.getMessage());
                    // 发现异常不做处理，使用原先的类
                    try {
                        FileUtils.copyFile(classFile, outClassFile);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return errorFiles;
    }
}

