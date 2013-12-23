package com.adecker.glowplugcompiler.example.model;

import com.adecker.glowplugannotations.Attribute;
import com.adecker.glowplugannotations.Entity;
import com.adecker.glowplugannotations.Relationship;


/**
 * Created by alex on 10/28/13.
 *
 * The following exemplifies how to define your data model in a single file.
 * Parts may be intentionally inconsistent in order to demonstrate alternative functionality
 */
public class DataModel {

	@Entity
	public static class Actor {
		@Attribute(primaryKey = true, autoIncrement = true, sqliteName = "_id", remoteName="actor_id")
		public long id;
        @Attribute(remoteName = "first_name")
		public String firstName;
        @Attribute(remoteName = "last_name")
		public String lastName;
		@Attribute(remoteName = "last_update")
		public long lastUpdate;
	}

	@Entity
	public static class Address {
		@Attribute(primaryKey = true, autoIncrement = true, sqliteName = "_id", remoteName="address_id")
		public long id;
		public String address;
		public String address2;
		public String district;
		@Relationship(table=City.class, key="_id", remoteName="city_id", constaints = {"DEFERRABLE INITIALLY DEFERRED"})
		public long city;
		@Attribute(remoteName = "postal_code")
		public String postalCode;
		public String phone;
		@Attribute(remoteName = "last_update")
		public long lastUpdate;
	}

	@Entity
	public static class Category {
		@Attribute(primaryKey = true, autoIncrement = true, sqliteName = "_id", remoteName="category_id")
		public long id;
		public String name;
		@Attribute(remoteName = "last_update")
		public String lastUpdate;
	}


	@Entity
	public static class City {
		@Attribute(primaryKey = true, autoIncrement = true, sqliteName = "_id", remoteName="city_id")
		public long id;
		public String city;

		@Relationship(table=Country.class, key="_id", sqliteName = "country_id", constaints = {"DEFERRABLE INITIALLY DEFERRED"})
		public long countryId;
		@Attribute(remoteName = "last_update")
		public long lastUpdate;
	}

	@Entity
	public static class Country {
		@Attribute(primaryKey = true, primaryKeyConflict = "ON CONFLICT REPLACE")
		public long _id;
		public String country;
		@Attribute(remoteName = "last_update")
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
		public int rating;
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
        @Relationship(table=Store.class, key="_id", sqliteName = "store")
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
        public byte[] picture;
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
