package com.amg.livestatistcs.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
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
    private static final String PREF_NAME = "Players"; //Nome do arquivo do SharedPref
    public static final String SETTINGS = "PlayerSettings"; //Configurações dos jogadores

    ArrayList<Player> playerArrayList;
    HashMap<String, String[]> playerSettings;
    Gson gson;

    public PlayerManagement(Context _context) {
        this._context = _context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void updatePlayerSettings(String name, String mac, String number, int colorpos){
        Type type = new TypeToken<List<Player>>(){}.getType(); //Pega tipo do configurações
        String players = pref.getString(SETTINGS, null); //Pega a string em json de configurações
        gson = new Gson();
        boolean found = false;

        if(!TextUtils.isEmpty(players)) {
            this.playerArrayList = gson.fromJson(players, type); //Transforma JSON em String
            for(int i = 0; i<this.playerArrayList.size(); i++){
                Player player = this.playerArrayList.get(i);
                if(player.getName().equals(name)){
                    player.setMac(mac);
                    player.setName(name);
                    player.setNumber(number);
                    player.setColorpos(colorpos);
                    this.playerArrayList.set(i, player);
                    found = true;
                    break;
                }
            }
            if(!found){
                Player player = new Player(0,0,number,colorpos,name,mac);
                this.playerArrayList.add(player);
            }
        }
        else{
            this.playerArrayList = new ArrayList<>();
            this.playerArrayList.add(new Player(0,0,number,colorpos,name,mac));
        }

        String jsonPlayer = gson.toJson(this.playerArrayList);
        Log.d("TAG","jsonPlayer = " + jsonPlayer);
        editor.putString(SETTINGS, jsonPlayer);
        editor.commit();
    }

    public void savePlayers(ArrayList<Player> players){
        this.playerArrayList = players;
        gson = new Gson();
        String jsonPlayer = gson.toJson(players);
        Log.d("TAG","jsonPlayer = " + jsonPlayer);
        editor.putString(SETTINGS, jsonPlayer);
        editor.commit();
    }

    public void fetchPlayerSettings(){
        Type type = new TypeToken<List<Player>>(){}.getType(); //Pega tipo do configurações
        String players = pref.getString(SETTINGS, null); //Pega a string em json de configurações
        gson = new Gson();

        if(!TextUtils.isEmpty(players)){
            this.playerArrayList = gson.fromJson(players, type); //Transforma JSON em String
            playerSettings = new HashMap<>();
            for(Player player : this.playerArrayList){
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