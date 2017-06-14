package com.plugin.demo1

import com.android.build.api.transform.*
import com.android.build.gradle.internal.scope.VariantScopeImpl
import com.google.common.base.Joiner
import com.google.common.collect.Lists
import com.plugin.demo1.tools.PathUtil
import javassist.ClassPool
import javassist.NotFoundException
import org.gradle.api.tasks.StopExecutionException
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * Created by jiangbenpeng on 12/06/2017.
 *
 * @author benpeng.jiang
 * @version 1.0.0
 */
public class BlogTransform extends Transform {

    @Override
    String getName() {
        return "BlogDoNothing"
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        Set<QualifiedContent.ContentType> set = new HashSet<QualifiedContent.ContentType>();
        set.add(QualifiedContent.DefaultContentType.CLASSES);
        return set;
    }

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        Set<QualifiedContent.Scope> set = new HashSet<QualifiedContent.Scope>();
        set.add(QualifiedContent.Scope.PROJECT);
        return set;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {

        def outDir = outputProvider.getContentLocation("blogdonothing", outputTypes, scopes, Format.DIRECTORY)
        println "transform outDir ${outDir.path}"

        outDir.deleteDir()
        outDir.mkdirs()


        inputs.each {
            it.directoryInputs.each {
                int pathBitLen = it.file.toString().length()
                println "transform filePath ${it.file.path}"

                it.file.traverse {
                    def path = "${it.toString().substring(pathBitLen)}"
                    println "transform path in it ${path}"
                    if (it.isDirectory()) {
                        new File(outDir, path).mkdirs()
                    } else {
                        if (! path.endsWith("BuildConfig.class")) {
                            new File(outDir, path).bytes = it.bytes
                        }
                    }

                }
            }
        }

        inputs.each {
            it.jarInputs.each {
                println ("transform jar each: ${it.file.path}")
            }
        }

        // Gather a full list of all inputs.
        List<JarInput> jarInputs = Lists.newArrayList();
        List<DirectoryInput> directoryInputs = Lists.newArrayList();

        for (TransformInput input : inputs) {
            jarInputs.addAll(input.getJarInputs());
            directoryInputs.addAll(input.getDirectoryInputs());
        }

        outputProvider.deleteAll();
        ClassPool classPool = initClassPool(jarInputs, directoryInputs);

        // 注入jar中的代码
        for (JarInput jarInput : jarInputs) {
            String jarFileName = jarInput.getFile().getName();
            println "transform jars [ClassInject] ${jarInput.getFile().getAbsolutePath()} jarFileName: ${jarFileName}"
        }

        // 注入目录中的代码
        for (DirectoryInput directoryInput : directoryInputs) {
            String folderName = directoryInput.getFile().getName();

            println "transform directory [ClassInject] ${directoryInput.getFile().getAbsolutePath()} folderName: ${folderName}"
            File to = outputProvider.getContentLocation(folderName,
                    getOutputTypes(), getScopes(), Format.DIRECTORY);
            CodeInjectByJavassist.injectFolder(classPool, directoryInput.getFile(), to);
        }

    }

    private ClassPool initClassPool(List<JarInput> jarInputs, List<DirectoryInput> directoryInputs) {

        FieldUtils.writeStaticField(ClassPool.class, "defaultPool", null, true);
//        if (((VariantScopeImpl) this.scope).getVariantData().getName().toLowerCase().contains("debug")) {
//            try {
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        } else {
//            logger.warning(">>> 请勿开启daemon <<<<");
//        }

        final ClassPool pool = ClassPool.getDefault();

        try {
            File verifyFile = PathUtil.getJarFile(com.taobao.verify.Verifier.class);
            pool.insertClassPath(verifyFile.getAbsolutePath());
//            for (File file : scope.getJavaClasspath()) {
//                if (file.isFile()) {
//                    pool.insertClassPath(file.getAbsolutePath());
//                } else {
//                    pool.appendClassPath(file.getAbsolutePath());
//                }
//            }
//            String path = Joiner.on(File.pathSeparator).join(
//                    scope.getGlobalScope().getAndroidBuilder().getBootClasspathAsStrings(false));
//            pool.appendPathList(path);

            for (JarInput jarInput : jarInputs) {
                pool.insertClassPath(jarInput.getFile().getAbsolutePath());
            }
            for (DirectoryInput directoryInput : directoryInputs) {
                pool.appendClassPath(directoryInput.getFile().getAbsolutePath());
            }
        } catch (NotFoundException e) {
            throw new StopExecutionException(e.getMessage());
        }
        println "transform init pool success"
        return pool;
    }


}
