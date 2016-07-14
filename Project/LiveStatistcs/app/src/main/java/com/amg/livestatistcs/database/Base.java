package com.amg.livestatistcs.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Base extends SQLiteOpenHelper{

    //nome do banco
    public static final String DB_NAME = "ISTdb";

    //versão
    public static final int SCHEMA_VERSION = 1;

    //nome da tabela
    public static final String GAME_TABLE = "GameInfo";

    public static final int color = 0;
    public static final int nGames = 0;
    public static final double totalRun = 0;
    public static final double avgRun = 0;
    public static final double timeGame = 0;

    //campos da tabela
    public static final String FIELD_ID = "_id";
    public static final String MAC = "mac";
    public static final String PLAYER_NAME = "name";
    public static final String PLAYER_NUMBER = "number";
    public static final int COLOR_POSITION = color;
    public static final int NUMBER_GAMES = nGames;
    public static final double TOTAL_RUN = totalRun;
    public static final double AVERAGE_RUN = avgRun;
    public static final double TIME_GAME = timeGame;

    private static final String CREATE_TABLE = "CREATE TABLE "
            + GAME_TABLE + " (" + FIELD_ID + " INTEGER PRIMARY KEY, "
            + MAC + " TEXT, "
            + PLAYER_NAME + " TEXT, "
            + PLAYER_NUMBER + " TEXT, "
            + COLOR_POSITION + " NUMBER, "
            + NUMBER_GAMES + " NUMBER, "
            + TOTAL_RUN + " NUMBER, "
            + AVERAGE_RUN + " NUMBER, "
            + TIME_GAME + " NUMBER)";

    public Base(Context context) {
        super(context, DB_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
