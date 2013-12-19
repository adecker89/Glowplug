package com.adecker.glowplugcompiler;

/**
 * Created by alex on 10/29/13.
 */
public @interface Attribute {
	String sqliteName() default "";
    String sqliteType() default "";
    boolean primaryKey() default false;
    String primaryKeyContraint() default "";
    boolean autoIncrement() default false;
    String[] constaints() default {};
}
