package com.amg.ibeaconfinder.util;

public class BeaconNotification{

    public BeaconSound beaconSound;

    public double calculateAccuracy(int txPower, double rssi) {

        if (rssi == 0)
            return -1.0; // if we cannot determine accuracy, return -1.

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            new BeaconSound(3);
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            new BeaconSound(1);
            return accuracy;
        }
    }

    public void notificate(double accuracy){

        if (accuracy < 1) new BeaconSound(1);
        else if (accuracy < 3) new BeaconSound(3);
        else new BeaconSound(6);
    }
}

