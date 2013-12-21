package com.adecker.glowplugannotations;

/**
 * Created by alex on 10/29/13.
 */
public @interface Attribute {
	String sqliteName() default "";
    String sqliteType() default "";
    String remoteName() default "";
    boolean primaryKey() default false;
    String primaryKeyConflict() default "";
    boolean autoIncrement() default false;
    String[] constaints() default {};
}
