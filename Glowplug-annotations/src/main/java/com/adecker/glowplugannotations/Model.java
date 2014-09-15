package com.adecker.glowplugannotations;

/**
 * Created by alex on 10/29/13.
 */
public @interface Model {
	String name() default "";
    boolean generateContentProvider() default false;
    String authority() default "";
}
