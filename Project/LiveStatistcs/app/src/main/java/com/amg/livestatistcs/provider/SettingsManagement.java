package com.amg.livestatistcs.provider;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by Lucas on 18/07/2016.
 */
public class SettingsManagement {
    SharedPreferences pref; //Criando SharedPreferences
    SharedPreferences.Editor editor; //Criando o editor do SharedPreferences
    Context _context; //Criando o context

    int PRIVATE_MODE = 0; //Setando o mode do SharedPreferences para private
    private static final String PREF_NAME = "Settings"; //Nome do arquivo do SharedPref
    public static final String XDISTANCE = "X_Distance"; //Distancia em X da quadra
    public static final String YDISTANCE = "Y_Distance"; //Distancia em Y da quadra
    public static final String XCOURT = "X_Court"; //Tamanho quadra em X
    public static final String YCOURT = "Y_Court"; //Tamanho quadra em Y

    public SettingsManagement(Context _context) {
        this._context = _context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveSettings(float xd, float yd, float xc, float yc){
        editor.putFloat(XDISTANCE, xd);
        editor.putFloat(YDISTANCE, yd);
        editor.putFloat(XCOURT, xc);
        editor.putFloat(YCOURT, yc);
        editor.commit();
    }

    public float[] retrieveDistanceFromCourt(){
        float[] values;
        values = new float[2];
        values[0] = pref.getFloat(XDISTANCE, 0);
        values[1] = pref.getFloat(YDISTANCE, 0);
        return  values;
    }

    public float[] retrieveDimensions(){
        float[] values;
        values = new float[2];
        values[0] = pref.getFloat(XCOURT, 0);
        values[1] = pref.getFloat(YCOURT, 0);
        return  values;
    }
}
