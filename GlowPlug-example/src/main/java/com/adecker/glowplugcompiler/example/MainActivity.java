package com.adecker.glowplugcompiler.example;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import com.adecker.glowplug.GlowplugEntity;
import com.adecker.glowplug.GlowplugOpenHelper;
import com.adecker.glowplugcompiler.example.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    ListView bookmarkList;

    private GlowplugOpenHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadJson(View v) {
        new LoadJsonTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public static class LoadJsonTask extends AsyncTask<Void, Integer, Integer> {

        private WeakReference<Context> weakContext;

        public LoadJsonTask(Context context) {
            weakContext = new WeakReference<Context>(context);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Context context = weakContext.get();
            if (context == null) {
                return 0;
            }
            GlowplugOpenHelper dbHelper = new GlowplugOpenHelper(context, "Sakila", 2, EntityList.entities);


            AssetManager manager = context.getAssets();
            InputStream stream = null;

	        SQLiteDatabase db = dbHelper.getWritableDatabase();
	        dbHelper.onUpgrade(db,0,0);

	        db.beginTransaction();

            try {


                stream = manager.open("sakila.json");
                JsonReader reader = new JsonReader(new InputStreamReader(stream));
                reader.setLenient(true);
                reader.beginArray();

	            parseAndInsertEntityArray(db, reader, new MyActor());
	            parseAndInsertEntityArray(db, reader, new AddressEntity());
	            parseAndInsertEntityArray(db, reader, new CategoryEntity());
	            parseAndInsertEntityArray(db, reader, new CityEntity());
	            parseAndInsertEntityArray(db, reader, new CountryEntity());
	            parseAndInsertEntityArray(db, reader, new CustomerEntity());
	            parseAndInsertEntityArray(db, reader, new FilmEntity());

	            db.setTransactionSuccessful();
                reader.beginArray();
                reader.close();
            } catch (IOException e) {
                Log.d(TAG, "error loading json", e);
            } catch (IllegalStateException e) {
                Log.d(TAG, "error loading json", e);
            } finally {
	            db.endTransaction();
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

	    private void parseAndInsertEntityArray(SQLiteDatabase db, JsonReader reader, GlowplugEntity entity) throws IOException {
		    reader.beginArray();
		    while (reader.hasNext()) {
			    entity.fromJson(reader);
			    db.insert(entity.getEntityName(), null, entity.getContentValues());
		    }
		    reader.endArray();
	    }

	    @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
