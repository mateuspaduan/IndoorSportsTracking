package com.amg.livestatistcs.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amg.livestatistcs.R;

public class MainActivity extends AppCompatActivity {
    Button StartgameButton, SettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartgameButton = (Button) findViewById(R.id.startgame_bt);
        SettingsButton = (Button) findViewById(R.id.settings_bt);

        StartgameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LiveActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Fechando todas as activities
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Adicionando flag pra iniciar outra activity
                getApplicationContext().startActivity(i);
            }
        });

        SettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Fechando todas as activities
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Adicionando flag pra iniciar outra activity
                getApplicationContext().startActivity(i);
            }
        });
    }
}
