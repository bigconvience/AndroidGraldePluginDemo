package com.afterecho.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by jiangbenpeng on 13/06/2017.
 * @author benpeng.jiang
 * @version 1.0.0
 */
class BlogPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
//        def showDevicesTask = target.tasks.create("showDevices") << {
//            def adbExe = target.android.getAdbExe().toString()
//            println "${adbExe} devices".execute().text
//        }
//        showDevicesTask.group = "blogplugin"
//        showDevicesTask.description = "Run adb devices command"

        target.tasks.create(name: "showDevices", type: ShowDevicesTask)

        target.android.applicationVariants.all { variant ->
            File inputWordFile = new File(target.projectDir, "plugin_words.txt")
            File outputDir = new File(target.buildDir, "generated/source/wordsToEnum/${variant.dirName}")
            def task = target.tasks.create(name: "wordsToEnum${variant.name.capitalize()}", type: WordsToEnumTask) {
                outDir = outputDir
                wordsFile = inputWordFile
            }
        }
    }
}
