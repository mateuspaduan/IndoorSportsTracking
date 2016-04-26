package com.amg.ibeaconfinder.util;

public class BeaconNotification{

    BeaconSound beaconSound;

    public double calculateAccuracy(int txPower, double rssi){

        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }

        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    public void calculateProximity(double accuracy) {

        if (accuracy < 0) {
            //Shows a Toast informing that the beacon is out of range
        }

        if (accuracy < 0.5 ) {
            //return IBeacon.PROXIMITY_IMMEDIATE;
            new BeaconSound(1);
        }

        if (accuracy <= 4.0) {
            //return IBeacon.PROXIMITY_NEAR;
            new BeaconSound(3);
        }
        //if it is > 4.0 meters, call it far
        //return IBeacon.PROXIMITY_FAR;
        new BeaconSound(5);
    }
}
