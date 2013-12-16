package com.adecker.glowplugcompiler.example.model;

import com.adecker.glowplugcompiler.Attribute;
import com.adecker.glowplugcompiler.Entity;
import com.adecker.glowplugcompiler.Relationship;


/**
 * Created by alex on 10/28/13.
 */
public class DataModel {

	@Entity
	public static class Actor {
		public long id;
		public String firstName;
		public String lastName;
		public long lastUpdate;
	}

	@Entity
	public static class Film {
		@Attribute(localName="film_id")
		public long id;
		public String title;
		public String description;
		public int releaseYear;
		public long languageId;
		public long originalLanguageId;
		public int rentalDuration;
		public double rentalRate;
		public int length;
		public double replacementCost;
        @Attribute(type="INTEGER")
		public Rating rating;
		public long lastUpdate;
	}

	@Entity
	public static class City {
		public long id;
		public String city;

		@Relationship(table=Country.class, key="id")
		public long countryId;
		public long lastUpdate;
	}

	@Entity
	public static class Country {
		public long id;
		public String country;
		public long lastUpdate;
	}

	public static enum Rating {
		G,PG,PG13,R,NC17
	}
}
