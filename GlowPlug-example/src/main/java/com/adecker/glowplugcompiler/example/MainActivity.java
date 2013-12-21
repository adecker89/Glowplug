package com.adecker.glowplugcompiler.example;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.adecker.glowplug.GlowplugOpenHelper;
import com.adecker.glowplugcompiler.example.model.ActorEntity;
import com.adecker.glowplugcompiler.example.model.EntityList;
import com.adecker.glowplugcompiler.example.model.MyActor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

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

            try {
                stream = manager.open("sakila.json");
                JsonReader reader = new JsonReader(new InputStreamReader(stream));
                reader.setLenient(true);
                reader.beginArray();

                reader.beginArray();
                while (reader.hasNext()) {
                    MyActor actor = new MyActor(reader);
                    Log.d(TAG, "added " + actor);
                    dbHelper.getWritableDatabase().insert(MyActor.TABLE_NAME, null, actor.getContentValues());
                }

                reader.endArray();

                reader.beginArray();
                reader.close();
            } catch (IOException e) {
                Log.d(TAG, "error loading json", e);
            } catch (IllegalStateException e) {
                Log.d(TAG, "error loading json", e);
            } finally {
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

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
