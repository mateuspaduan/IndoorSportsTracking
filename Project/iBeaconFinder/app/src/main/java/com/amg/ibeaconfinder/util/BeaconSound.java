package com.amg.ibeaconfinder.util;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.util.Timer;
import java.util.TimerTask;

public class BeaconSound {

    Timer timer = new Timer();
    public Context context;

    public BeaconSound(int seconds){

        timer.schedule(new NotificationTask(), 0, seconds);
    }

    class NotificationTask extends TimerTask{

        public void run(){

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        }
    }
}
