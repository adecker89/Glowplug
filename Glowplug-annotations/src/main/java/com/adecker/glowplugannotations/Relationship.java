package com.adecker.glowplugannotations;

/**
 * Created by alex on 12/12/13.
 */
public @interface Relationship {
    String sqliteName() default "";
	String remoteName() default"";
    Class<?> table();
    String key();
    boolean manyToMany() default false;
	String[] constaints() default {};
    boolean primaryKey() default false;
}
