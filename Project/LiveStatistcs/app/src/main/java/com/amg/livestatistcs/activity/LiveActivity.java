package com.amg.livestatistcs.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.amg.livestatistcs.R;
import com.amg.livestatistcs.adapter.LiveListAdapter;
import com.amg.livestatistcs.model.Beacon;
import com.amg.livestatistcs.model.Player;
import com.amg.livestatistcs.provider.BeaconManagement;
import com.amg.livestatistcs.provider.MatchManagement;
import com.amg.livestatistcs.provider.PlayerManagement;
import com.amg.livestatistcs.provider.SettingsManagement;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LiveActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private AppBarLayout appBarLayout;
    private Bitmap bg;
    private ArrayList<Player> liveArrayList;
    private LiveListAdapter listAdapter;
    private ListView liveView;
    private Handler handler;
    private SettingsManagement settingsManagement;
    private MatchManagement matchManagement;
    private PlayerManagement playerManagement;
    private FloatingActionButton fab;
    private boolean startgame = true;
    private double courtX;
    private double courtY;
    private int time = 0;
    private final int INTERVAL = 500; //500ms
    private final int COLORS[] = {R.drawable.brown_400, R.drawable.dpurple_300, R.drawable.gray_500,
                                  R.drawable.green_a400, R.drawable.indigo_500, R.drawable.lblue_a100,
                                  R.drawable.lgreen_a200, R.drawable.orange_800, R.drawable.pink_300,
                                  R.drawable.red_500, R.drawable.teal_a200, R.drawable.yellow_500};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        handler = new Handler();
        matchManagement = new MatchManagement(this);

        //AppBarLayout settings
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);
        creatingCourtBitmap();

        //ListView settings
        liveArrayList = new ArrayList<>();
        liveView = (ListView) findViewById(R.id.live_list);
        listAdapter = new LiveListAdapter(liveArrayList, this);
        liveView.setAdapter(listAdapter);

        //Retrieving court dimensions
        settingsManagement = new SettingsManagement(this);
        float values[] = settingsManagement.retrieveDimensions();
        courtX = values[0];
        courtY = values[1];

        //Retrieving Players Settings
        playerManagement = new PlayerManagement(this);
        playerManagement.fetchPlayerSettings();

        //FloatActionButton
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startgame){
                    startRepeatingTask();
                    fab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    startgame = false;
                }
                else{
                    stopRepeatingTask();
                    fab.setImageResource(R.drawable.ic_stop_white_24dp);
                    startgame = true;
                }
            }
        });
    }

    Runnable statusChecker = new Runnable() {
        @Override
        public void run() {
            try{
                new updateStatusAsync().execute();
            }finally {
                time++;
                handler.postDelayed(statusChecker, INTERVAL);
            }
        }
    };

    void startRepeatingTask(){
        statusChecker.run();
    }

    void stopRepeatingTask(){
        handler.removeCallbacks(statusChecker);
    }

    private class updateStatusAsync extends AsyncTask<Void, Void, Integer> {
        ArrayList<Beacon> beaconArrayList;
        Canvas canvas;
        double width;
        double height;

        @Override
        protected void onPreExecute() {
            height = appBarLayout.getHeight();
            width = appBarLayout.getWidth();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            canvas = new Canvas(bg);
            beaconArrayList = returnBeaconsFromWifi();
            for(Beacon beacon : beaconArrayList){
                String mac = beacon.getMac();
                String values[] = playerManagement.retrievePlayerSettings(mac);
                liveArrayList.add(new Player(beacon.getAvg(), beacon.getTotal(), values[2],
                        Integer.parseInt(values[1]), values[0], mac));
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), COLORS[0]);
                int drawX = (int) ((beacon.getPos_x()*width)/courtX);
                int drawY = (int) ((beacon.getPos_y()*height)/courtY);
                canvas.drawBitmap(bitmap, drawX, drawY, null);
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result){
            matchManagement.createMatchSession(beaconArrayList, time);
            listAdapter.notifyDataSetChanged();
            setToolbarMap();
        }
    }

    void setToolbarMap(){
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bg);
        appBarLayout.setBackground(drawable);
    }

    void creatingCourtBitmap(){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inScaled = true;
        opt.inMutable  = true;
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.court, opt);
    }


    //-------------------------------------------------------------------------------------------//
    //WIFI AND SHAREDPREFS PART
    ArrayList<Beacon> returnBeaconsFromWifi(){
        ArrayList<Beacon> beaconArrayList = new ArrayList<>();
        //METHOD
        return beaconArrayList;
    }

    void calculatePosition(){

    }

    public double yComponent(double l1, double l2, double base, double offset){
        double s = (base+l1+l2)/2;
        return ((Math.sqrt(s*(s-l1)*(s-l2)*(s-base))*2)/base) - offset;
    }
    //l1 = rx1, distancia do beacon ate a base 0,0
    //l2 = rx2, distancia do beacon ate a base 10,0
    //base = distancia entre as bases

    public double xComponent(double l1, double l2, double offset){
        return Math.sqrt(Math.pow(l1, 2) - Math.pow(l2, 2)) - offset;
    } //l1 = hipotenusa (Rx1) e l2 = componente y do ponto (altura)

    //-------------------------------------------------------------------------------------------//
    //ACTIVITY METHODS
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
            getSupportActionBar().setLogo(null);
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
