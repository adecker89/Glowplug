package com.adecker.glowplug;

import com.adecker.glowplugannotations.GlowplugType;

/**
 * Created by alex on 12/20/13.
 */
public abstract class GlowplugProperty {
    public abstract String getName();

    public abstract String getSqliteName();

    public abstract String getRemoteName();

    public abstract String getFQName();

    public abstract GlowplugType getType();

    public abstract int getIndex();
}
