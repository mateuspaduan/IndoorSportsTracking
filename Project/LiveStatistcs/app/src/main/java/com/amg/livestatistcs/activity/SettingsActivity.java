package com.amg.livestatistcs.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.amg.livestatistcs.R;
import com.amg.livestatistcs.adapter.SettingsListAdapter;
import com.amg.livestatistcs.model.Player;
import com.amg.livestatistcs.provider.SettingsManagement;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    EditText XComponent, YComponent, XSize, YSize;
    ListView SettingsListView;
    Button SaveSettings;
    SettingsManagement sManagement;
    SettingsListAdapter settingsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        XComponent = (EditText) findViewById(R.id.x_et);
        YComponent = (EditText) findViewById(R.id.y_et);
        XSize = (EditText) findViewById(R.id.sizex_et);
        YSize = (EditText) findViewById(R.id.sizey_et);
        SettingsListView = (ListView) findViewById(R.id.settings_list);

        sManagement = new SettingsManagement(getApplicationContext());
        float values[] = sManagement.retrieveDimensions();
        XSize.setHint("Tamanho atual: " + Float.toString(values[0]));
        YSize.setHint("Tamanho atual: " + Float.toString(values[1]));
        values = sManagement.retrieveDistanceFromCourt();
        XComponent.setHint("Distância atual: " + Float.toString(values[0]));
        YComponent.setHint("Distância atual: " + Float.toString(values[1]));

        View header = getLayoutInflater().inflate(R.layout.content_settings, null);

        settingsListAdapter = new SettingsListAdapter(populateList(), this);
        SettingsListView.setAdapter(settingsListAdapter);
        SettingsListView.addHeaderView(header);
    }

    ArrayList<Player> populateList(){
        ArrayList<Player> list = new ArrayList<>();
        list.add(new Player(18, 150, "10", 4, "Lucas Selani", "89:AB:T3:AR:P9:23"));
        list.add(new Player(14, 244, "42", 1, "Pablo Bochi", "89:AB:T3:AR:P9:23"));
        list.add(new Player(21, 135, "34", 2, "Mateus Paduan", "89:AB:T3:AR:P9:23"));
        list.add(new Player(33, 188, "06", 6, "Leonardo Saldanha", "89:AB:T3:AR:P9:23"));
        list.add(new Player(5, 89, "99", 3, "Gabriel Bino", "89:AB:T3:AR:P9:23"));
        return list;
    }

    @Override
    protected void onPause() {
        super.onPause();

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
    }
}
