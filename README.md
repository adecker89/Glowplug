Glowplug
========


Glowplug is a helper library for Android that uses compile-time code generation to make working with sqlite queries and cursors easier.

**Glowplug is not an ORM**

Glowplug takes a concise data model and expands it with generated helper classes. So your model may look something like this.

```
@Entity
public class Person {
    @Attribute(primaryKey = true)
    public long _id;
    public String firstName;
    public String lastName;
    public int numWorkingLegs
}

@Entity
public class Loan {
    public long _id;
    
    @Relationship(table = Person.class, key = "_id")
    public long person;
    public int amount
}
```

A representation like this would be easy to read and maintain, but not super useful by itself when you need to work with sqlite. We take this representation and generate all the extra components that make it easy to construct and process sqlite queries.

####Features

##### Generated classes can wrap cursors
This wrapper looks and feels like a POJO, but is backed by a cursor. This approach provides the ease of use and extensibility of a POJO without sacrificing the performance and integration of a cursor.

*CursorAdapter example*
```
public static class PersonAdapter extends CursorAdapter{
	PersonEntity person = new PersonEntity();

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
	  //no allocations because we can reuse the person object
	  person.fromCursor(cursor)
		TextView tv = (TextView) view.findViewById(android.R.id.text1);
		tv.setText(person.getFirstName() + " " + person.getLastName());
	}
}
```
*List example*
```
Cursor cursor = someQuery();
List<PersonEntity> persons = new ArrayList<PersonEntity>(cursor.getCount());
while(cursor.moveToNext()) {
  //PersonEntity holds a reference to the cursor and position instead of copying everything from the row
  persons.add(new PersonEntity(cursor));
}
```

##### Generated classes are extensible

Extensible classes allow for Object-Oriented practices to be better applied when workign with a sqlite data model. Extend the entities and place helper methods that act on entities inside, make polymorphic entities, etc.
```
public class MyPerson extends PersonEntity {
  /*...constructors...*/
  
  public String getDisplayName() {
    return person.getFirstName() + " " + person.getLastName();
  }
  
  public int computeAmountOwed() {
    /* ... */
  }
  
  public void breakLeg() {
    /* ... */
  }
}
```
##### Generated constants for Entity, Attribute, and Relationship names
```
String selection = "SELECT * FROM " + PersonEntity.TABLE_NAME + " WHERE " + 
db.query(PersonEntity.TABLE_NAME,null,PersonEntity.AttributeNames.FIRSTNAME+"=?","Fred",null,null,null);
```
##### Generated enums Attributes and Relationships
similar to the generated constants, but the enums can also provide useful metadata about the column it represents such as the fully-qualified column name.
```
//useful if two tables have the same column name, such as "_id"
String[] projection = new String[] {PersonEntity.Attribute._ID.getFQName()};
db.query(someCrazyJoin,projection,null,null,null,null,null);
```
##### Ready to use SqliteOpenHelper
Glowplug provides an extensible SqliteOpenHelper that will create the database from your annotated model
```
new GlowplugOpenHelper(context, DATABASE_NAME, VERSION, EntityList.entities);
```

####Usage
#####Android Studio
Please refer to the Glowplug-example project for an example build.gradle file.
#####Eclipse
TODO
