package com.adecker.glowplugcompiler.example.model;

import android.content.Context;
import com.adecker.glowplug.GlowplugEntity;
import com.adecker.glowplug.GlowplugOpenHelper;

/**
 * Created by alex on 12/25/13.
 */
public class SakilaHelper extends GlowplugOpenHelper{

	public SakilaHelper(Context context) {
		super(context, "Sakila", 2, EntityList.entities);
	}
}
