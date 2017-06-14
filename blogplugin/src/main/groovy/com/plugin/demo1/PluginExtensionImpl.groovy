package com.plugin.demo1

import org.gradle.api.Plugin
import org.gradle.api.Project

public class PluginExtensionImpl implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create('pluginExt', PluginExtension)
        project.pluginExt.extensions.create('nestExt', PluginNestExtension)
        project.task('customTask', type: CustomTask)

        def transform = new BlogTransform()
        project.android.registerTransform(transform)
    }
}