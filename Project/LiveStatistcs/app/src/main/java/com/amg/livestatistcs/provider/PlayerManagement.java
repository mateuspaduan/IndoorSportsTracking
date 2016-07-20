package com.amg.livestatistcs.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amg.livestatistcs.model.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lucas on 19/07/2016.
 */
public class PlayerManagement {
    SharedPreferences pref; //Criando SharedPreferences
    SharedPreferences.Editor editor; //Criando o editor do SharedPreferences
    Context _context; //Criando o context

    int PRIVATE_MODE = 0; //Setando o mode do SharedPreferences para private
    private static final String PREF_NAME = "Settings"; //Nome do arquivo do SharedPref
    public static final String SETTINGS = "PlayerSettings"; //Configurações dos jogadores

    ArrayList<Player> players;
    HashMap<String, String[]> playerSettings;
    Gson gson;

    public PlayerManagement(Context _context) {
        this._context = _context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void savePlayerSettings(ArrayList<Player> players){
        this.players = players;
        gson = new Gson();
        String jsonPlayer = gson.toJson(players);
        Log.d("TAG","jsonPlayer = " + jsonPlayer);
        editor.putString(SETTINGS, jsonPlayer);
        editor.commit();
    }

    public void fetchPlayerSettings(){
        Type type = new TypeToken<List<Player>>(){}.getType(); //Pega tipo do configurações
        String players = pref.getString(SETTINGS, null); //Pega a string em json de configurações

        if(players != null){
            this.players = gson.fromJson(players, type); //Transforma JSON em String
            playerSettings = new HashMap<>();
            for(Player player : this.players){
                String[] values = new String[3];
                values[0] = player.getName();
                values[1] = Integer.toString(player.getColorpos());
                values[2] = player.getNumber();
                playerSettings.put(player.getMac(), values);
            }
        }
    }

    public String[] retrievePlayerSettings(String mac){
        return playerSettings.get(mac);
    }
}