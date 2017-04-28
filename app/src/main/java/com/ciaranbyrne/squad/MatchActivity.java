package com.ciaranbyrne.squad;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.ciaranbyrne.squad.R.id.daySpinner;
import static com.ciaranbyrne.squad.R.id.timeSpinner;
import static com.ciaranbyrne.squad.R.id.tvDay;


public class MatchActivity extends AppCompatActivity {
    static final String TAG = "MatchActivity";

    //Firebase database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference groupsDatabase;
    private DatabaseReference matchesDatabase;
    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;
    private ChildEventListener mChildEventListener;

    //instance variables
    private Spinner timeSpin;
    private Spinner daySpin;
    private TextView tvResultPlayerNum;
    private TextView tvDays;
    private TextView tvTimes;
    private SeekBar seekBarPlayersNum;
    private Switch switchWeekly;
    private Switch switchEvenTeams;
    private Button btnEditPlayers;
    private Button btnSaveMatch;

    private String[] days;
    private String[] times;

    private ArrayAdapter adapterDaysAutoComplete;
    private ArrayAdapter adapterDaysSpinner;
    private ArrayAdapter adapterTimesAutoComplete;
    private ArrayAdapter adapterTimesSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ciaranbyrne.squad.R.layout.activity_match);

        //GET CURRENT USER INFO
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        // Adding users to groups - Setting Group ID to be the same as User Id
        final String groupId = firebaseUser.getUid();

        //Initialize Variables
        seekBarPlayersNum = (SeekBar) findViewById(R.id.seekBarPlayers);
        btnEditPlayers = (Button) findViewById(com.ciaranbyrne.squad.R.id.btnEditPlayers);
        btnSaveMatch = (Button) findViewById(com.ciaranbyrne.squad.R.id.btnSaveMatch);
        switchEvenTeams = (Switch) findViewById(R.id.switchEvenTeams);
        switchWeekly = (Switch) findViewById(R.id.switchWeekly);
        tvResultPlayerNum = (TextView) findViewById(R.id.tvResultPlayerNum);
        tvDays = (TextView) findViewById(tvDay);
        tvTimes = (TextView) findViewById(R.id.tvTime);

        // get database reference to read data
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        groupsDatabase = mFirebaseDatabase.getReference().child("groups");
        matchesDatabase = mFirebaseDatabase.getReference().child("groups").child(groupId).child("matches");
        mDatabase = mFirebaseDatabase.getReference();

        //instantiate arrays for days and times spinners
        days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        times = new String[]{"11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00"};

        //Array Adapters for Spinners & AutoComplete
        adapterDaysAutoComplete = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapterDaysSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapterTimesAutoComplete = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
        adapterTimesSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);

        // Time Spinner for selection
        timeSpin = (Spinner) findViewById(timeSpinner);
        timeSpin.setAdapter((adapterTimesSpinner));

        timeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Spinner time selected: \n" + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        timeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = timeSpin.getSelectedItem().toString();
                tvTimes.setText("Time: " + str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Day Spinner
        daySpin = (Spinner) findViewById(daySpinner);
        daySpin.setAdapter(adapterDaysSpinner);

        daySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Spinner day selected: \n" + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Day Spinner
        daySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = daySpin.getSelectedItem().toString();
                tvDays.setText("Day: " + str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // End Spinners

        // Seekbar
        seekBarPlayersNum = (SeekBar) findViewById(R.id.seekBarPlayers);
        seekBarPlayersNum.setMax(22);
        seekBarPlayersNum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvResultPlayerNum.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        }); // END SEEKBAR

        //Switches //TODO
        //set the switch to ON
        switchWeekly.setChecked(true);
        //attach a listener to check for changes in state
        switchWeekly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isWeeklyChecked) {
                Snackbar.make(compoundButton, "Weekly: " + isWeeklyChecked, Snackbar.LENGTH_LONG)
                        .setAction("ACTION", null).show();
            }
        });

        switchEvenTeams.setChecked(true);
        switchEvenTeams.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isEvenChecked) {
                Snackbar.make(compoundButton, "Even Teams: " + isEvenChecked, Snackbar.LENGTH_LONG)
                        .setAction("ACTION", null).show();
            }
        });

        //create button to save match details
        //set on click listener
        btnSaveMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            saveMatchDetails(groupId);

            }
        });

        //button to edit players
        //set on click listener
        btnEditPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditPlayersActivity.class);
                startActivity(i);
            }
        });


    }// end onCreate

    private void saveMatchDetails(String groupId) {
        String matchDay = tvDays.getText().toString();
        String matchTime = tvTimes.getText().toString();
        int matchNumber = Integer.parseInt(tvResultPlayerNum.getText().toString());
        Boolean isEvenChecked = switchEvenTeams.isChecked();
        Boolean isWeeklyChecked = switchWeekly.isChecked();

        Match match = new Match(matchTime, matchDay, matchNumber, isEvenChecked, isWeeklyChecked,groupId);

        matchesDatabase.setValue(match);

        Toast.makeText(this, "Match details added", Toast.LENGTH_SHORT).show();
    }

}
