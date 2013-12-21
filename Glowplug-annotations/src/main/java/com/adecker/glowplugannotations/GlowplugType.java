package com.adecker.glowplugannotations;

/**
 * Created by alex on 12/20/13.
 */
public enum GlowplugType {
    LONG("java.lang.Long","Long","long"),
    INTEGER("java.lang.Integer","Integer","int"),
    DOUBLE("java.lang.Double","Double","double"),
    FLOAT("java.lang.Float","Float","float"),
    STRING("java.lang.String","String",null),
    BOOLEAN("java.lang.Boolean","Boolean","boolean"),
    BLOB("byte[]","byte[]","byte[]"),
    ;

    public static GlowplugType fromTypeName(String typeName) {
        if (typeName != null) {
            for (GlowplugType type : GlowplugType.values()) {
                if (typeName.equalsIgnoreCase(type.getType())) {
                    return type;
                }
            }
        }
        return null;
    }

    private String typeName, simpleName, primitiveName;

    GlowplugType(String typeName, String simpleName, String primitiveName) {
        this.typeName = typeName;
        this.simpleName = simpleName;
        this.primitiveName = primitiveName;
    }

    public String getType() {
        return typeName;
    }

    public String getSimpleName() {
        return simpleName;
    }

}
