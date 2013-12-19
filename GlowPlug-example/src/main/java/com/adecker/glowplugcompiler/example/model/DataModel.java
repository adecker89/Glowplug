package com.adecker.glowplugcompiler.example.model;

import android.graphics.Bitmap;

import com.adecker.glowplugcompiler.Attribute;
import com.adecker.glowplugcompiler.Entity;
import com.adecker.glowplugcompiler.Relationship;


/**
 * Created by alex on 10/28/13.
 *
 * The following exemplifies how to define your data model in a single file.
 * Parts may be intentionally inconsistent in order to demonstrate alternative functionality
 */
public class DataModel {

	@Entity
	public static class Actor {
		public long _id;
		public String firstName;
		public String lastName;
		public long lastUpdate;
	}

	@Entity
	public static class Film {
        @Attribute(primaryKey = true, constaints = {""})
		public long _id;
		public String title;
		public String description;
		public int releaseYear;
        @Relationship(table=Language.class, key="_id")
		public long languageId;
        @Relationship(table=Language.class, key="_id")
		public long originalLanguageId;
		public int rentalDuration;
		public double rentalRate;
		public int length;
		public double replacementCost;
        @Attribute(sqliteType="INTEGER")
		public Rating rating;
		public long lastUpdate;
	}

	@Entity
	public static class City {
        @Attribute(primaryKey = true, autoIncrement = true)
		public long _id;
		public String city;

		@Relationship(table=Country.class, key="_id")
		public long countryId;
		public long lastUpdate;
	}

	@Entity
	public static class Country {
        @Attribute(primaryKey = true, primaryKeyContraint = "ON CONFLICT REPLACE")
		public long _id;
		public String country;
		public long lastUpdate;
	}

    @Entity
    public static class Address {
        @Attribute(primaryKey = true)
        public long _id;
        public String address;
        public String address2;
        public String district;
        @Relationship(table=City.class, key="_id")
        public long city;
        public String postalCode;
        public String phone;
        public long lastUpdate;
    }

    @Entity
    public static class Language {
        @Attribute(sqliteName = "_id",primaryKey = true)
        public long id;
        public String name;
        public long lastUpdate;
    }

    @Entity
    public static class Customer {
        @Attribute(primaryKey = true)
        public long _id;
        @Relationship(table=Store.class, key="_id", localName = "store")
        public long homeStore;
        public String firstName;
        public String lastName;
        public String email;
        @Relationship(table=Address.class, key="_id")
        public long address;
        public boolean active;
        public long createDate;
        public long lastUpdate;
    }

    @Entity
    public static class Store {
        @Attribute(primaryKey = true)
        public long _id;
        @Relationship(table=Staff.class, key = "_id")
        public long manager;
        @Relationship(table=Address.class, key = "_id")
        public long address;
        public long lastUpdate;
    }

    @Entity
    public static class Staff {
        @Attribute(sqliteName = "_id", primaryKey = true)
        public long id;
        public String firstName;
        public String lastName;
        @Relationship(table = Address.class, key = "_id")
        public long address;
        @Attribute(sqliteType = "BLOB")
        public Bitmap picture;
        public String email;
        public boolean active;
        public String username;
        public String password;
        public long lastUpdate;
    }

	public static enum Rating {
		G,PG,PG13,R,NC17
	}
}
