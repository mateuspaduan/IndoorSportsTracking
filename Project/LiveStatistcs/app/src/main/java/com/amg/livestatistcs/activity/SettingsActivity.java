package com.amg.livestatistcs.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.amg.livestatistcs.R;
import com.amg.livestatistcs.adapter.SettingsListAdapter;
import com.amg.livestatistcs.model.Player;
import com.amg.livestatistcs.provider.BeaconManagement;
import com.amg.livestatistcs.provider.PlayerManagement;
import com.amg.livestatistcs.provider.SettingsManagement;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    EditText XComponent, YComponent, XSize, YSize;
    ListView SettingsListView;
    PlayerManagement pManagement;
    SettingsManagement sManagement;
    BeaconManagement bManagement;
    SettingsListAdapter settingsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingsListView = (ListView) findViewById(R.id.settings_list);

        View header = getLayoutInflater().inflate(R.layout.content_settings, null);

        sManagement = new SettingsManagement(getApplicationContext());
        pManagement = new PlayerManagement(getApplicationContext());
        bManagement = new BeaconManagement(getApplicationContext());
        float values[] = sManagement.retrieveDimensions();
        ((EditText) header.findViewById(R.id.sizex_et)).setHint("Tamanho atual: " + Float.toString(values[0]));
        ((EditText) header.findViewById(R.id.sizey_et)).setHint("Tamanho atual: " + Float.toString(values[1]));
        values = sManagement.retrieveDistanceFromCourt();
        ((EditText) header.findViewById(R.id.x_et)).setHint("Distância atual: " + Float.toString(values[0]));
        ((EditText) header.findViewById(R.id.y_et)).setHint("Distância atual: " + Float.toString(values[1]));

        settingsListAdapter = new SettingsListAdapter(populateList(), this);
        SettingsListView.addHeaderView(header);
        SettingsListView.setItemsCanFocus(true);
        SettingsListView.setAdapter(settingsListAdapter);
    }

    public ArrayList<Player> populateList(){
        ArrayList<Player> list = new ArrayList<>();
        ArrayList<String> macs = bManagement.returnMacs();
        for(String mac : macs){
            String[] values = pManagement.retrievePlayerSettings(mac);
            list.add(new Player(values[0], Integer.parseInt(values[1]), values[2], mac));
        }
        return list;
    }

    public void updatePlayers(){
        View view = SettingsListView.getAdapter().getView(0, null, null);
        EditText view_xoffset = (EditText) view.findViewById(R.id.x_et);
        EditText view_yoffset = (EditText) view.findViewById(R.id.y_et);
        EditText view_xsize = (EditText) view.findViewById(R.id.sizex_et);
        EditText view_ysize = (EditText) view.findViewById(R.id.sizey_et);

        if(view_xoffset.length() > 0 && view_yoffset.length() > 0 &&
                view_xsize.length() > 0 && view_ysize.length() > 0){
            sManagement.saveSettings(Float.parseFloat(view_xoffset.getText().toString()),
                    Float.parseFloat(view_yoffset.getText().toString()),
                    Float.parseFloat(view_xsize.getText().toString()),
                    Float.parseFloat(view_ysize.getText().toString()));
            Toast.makeText(getApplicationContext(), "Configurações salvas com sucesso", Toast.LENGTH_LONG).show();
        }

        for(int i=1; i<settingsListAdapter.getCount(); i++){
            Player player = settingsListAdapter.getItem(i-1);
            view = settingsListAdapter.getView(i, null, null);
            EditText name = (EditText) view.findViewById(R.id.name);
            EditText number = (EditText) view.findViewById(R.id.number_et);
            EditText mac = (EditText) view.findViewById(R.id.mac_et);
            Spinner color = (Spinner) view.findViewById(R.id.color_spinner);

            String nameSave = (name.getText() != null) ? name.getText().toString() : player.getName();
            String numberSave = (number.getText() != null) ? number.getText().toString() : player.getNumber();
            String macSave = (mac.getText() != null) ? mac.getText().toString() : player.getMac();
            int colorSave = color.getSelectedItemPosition();

            if(!TextUtils.isEmpty(nameSave) && !TextUtils.isEmpty(macSave) && !TextUtils.isEmpty(numberSave))
                pManagement.updatePlayerSettings(nameSave, macSave, numberSave, colorSave);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updatePlayers();
    }
}
