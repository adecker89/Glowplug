package com.adecker.glowplugcompiler;

import java.util.Collection;

/**
 * Created by alex on 12/12/13.
 */
public abstract class GlowplugEntity {

    public abstract String getName();
    public abstract String getLocalName();
    public abstract String getRemoteName();

    public abstract GlowplugAttribute[] getAttributes();
    public abstract GlowplugRelationship[] getRelationships();
}
