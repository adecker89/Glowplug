package com.adecker.glowplugcompiler.example.model;

import com.adecker.glowplugcompiler.Column;
import com.adecker.glowplugcompiler.Entity;


/**
 * Created by alex on 10/28/13.
 */
@Entity
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
		@Column(name="film_id")
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
		public Rating rating;
		public long lastUpdate;
	}

	@Entity
	public static class City {
		public long id;
		public String city;

		@Column(foreignKeyTable=CountryEntity.TABLE_NAME, foreignKeyColumn=CountryEntity.ColumnStrings.ID)
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
