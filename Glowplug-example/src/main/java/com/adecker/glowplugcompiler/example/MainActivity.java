package com.adecker.glowplugcompiler.example;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.adecker.glowplug.GlowplugEntity;
import com.adecker.glowplug.GlowplugOpenHelper;
import com.adecker.glowplugcompiler.example.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class MainActivity extends Activity implements FilmListFragment.OnFragmentInteractionListener, ActorListFragment.OnActorInteractionListener {
    private static final String TAG = "MainActivity";
    ListView bookmarkList;

	private TextView status;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		status = (TextView) findViewById(R.id.statusText);
    }
	
	public void updateStatus(String statusText) {
		status.setText(statusText);
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

	@Override
	public void onFragmentInteraction(String id) {

	}

	@Override
	public void onActorClick(String id) {

	}

	public static class LoadJsonTask extends AsyncTask<Void, String, Integer> {

        private WeakReference<MainActivity> weakContext;
	    private HashMap<String,Integer> progress = new HashMap<String, Integer>();

        public LoadJsonTask(MainActivity context) {
            weakContext = new WeakReference<MainActivity>(context);
        }

        @Override
        protected Integer doInBackground(Void... params) {
	        MainActivity context = weakContext.get();
            if (context == null) {
                return 0;
            }
	        GlowplugOpenHelper dbHelper = new SakilaHelper(context);

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
		    int count = 0;
		    reader.beginArray();
		    while (reader.hasNext()) {
			    entity.fromJson(reader);
			    db.insert(entity.getEntityName(), null, entity.getContentValues());
			    count++;
			    progress.put(entity.getEntityName(),Integer.valueOf(count));
			    publishProgress(String.format("Loaded %d %s",count,entity.getEntityName()));
		    }
		    reader.endArray();
	    }

	    @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);


		    MainActivity context = weakContext.get();
		    if (context != null) {
			   context.updateStatus(values[0]);
		    }
        }

	    @Override
	    protected void onPostExecute(Integer integer) {
		    super.onPostExecute(integer);
//		    MainActivity context = weakContext.get();
//		    if (context != null) {
//			    context.loadList();
//		    }
	    }
    }
}
