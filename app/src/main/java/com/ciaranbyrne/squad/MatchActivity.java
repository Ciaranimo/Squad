package com.ciaranbyrne.squad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ciaranbyrne.squad.R.layout.activity_match);

        //create button to save match details
        Button btnSaveMatchDetails = (Button)findViewById(com.ciaranbyrne.squad.R.id.btnSaveMatch);
        //set on click listener
        btnSaveMatchDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        //button to edit players
        //create button
        Button btnEditPlayers = (Button)findViewById(com.ciaranbyrne.squad.R.id.btnEditPlayers);
        //set on click listener
        btnEditPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),EditPlayersActivity.class);
                startActivity(i);
            }
        });
    }
}
