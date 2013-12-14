package com.adecker.glowplugcompiler;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by alex on 9/19/13.
 */

@SupportedAnnotationTypes("com.adecker.glowplugcompiler.Entity")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class EntityProcessor extends AbstractProcessor {

	private VelocityEngine ve;

    public EntityProcessor() {
		super();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		String fqClassName, className, packageName, tableName;
		ArrayList<Element> entities = new ArrayList<Element>();

		ve = initVelocity();

		if (ve == null) {
			return true;
		}

		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "entered");

		for (Element e : roundEnv.getElementsAnnotatedWith(Entity.class)) {
			if (e.getKind() == ElementKind.CLASS) {
				processEntityClass(e);
				entities.add(e);
			}
		}

		generateEntityList(entities);
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
		PackageElement packageElement = getPackage(e);

		className = classElement.getSimpleName().toString();
		packageName = packageElement.getQualifiedName().toString();
		tableName = className;



		VelocityContext vc = new VelocityContext();

		vc.put("className", className);
		vc.put("packageName", packageName);
		vc.put("tableName", tableName);

        ArrayList<GlowplugAttribute> attrs = new ArrayList<GlowplugAttribute>();
        ArrayList<GlowplugRelationship> relationships = new ArrayList<GlowplugRelationship>();
        parseFields(classElement, attrs, relationships);
        vc.put("attrs", attrs.toArray(new GlowplugAttribute[attrs.size()]));
        vc.put("relationships", relationships.toArray(new GlowplugRelationship[relationships.size()]));

		Template vt = ve.getTemplate("entity.vm");

		String name = packageName + "." + classElement.getSimpleName() + "Entity";

		writeTemplateToFile(vt, vc, name, e);
	}

    private void parseFields(TypeElement classElement, List<GlowplugAttribute> attrs, List<GlowplugRelationship> relationships) {
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.FIELD) {
                VariableParser parser = new VariableParser(processingEnv,(VariableElement) enclosed);
                if(parser.isRelationship()) {
                    relationships.add(parser.parseRelationship());
                } else {
                    attrs.add(parser.parseAttribute());
                }
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "adding field: " + enclosed
                        .getSimpleName());
            }
        }
    }

	private void generateEntityList(ArrayList<Element> entities) {
		if(entities == null || entities.isEmpty()) {
			return;
		}
		String packageName = getPackage(entities.get(0)).getQualifiedName().toString();
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

	public static PackageElement getPackage(Element type) {
		while (type.getKind() != ElementKind.PACKAGE) {
			type = type.getEnclosingElement();
		}
		return (PackageElement) type;
	}
}