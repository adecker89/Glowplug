package com.adecker.glowplugcompiler;

/**
 * Created by alex on 10/29/13.
 */
public @interface Column {
	String name() default "";
	String foreignKeyTable() default "";
	String foreignKeyColumn() default "";
}
