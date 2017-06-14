package com.plugin.demo1;

import com.android.build.api.transform.*;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import com.android.build.gradle.internal.pipeline.TransformManager;

/**
 * Created by jiangbenpeng on 12/06/2017.
 *
 * @author benpeng.jiang
 * @version 1.0.0
 */
public class ClassInjectTransform extends Transform {
    @Override
    public String getName() {
        return "classInject";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<QualifiedContent.ContentType> getOutputTypes() {
        return TransformManager.CONTENT_JARS;
    }

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, IOException,
            InterruptedException {
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        // Gather a full list of all inputs.
        List<JarInput> jarInputs = Lists.newArrayList();
        List<DirectoryInput> directoryInputs = Lists.newArrayList();
        for (TransformInput input : transformInvocation.getInputs()) {
            jarInputs.addAll(input.getJarInputs());
            directoryInputs.addAll(input.getDirectoryInputs());
        }

        outputProvider.deleteAll();
    }
}
