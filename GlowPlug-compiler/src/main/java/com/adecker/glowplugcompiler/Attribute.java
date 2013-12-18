package com.adecker.glowplugcompiler;

/**
 * Created by alex on 10/29/13.
 */
public @interface Attribute {
	String localName() default "";
    String type() default "";
    boolean primaryKey() default false;
}
