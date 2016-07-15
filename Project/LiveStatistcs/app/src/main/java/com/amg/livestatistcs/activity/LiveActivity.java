package com.amg.livestatistcs.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.amg.livestatistcs.R;
import com.amg.livestatistcs.adapter.LiveListAdapter;
import com.amg.livestatistcs.model.Player;

import java.util.ArrayList;

public class LiveActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private AppBarLayout appBarLayout;
    private Bitmap bg;
    private ArrayList<Player> liveArrayList;
    private LiveListAdapter listAdapter;
    private ListView liveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        //AppBarLayout settings
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

        //ListView settings
        liveArrayList = new ArrayList<>();
        liveView = (ListView) findViewById(R.id.live_list);
        listAdapter = new LiveListAdapter(liveArrayList, this);
        liveView.setAdapter(listAdapter);

        creatingCourtBitmap();
        setToolbarMap();
    }

    void creatingCourtBitmap(){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inScaled = true;
        opt.inMutable  = true;
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.court, opt);
    }

    void setToolbarMap(){
        Canvas canvas = new Canvas(bg);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(300,300, 50, paint);

        BitmapDrawable drawable = new BitmapDrawable(getResources(), bg);
        appBarLayout.setBackground(drawable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        Log.d("AppBarLayout", "offset = " + offset);
        if (offset > -165){ //Showing app bar layout
            getSupportActionBar().setTitle(null);
        }
        else { //Not collapsed
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }
}

/*  Beacon beacon1 = beaconList.get(0);
    Beacon beacon2 = beaconList.get(1);
    double l1 = beacon1.getRealDistance();
    double l2 = beacon2.getRealDistance();
    double y = yComponent(l1,l2,10);
    double x = xComponent(l1,y);
    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
    builder.setTitle("Jogador").setMessage("X = " + x + "Y = " + y).create().show();*/
