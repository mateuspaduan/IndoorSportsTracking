package com.amg.livestatistcs.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.amg.livestatistcs.R;
import com.amg.livestatistcs.adapter.LiveListAdapter;
import com.amg.livestatistcs.alljoyn.*;
import com.amg.livestatistcs.model.Beacon;
import com.amg.livestatistcs.model.Player;
import com.amg.livestatistcs.provider.MatchManagement;
import com.amg.livestatistcs.provider.PlayerManagement;
import com.amg.livestatistcs.provider.SettingsManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LiveActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private AppBarLayout appBarLayout;
    private Bitmap bg;
    private ArrayList<Player> liveArrayList;
    private ArrayList<Beacon> liveBeaconList;
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
    private double distanceX;
    private double distanceY;
    private int time = 0;
    private final int INTERVAL = 500; //500ms
    private final int COLORS[] = {R.drawable.brown_400, R.drawable.dpurple_300, R.drawable.gray_500,
                                  R.drawable.green_a400, R.drawable.indigo_500, R.drawable.lblue_a100,
                                  R.drawable.lgreen_a200, R.drawable.orange_800, R.drawable.pink_300,
                                  R.drawable.red_500, R.drawable.teal_a200, R.drawable.yellow_500};
    private final String TAG = "LiveActivity";
    private Handler mBusHandler;

    static {
        System.loadLibrary("alljoyn_java");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_PING:
                    Util.logInfo(TAG, (String) msg.obj);
                    break;
                case Constants.MESSAGE_PING_REPLY:
                    Util.logInfo(TAG, (String) msg.obj);
                    break;
                case Constants.MESSAGE_POST_TOAST:
                    Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case Constants.FINISH:
                    finish();
                    break;
                case Constants.MESSAGE_SIGNAL:
                    String message = (String) msg.obj;
                    //TODO
                default:
                    break;
            }
        }
    };

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
        setToolbarMap();

        //ListView settings
        liveBeaconList = new ArrayList<>();
        liveArrayList = new ArrayList<>();
        liveView = (ListView) findViewById(R.id.live_list);
        listAdapter = new LiveListAdapter(liveArrayList, this);
        liveView.setAdapter(listAdapter);

        //Retrieving court dimensions
        settingsManagement = new SettingsManagement(this);
        float values[] = settingsManagement.retrieveDimensions();
        courtX = values[0];
        courtY = values[1];
        values = settingsManagement.retrieveDistanceFromCourt();
        distanceX = values[0];
        distanceY = values[1];

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

                    /* Make all AllJoyn calls through a separate handler thread to prevent blocking the UI. */
                    HandlerThread busThread = new HandlerThread("AllJoynBusHandler");
                    busThread.start();
                    mBusHandler = new AllJoynBusHandler(busThread.getLooper(),
                            getApplicationContext(), getPackageName(), mHandler);
                    mBusHandler.sendEmptyMessage(Constants.CONNECT);
                }
                else{
                    stopRepeatingTask();
                    fab.setImageResource(R.drawable.ic_stop_white_24dp);
                    startgame = true;
                    mBusHandler.sendEmptyMessage(Constants.DISCONNECT);
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
            try {
                readTxtBeacons();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(Beacon beacon : liveBeaconList){
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
            matchManagement.createMatchSession(liveBeaconList, time);
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
    private void readTxtBeacons() throws IOException {
        BufferedReader brFinder1 = null, brFinder2 = null;
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            String lineFinder1, lineFinder2;

            File file1 = new File(sdcard,"finder1.txt");
            File file2 = new File(sdcard,"finder2.txt");
            brFinder1 = new BufferedReader(new FileReader(file1));
            brFinder2 = new BufferedReader(new FileReader(file2));

            lineFinder1 = brFinder1.readLine();
            lineFinder2 = brFinder2.readLine();
            while (lineFinder1 != null || lineFinder2 != null) {
                Log.i("txtBeacons", "textFinder1: "+lineFinder1+" : end");
                Log.i("txtBeacons", "textFinder2: "+lineFinder2+" : end");

                boolean result = convertTxtToBeacon(lineFinder1, lineFinder2);

                String oldLineFinder1 = lineFinder1;
                String oldLineFinder2 = lineFinder2;
                lineFinder1 = brFinder1.readLine();
                lineFinder2 = brFinder2.readLine();

                if(!result){
                    result = convertTxtToBeacon(oldLineFinder1, lineFinder2);
                    if(!result) {
                        result = convertTxtToBeacon(lineFinder1, oldLineFinder2);
                        if(!result) result = convertTxtToBeacon(lineFinder1, lineFinder2);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(brFinder1 != null) brFinder1.close();
            if(brFinder2 != null) brFinder2.close();
        }
    }

    private boolean convertTxtToBeacon(String finder1, String finder2){
        String[] linesFinder1 = finder1.split(System.getProperty(","));
        String[] linesFinder2 = finder2.split(System.getProperty(","));
        double[] values;

        //int pos1 = Integer.parseInt(linesFinder1[0]);
        //int pos2 = Integer.parseInt(linesFinder2[0]);
        //if(pos1 == pos2)

        if(linesFinder1[1].equals(linesFinder2[1])){
            values = calculatePosition(linesFinder1[2], linesFinder2[2]);
            liveBeaconList.add(new Beacon(linesFinder1[1], values[0], values[1]));
            return true;
        }
        else return false;
    }

    double[] calculatePosition(String finder1, String finder2){
        int distance1 = Integer.parseInt(finder1);
        int distance2 = Integer.parseInt(finder2);
        double y = yComponent(distance1, distance2);
        double x = xComponent(distance1, y);
        double[] values = new double[2];
        values[0] = x;
        values[1] = y;
        return values;
    }

    public double yComponent(double l1, double l2){ //Altura do triangulo
        double s = (courtX+l1+l2)/2;
        return ((Math.sqrt(s*(s-l1)*(s-l2)*(s-courtX))*2)/courtX) - distanceY;
    }
    //l1 = rx1, distancia do beacon ate a base 0,0
    //l2 = rx2, distancia do beacon ate a base 10,0

    public double xComponent(double l1, double l2){
        return Math.sqrt(Math.pow(l1, 2) - Math.pow(l2, 2)) - distanceX;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* Disconnect to prevent any resource leaks. */
        mBusHandler.sendEmptyMessage(Constants.DISCONNECT);
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
