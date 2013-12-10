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

	public EntityProcessor() {
		super();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		String fqClassName, className, packageName, tableName;

		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "entered");

		for (Element e : roundEnv.getElementsAnnotatedWith(Entity.class)) {
			if (e.getKind() == ElementKind.CLASS) {
				processEntityClass(e);
			}
		}
		return true;
	}

	private void processEntityClass(Element e) {
		String className;
		String packageName;
		String tableName;TypeElement classElement = (TypeElement) e;
		PackageElement packageElement = getPackage(e);

		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "annotated class: " + classElement
				.getQualifiedName());

		className = classElement.getSimpleName().toString();
		packageName = packageElement.getQualifiedName().toString();
		tableName = className;

		List<ColumnElement> fields = getColumns(classElement);

		Properties props = new Properties();
		URL url = this.getClass().getClassLoader().getResource("velocity.properties");
		try {
			props.load(url.openStream());

			VelocityEngine ve = new VelocityEngine(props);
			ve.init();

			VelocityContext vc = new VelocityContext();

			vc.put("className", className);
			vc.put("packageName", packageName);
			//vc.put("f",fields.get(0));
			vc.put("fields", fields.toArray(new ColumnElement[fields.size()]));
			vc.put("tableName", tableName);

			Template vt = ve.getTemplate("entity.vm");

			Filer filer = processingEnv.getFiler();
			JavaFileObject jfo = filer.createSourceFile(packageName + "." + classElement.getSimpleName() +
					"Entity", e);

			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "creating source file: " + jfo
					.toUri());

			Writer writer = jfo.openWriter();
			vt.merge(vc, writer);

			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private List<ColumnElement> getColumns(TypeElement classElement) {
		List<ColumnElement> fields = new ArrayList<ColumnElement>();
		for (Element enclosed : classElement.getEnclosedElements()) {
			if (enclosed.getKind() == ElementKind.FIELD) {
				fields.add(new ColumnElement((VariableElement) enclosed));
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "adding field: " + enclosed
						.getSimpleName());
			}
		}
		return fields;
	}

	public static PackageElement getPackage(Element type) {
		while (type.getKind() != ElementKind.PACKAGE) {
			type = type.getEnclosingElement();
		}
		return (PackageElement) type;
	}
}