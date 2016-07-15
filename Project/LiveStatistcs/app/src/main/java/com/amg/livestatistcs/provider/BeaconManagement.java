package com.amg.livestatistcs.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amg.livestatistcs.model.Beacon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 15/07/2016.
 */
public class BeaconManagement {
    SharedPreferences pref; //Criando SharedPreferences
    SharedPreferences.Editor editor; //Criando o editor do SharedPreferences
    Context _context; //Criando o context
    Gson gson;

    int PRIVATE_MODE = 0; //Setando o mode do SharedPreferences para private
    private static final String PREF_NAME = "Beacons"; //Nome do arquivo do SharedPref
    public static final String BEACONS = "Beacons"; //MAC do beacon

    public BeaconManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Metodo que cadastra MAC dos beacons
    public void registerBeacons (ArrayList<Beacon> beacons){
        gson = new Gson();

        ArrayList<String> newMacs = new ArrayList<>();
        for(Beacon beacon : beacons) newMacs.add(beacon.getMac());

        Type type = new TypeToken<List<String>>(){}.getType(); //Pega tipo do beacon
        String macs = pref.getString(BEACONS, null); //Pega a string em json de beacons
        ArrayList<String> macsList = gson.fromJson(macs, type); //Transforma JSON em String

        boolean flag = true;
        for(String newmac : newMacs){
            for(String mac : macsList){
                if(newmac.equals(mac)){
                    flag = false;
                    break;
                }
            }
            if(flag) macsList.add(newmac);
            flag = true;
        }

        String jsonMac = gson.toJson(macsList); //Transformando lista de jogadores em JSON
        Log.d("TAG","jsonMac = " + jsonMac);

        editor.putString(BEACONS, jsonMac); //Salvando jogadores no SharedPrefs
        editor.commit(); //Commita mudanças no SharedPrefs
    }

    //Retorna lista de MAC de beacons
    public ArrayList<String> returnMacs(){
        Type type = new TypeToken<List<String>>(){}.getType(); //Pega tipo do beacon
        String macs = pref.getString(BEACONS, null); //Pega a string em json de beacons
        ArrayList<String> macsList = gson.fromJson(macs, type); //Transforma JSON em String
        return macsList; //Retorna a lista de beacons
    }
}
