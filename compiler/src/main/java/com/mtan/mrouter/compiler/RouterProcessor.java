package com.mtan.mrouter.compiler;

import com.google.auto.service.AutoService;
import com.mtan.mrouter.annotation.Route;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (elements == null || elements.size() <= 0) {
            return false;
        }

        ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Class.class)
        );

        ParameterSpec param = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build();

        MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder("loadInto")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(param);

        for (Element e : elements) {
            messager.printMessage(Diagnostic.Kind.NOTE, e.getSimpleName().toString());

            String path = e.getAnnotation(Route.class).path();
            ClassName className = ClassName.get((TypeElement) e);
            loadIntoMethodOfRootBuilder.addStatement("atlas.put($S, $T.class)", path, className);
        }



        try {
            String rootFileName = "MRouter$$Group$$activity";
            JavaFile.builder("com.mtan.mrouter.apt",
                    TypeSpec.classBuilder(rootFileName)
                            .addJavadoc("NOT MODIFY!!!")
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(loadIntoMethodOfRootBuilder.build())
                            .build()
            ).build().writeTo(mFiler);
        } catch (Throwable t) {
            messager.printMessage(Diagnostic.Kind.ERROR, t.getMessage());
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(Route.class.getCanonicalName());
        return annotations;
    }
}
