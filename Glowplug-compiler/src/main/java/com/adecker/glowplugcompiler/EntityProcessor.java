package com.adecker.glowplugcompiler;

import com.adecker.glowplugannotations.Entity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DisplayTool;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by alex on 9/19/13.
 */

@SupportedAnnotationTypes("com.adecker.glowplugannotations.Entity")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class EntityProcessor extends AbstractProcessor {

    private VelocityEngine ve;

    public EntityProcessor() {
        super();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        long startTime = System.currentTimeMillis();
        String fqClassName, className, packageName, tableName;
        ArrayList<Element> entities = new ArrayList<Element>();

        ve = initVelocity();

        if (ve == null) {
            return true;
        }

        for (Element e : roundEnv.getElementsAnnotatedWith(Entity.class)) {
            if (e.getKind() == ElementKind.CLASS) {
                processEntityClass(e);
                entities.add(e);
            }
        }

        generateEntityList(entities);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "finished annotation processing, took " + (System.currentTimeMillis() - startTime) + " milliseconds" + startTime);
        return true;
    }

    private VelocityEngine initVelocity() {
        String propName = "velocity.properties";
        try {
            Properties props = new Properties();
            URL url = this.getClass().getClassLoader().getResource(propName);
            props.load(url.openStream());

            VelocityEngine ve = new VelocityEngine(props);
            ve.init();
            return ve;
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to initialize Velocity. Unable " +
                    "to read file " + propName);
            return null;
        }

    }

    private void processEntityClass(Element e) {
        String className;
        String packageName;
        String tableName;
        TypeElement classElement = (TypeElement) e;
        PackageElement packageElement = Util.getPackage(e);

        className = classElement.getSimpleName().toString();
        packageName = packageElement.getQualifiedName().toString();
        tableName = className;


        VelocityContext vc = new VelocityContext();

        vc.put("className", className);
        vc.put("packageName", packageName);
        vc.put("tableName", tableName);

        ArrayList<VariableParser.AttributeStruct> attrs = new ArrayList<VariableParser.AttributeStruct>();
        ArrayList<VariableParser.RelationStruct> relationships = new ArrayList<VariableParser.RelationStruct>();
        if (parseFields(classElement, attrs, relationships)) {
            vc.put("attrs", attrs);
            vc.put("relationships", relationships);
            vc.put("display", new DisplayTool());

            Template vt = ve.getTemplate("entity.vm");

            String name = packageName + "." + classElement.getSimpleName() + "Entity";

            writeTemplateToFile(vt, vc, name, e);
        }
    }

    private boolean parseFields(TypeElement classElement, List<VariableParser.AttributeStruct> attrs, List<VariableParser.RelationStruct> relationships) {
        int i = 0;
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.FIELD) {
                VariableParser parser = new VariableParser(processingEnv, (VariableElement) enclosed);
                if (parser.isRelationship()) {
                    VariableParser.RelationStruct relationStruct = parser.parseRelationship();
                    relationStruct.index = i;
                    relationships.add(relationStruct);
                } else {
                    VariableParser.AttributeStruct attributeStruct = parser.parseAttribute();
                    attributeStruct.index = i;
                    VariableParser.AttributeStruct attr = attributeStruct;
                    if (attr == null) {
                        return false;
                    }
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "adding attribute: " + attr.name);
                    attrs.add(attr);
                }
                i++;
            }
        }
        return true;
    }

    private void generateEntityList(ArrayList<Element> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        String packageName = Util.getPackage(entities.get(0)).getQualifiedName().toString();
        String name = packageName + ".EntityList";

        VelocityContext vc = new VelocityContext();
        vc.put("classes", entities);
        vc.put("packageName", packageName);

        Template vt = ve.getTemplate("entity-list.vm");


        writeTemplateToFile(vt, vc, name, null);
    }

    private void writeTemplateToFile(Template vt, VelocityContext vc, String fileName, Element originatingElement) {
        try {
            Filer filer = processingEnv.getFiler();

            JavaFileObject jfo = filer.createSourceFile(fileName, originatingElement);

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo
                    .toUri());

            Writer writer = jfo.openWriter();
            vt.merge(vc, writer);

            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write file" + fileName);
        }
    }
}