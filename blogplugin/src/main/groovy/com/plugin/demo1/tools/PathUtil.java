/*
 * Copyright 2015 Taobao.com All right reserved. This software is the
 * confidential and proprietary information of Taobao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Taobao.com.
 */
package com.plugin.demo1.tools;

import java.io.File;

/**
 * 路径的工具类 
 * @author shenghua.nish 2012-6-20 下午1:16:01
 */
public class PathUtil {

    /**
     * 获取当前Jar文件的文件对象
     * @return
     */
    public static File getCurrentJarFile(){
       return getJarFile(PathUtil.class);
    }
    /**
     * 得到jar包所在的文件夹
     * @param klazz
     * @return
     */
    public static File getJarFile(Class<?> klazz){
        String path = klazz.getProtectionDomain().getCodeSource().getLocation().getFile();
        File jarFile = new File(path);
       return jarFile;
    }

    public static String toRelative(File basedir, String absolutePath) {
        absolutePath = absolutePath.replace('\\', '/');
        String basedirPath = basedir.getAbsolutePath().replace('\\', '/');
        String relative;
        if(absolutePath.startsWith(basedirPath)) {
            relative = absolutePath.substring(basedirPath.length());
            if(relative.startsWith("/")) {
                relative = relative.substring(1);
            }

            if(relative.length() <= 0) {
                relative = ".";
            }
        } else {
            relative = absolutePath;
        }

        return relative;
    }
   
}
