package com.amg.livestatistcs.model;

/**
 * Created by Lucas on 15/07/2016.
 */
public class Beacon {
    private String mac;
    private double pos_x;
    private double pos_y;
    private double total;
    private double avg;

    public Beacon(String mac, double pos_x, double pos_y) {
        this.mac = mac;
        this.pos_x = pos_x;
        this.pos_y = pos_y;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public double getPos_x() {
        return pos_x;
    }

    public void setPos_x(double pos_x) {
        this.pos_x = pos_x;
    }

    public double getPos_y() {
        return pos_y;
    }

    public void setPos_y(double pos_y) {
        this.pos_y = pos_y;
    }

    public double getTotal() { return total; }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }
}
