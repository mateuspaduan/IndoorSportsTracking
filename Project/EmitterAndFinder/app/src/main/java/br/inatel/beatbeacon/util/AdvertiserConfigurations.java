package br.inatel.beatbeacon.util;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.os.ParcelUuid;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by lucas on 15/09/2016.
 */
public interface AdvertiserConfigurations {
    ParcelUuid PARCEL_UUID = new ParcelUuid(UUID.fromString("CDB7950D-73F1-4D4D-8E47-C090502DBD63"));

    AdvertiseData ADVERTISE_DATA =
        new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(true)
                .addServiceUuid(PARCEL_UUID)
                .addServiceData(PARCEL_UUID, "Data".getBytes(Charset.forName("UTF-8")))
                .build();



    AdvertiseSettings ADVERTISE_SETTINGS =
        new AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setConnectable(false)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build();

    AdvertiseCallback ADVERTISE_CALLBACK = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.v("AdvertiseSucess", settingsInEffect.toString());
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
                super.onStartFailure(errorCode);
            }
        };


    //public AdvertiseData getAdvertiseData(){ return ADVERTISE_DATA; }
    //public AdvertiseCallback getAdvertiseCallback(){ return advertiseCallback; }
    //public AdvertiseSettings getAdvertiseSettings() { return ADVERTISE_SETTINGS; }
}
