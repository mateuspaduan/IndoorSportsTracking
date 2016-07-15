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
public class MatchManagement {
    SharedPreferences pref; //Criando SharedPreferences
    SharedPreferences.Editor editor; //Criando o editor do SharedPreferences
    Context _context; //Criando o context
    Gson gson;

    int PRIVATE_MODE = 0; //Setando o mode do SharedPreferences para private
    private static final String PREF_NAME = "CurrentMatch"; //Nome do arquivo do SharedPref
    public static final String CONFIGURED = "SetPlayers";
    public static final String BEACONS = "Beacons"; //MAC do beacon

    // Constructor
    public MatchManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Metodo que cria uma sessao da partida, armazenando os dados de cada beacon
    public void createMatchSession(ArrayList<Beacon> beacons, double time){
        gson = new Gson();
        String jsonBeacons = gson.toJson(beacons); //Transformando lista de jogadores em JSON
        Log.d("TAG","jsonBeacons = " + jsonBeacons);
        editor.putBoolean(CONFIGURED, true); //Lista de jogadores já configurados
        editor.putString(BEACONS, jsonBeacons); //Salvando jogadores no SharedPrefs
        editor.commit(); //Commita mudanças no SharedPrefs
    }

    public ArrayList<Beacon> returnBeacons(){
        Type type = new TypeToken<List<Beacon>>(){}.getType(); //Pega tipo do beacon
        String beacons = pref.getString(BEACONS, null); //Pega a string em json de beacons
        ArrayList<Beacon> beaconList = gson.fromJson(beacons, type); //Transforma JSON em Object
        return beaconList; //Retorna a lista de beacons
    }

    //Metodo para checar se está configurado
    public boolean isConfigured(){
        return pref.getBoolean(CONFIGURED, false);
    }

    //Limpar Shared Prefs depois que terminar a partida
    public void erasePrefs(){
        editor.clear();
        editor.commit();
    }
}
