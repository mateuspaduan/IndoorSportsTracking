package com.amg.livestatistcs.model;

/**
 * Created by Lucas on 13/07/2016.
 */
public class Player {
    private String mac; //MAC do beacon associado ao jogador
    private String name; //Nome do jogador
    private String number; //Numero da camisa
    private int colorpos; //Posição do res/color para a cor do jogador
    private int games; //Jogos jogados
    private double totalrun; //Total corrido
    private double avgrun; //Media corrido
    private double timegame; //Tempo total jogado

    public Player(double timegame, double avgrun, double totalrun, String number, int games, int colorpos, String name, String mac) {
        this.timegame = timegame;
        this.avgrun = avgrun;
        this.totalrun = totalrun;
        this.number = number;
        this.games = games;
        this.colorpos = colorpos;
        this.name = name;
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public double getTimegame() {
        return timegame;
    }

    public void setTimegame(double timegame) {
        this.timegame = timegame;
    }

    public double getAvgrun() {
        return avgrun;
    }

    public void setAvgrun(double avgrun) {
        this.avgrun = avgrun;
    }

    public double getTotalrun() {
        return totalrun;
    }

    public void setTotalrun(double totalrun) {
        this.totalrun = totalrun;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getColorpos() {
        return colorpos;
    }

    public void setColorpos(int colorpos) {
        this.colorpos = colorpos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
