package com.adecker.glowplugcompiler.example.model;

import android.database.Cursor;
import android.util.JsonReader;

/**
 * Created by alex on 12/19/13.
 */
public class MyActor extends ActorEntity {
    public MyActor() {
        super();
    }

    public MyActor(JsonReader reader) throws IllegalStateException {
        super(reader);
    }

	public String getFirstNameCapitalized() {
		String firstName = getFirstName();
		return firstName.substring(0,1).toUpperCase() + firstName.substring(1).toLowerCase();
	}

	public String getLastNameCapitalized() {
		String lastName = getLastName();
		return lastName.substring(0,1).toUpperCase() + lastName.substring(1).toLowerCase();
	}

    @Override
    public String toString() {
        return this.getFirstNameCapitalized() + " " + this.getLastNameCapitalized();
    }
}
