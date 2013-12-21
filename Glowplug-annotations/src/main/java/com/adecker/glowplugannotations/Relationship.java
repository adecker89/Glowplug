package com.adecker.glowplugannotations;

/**
 * Created by alex on 12/12/13.
 */
public @interface Relationship {
    String localName() default "";
    Class<?> table();
    String key();
    boolean manyToMany() default false;
}
