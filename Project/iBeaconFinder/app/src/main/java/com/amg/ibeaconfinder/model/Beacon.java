package com.amg.ibeaconfinder.model;

/**
 * Created by Lucas on 24/04/2016.
 */
public class Beacon {
    private String uuid;
    private String minor;
    private String major;
    private String rssi;
    private String distance;
    private double realDistance;
    private String macAddress;

    public String getMacAddress() { return macAddress;  }

    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public double getRealDistance() { return realDistance; }

    public void setRealDistance(double realDistance) { this.realDistance = realDistance; }

    public double calculateAccuracy(int txPower, double rssi) {

        if (rssi == 0)
            return -1.0;

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0)
            return Math.pow(ratio, 10);

        else
            return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
    }
}
