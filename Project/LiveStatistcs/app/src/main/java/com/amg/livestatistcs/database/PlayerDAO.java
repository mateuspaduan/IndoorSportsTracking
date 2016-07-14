package com.amg.livestatistcs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.amg.livestatistcs.model.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 7/14/2016.
 */

public class PlayerDAO {

    private SQLiteDatabase database;
    private Base base;

    public PlayerDAO(Context context) {
        base = new Base(context);
    }

    public void open() throws SQLException {
        database = base.getWritableDatabase();
    }

    public long newPlayer(Player player) {

        ContentValues values = new ContentValues();
        values.put(Base.MAC, player.getMac());
        values.put(Base.PLAYER_NAME, player.getName());
        values.put(Base.PLAYER_NUMBER, player.getNumber());
        values.put(String.valueOf(Base.COLOR_POSITION), player.getColorpos());
        values.put(String.valueOf(Base.NUMBER_GAMES), player.getGames());
        values.put(String.valueOf(Base.TOTAL_RUN), player.getTotalrun());
        values.put(String.valueOf(Base.AVERAGE_RUN), player.getAvgrun());
        values.put(String.valueOf(Base.TIME_GAME), player.getTimegame());

        return database.insert(Base.GAME_TABLE, null, values);
    }

    public List<Player> readPlayers() {

        Cursor c = database.rawQuery("SELECT * FROM " + Base.GAME_TABLE, null);
        List<Player> players = new ArrayList<Player>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Player player = new Player(
                        c.getDouble(c.getColumnIndex(String.valueOf(Base.TIME_GAME))),
                        c.getDouble(c.getColumnIndex(String.valueOf(Base.AVERAGE_RUN))),
                        c.getDouble(c.getColumnIndex(String.valueOf(Base.TOTAL_RUN))),
                        c.getString(c.getColumnIndex(Base.PLAYER_NUMBER)),
                        c.getInt(c.getColumnIndex(String.valueOf(Base.NUMBER_GAMES))),
                        c.getInt(c.getColumnIndex(String.valueOf(Base.COLOR_POSITION))),
                        c.getString(c.getColumnIndex(Base.PLAYER_NAME)),
                        c.getString(c.getColumnIndex(Base.MAC)));
                players.add(player);
                c.moveToNext();
            }
        }
        c.close();
        return players;
    }

    public int updatePlayers(Player player){

        String mac = player.getMac();
        ContentValues values = new ContentValues();

        values.put(Base.PLAYER_NAME, player.getName());
        values.put(Base.PLAYER_NUMBER, player.getNumber());
        values.put(String.valueOf(Base.COLOR_POSITION), player.getColorpos());
        values.put(String.valueOf(Base.NUMBER_GAMES), player.getGames());
        values.put(String.valueOf(Base.TOTAL_RUN), player.getTotalrun());
        values.put(String.valueOf(Base.AVERAGE_RUN), player.getAvgrun());
        values.put(String.valueOf(Base.TIME_GAME), player.getTimegame());

        return database.update(Base.GAME_TABLE, values, Base.FIELD_ID + " = " + mac, null);
    }

    public void deletePlayer(Player player){

        String mac = player.getMac();
        database.delete(Base.GAME_TABLE, Base.FIELD_ID + " = " + mac, null);
    }
}


