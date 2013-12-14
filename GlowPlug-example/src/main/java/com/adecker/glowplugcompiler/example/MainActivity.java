package com.adecker.glowplugcompiler.example;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import com.adecker.glowplug.GlowplugOpenHelper;
import com.adecker.glowplugcompiler.example.model.ActorEntity;
import com.adecker.glowplugcompiler.example.model.EntityList;
import com.adecker.glowplugcompiler.example.model.FilmEntity;

public class MainActivity extends Activity {
	ListView bookmarkList;

    private GlowplugOpenHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    FilmEntity film;

        dbHelper = new GlowplugOpenHelper(this,"Actors",2, EntityList.entities);
        Cursor cursor = dbHelper.getReadableDatabase().query(ActorEntity.TABLE_NAME,null,null,null,null,null,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
