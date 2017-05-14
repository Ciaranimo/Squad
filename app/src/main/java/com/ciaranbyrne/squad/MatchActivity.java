package com.ciaranbyrne.squad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    private DatabaseReference usersDatabase;
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

    private TextView tvHeadingPlayersNum;
    private Button btnEditPlayers;
    private Button btnSaveMatch;

    private String[] days;
    private String[] times;

    private ArrayAdapter adapterDaysAutoComplete;
    private ArrayAdapter adapterDaysSpinner;
    private ArrayAdapter adapterTimesAutoComplete;
    private ArrayAdapter adapterTimesSpinner;
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    private String mUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ciaranbyrne.squad.R.layout.activity_match);

        //GET CURRENT USER INFO
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        final FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        // Adding users to groups - Setting Group ID to be the same as User Id
        final String groupId = firebaseUser.getUid();



        //Initialize Variables
        btnEditPlayers = (Button) findViewById(com.ciaranbyrne.squad.R.id.btnEditPlayers);
        btnSaveMatch = (Button) findViewById(com.ciaranbyrne.squad.R.id.btnSaveMatch);
        tvResultPlayerNum = (TextView) findViewById(R.id.tvResultPlayerNum);
        tvHeadingPlayersNum = (TextView)findViewById(R.id.tvHeadingNumOfPlayers);
        tvDays = (TextView) findViewById(tvDay);
        tvTimes = (TextView) findViewById(R.id.tvTime);

        // get database reference to read data
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        groupsDatabase = mFirebaseDatabase.getReference().child("groups");
        matchesDatabase = mFirebaseDatabase.getReference().child("groups").child(groupId).child("matches");
        mDatabase = mFirebaseDatabase.getReference();
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users");


        groupsDatabase.child(firebaseUser.getUid()).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count =dataSnapshot.getChildrenCount();

                String l = String.valueOf(count);
                Log.d("COUNT", l);

                if((count == 0) || (dataSnapshot.getValue() == null)) {
                    Log.d("Null","Null");
                    //  tvPlayerCount.setText("");

                }else {

                    if (count > 0) {
                        String c = Long.toString(count);
                        tvResultPlayerNum.setText(c);
                        tvHeadingPlayersNum.setText("Current number of players: ");
                    } else {
                        tvResultPlayerNum.setText("No players in your Squad");
                        tvHeadingPlayersNum.setText("");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
      //  readMatchTimes();
        timeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Spinner time selected: \n" + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
               // TODO if no value cannot read from db
                readMatchTimes();

            }
        });

        timeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = timeSpin.getSelectedItem().toString();
                tvTimes.setText(str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                readMatchTimes();
            }
        });

        // Day Spinner
        daySpin = (Spinner) findViewById(daySpinner);
        daySpin.setAdapter(adapterDaysSpinner);
      //  readMatchDays();

        daySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Spinner day selected: \n" + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                readMatchDays();
            }
        });

        //Day Spinner
        daySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = daySpin.getSelectedItem().toString();
                tvDays.setText(str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                readMatchDays();
            }
        });
        // End Spinners





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

    private void saveMatchDetails(final String groupId) {
        final String matchDay = tvDays.getText().toString();
        final String matchTime = tvTimes.getText().toString();
        final int matchNumber = Integer.parseInt(tvResultPlayerNum.getText().toString());

        groupsDatabase.child(firebaseUser.getUid()).child("members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Player mPlayer = dataSnapshot.getValue(Player.class);
                String phoneNum = mPlayer.getPhoneNum().toString();
                Log.d("MATCH 1",phoneNum);

                checkingNumber(phoneNum, groupId);


                Match match = new Match(matchTime, matchDay, matchNumber, groupId,firebaseUser.getDisplayName());

                matchesDatabase.setValue(match);



                Toast.makeText(MatchActivity.this, "Match details added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String phoneNum = dataSnapshot.getValue().toString();
                Log.d("phoneNum",phoneNum);

                checkingNumber(phoneNum, groupId);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // Read Match days from DB
    private void readMatchDays() {
        //TODO Add Value event listener to see if NULL

        matchesDatabase.child("matchDay").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String matchDay = dataSnapshot.getValue().toString();
                    daySpin.setSelection(adapterDaysSpinner.getPosition(matchDay));
                    //mySpinner.setSelection(arrayAdapter.getPosition("Category 2"))

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Read Match times from DB
    private void readMatchTimes() {
        matchesDatabase.child("matchTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String matchTime = dataSnapshot.getValue().toString();
                    timeSpin.setSelection(adapterTimesSpinner.getPosition(matchTime));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkingNumber(final String playerPhoneNum, final String playerGroupId){
        DatabaseReference mDatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("users");
        final Query query = mDatabaseReference;
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String ds = dataSnapshot.getValue().toString();
                if (dataSnapshot.getValue() != null || !dataSnapshot.exists() || ds.length()!= 0) {

                    User mUser = dataSnapshot.getValue(User.class);
                    String userPhone = mUser.getPhoneNum();
                    if (userPhone == null || userPhone.equals("") ){
                       // Toast.makeText(getApplicationContext(), "****NOT FOUND****", Toast.LENGTH_LONG).show();
                        Log.d(TAG,"Not found");

                    } else {
                        //TODO
                        Log.d("MATCH 2",userPhone);
                        Log.d("MATCH 3",playerPhoneNum);

                        if(userPhone != null){
                            userPhone = userPhone.replace(" ", "");

                            if(userPhone.equals(playerPhoneNum)) {
                                final String invitedUid = mUser.getUid();
                                Log.d("MATCH 4",invitedUid);

                                moveFirebaseRecord(groupsDatabase.child(firebaseUser.getUid()).child("matches"),
                                        usersDatabase.child(invitedUid).child("groups"));

                                // TODO IF INVITE GROUP ID MATCHES USER INVITED GROUP ID OR DOES NOT EXIST
                                usersDatabase.child(invitedUid).child("groups").child("groupId").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            String ds = dataSnapshot.getValue().toString();
                                            if (ds.equals(playerGroupId)) {
                                                // then it matches so we can move data
                                                Log.d("MATCH 5", ds);
                                                Log.d("MATCH 6", playerGroupId);
                                                moveFirebaseRecord(groupsDatabase.child(firebaseUser.getUid()).child("matches"),
                                                        usersDatabase.child(invitedUid).child("groups"));

                                              //  Toast.makeText(getApplicationContext(), "* found **" + " " + invitedUid, Toast.LENGTH_SHORT).show();

                                            } else {
                                                // it does not matchh so warn inviting user that thay are already involved in a match
                                                Log.e("EditPlayer", "Player already member of group");

                                                String pushKey = dataSnapshot.getKey();
                                                Log.d("push", pushKey);
                                                Toast.makeText(getApplicationContext(), "This player is already a member of a Squad", Toast.LENGTH_SHORT).show();
                                                //usersDatabase.child(firebaseUser.getUid()).child("members").child(pushKey).child("additionalMatch").setValue(false);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                       // Toast.makeText(getApplicationContext(), "Error with invite copy", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG,"Error");

                                    }
                                });

                            }else if(userPhone == null){
                                //Toast.makeText(getApplicationContext(), "3 CHECKECK", Toast.LENGTH_SHORT).show();
                                Log.d(TAG,"Error");

                            }
                            else{
                              //  Toast.makeText(getApplicationContext(), "THERE IS A USER IN DB WITH PHONE NULL " , Toast.LENGTH_SHORT).show();
                                Log.d(TAG,"user with null");


                            }
                        }else{
                            //Toast.makeText(getApplicationContext(), "USER PHONE NULL " , Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"user phone null");

                        }
                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void moveFirebaseRecord(DatabaseReference fromPath, final DatabaseReference toPath) {

        Log.d("moveRec", String.valueOf(fromPath));
        Log.d("moveRec", String.valueOf(toPath));

        fromPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Log.d("moveRec", String.valueOf(dataSnapshot));


                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Log.d("moveRec", String.valueOf(dataSnapshot));
                        Log.d("moveRec", String.valueOf(toPath));


                        if (databaseError != null) {
                           // Toast.makeText(getApplicationContext(), "COPY FAILED", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"Copy fail");

                        } else {
                            //Toast.makeText(getApplicationContext(), "COPY SUCCESS", Toast.LENGTH_LONG).show();
                            Log.d(TAG,"copy success");

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
          //      Toast.makeText(getApplicationContext(), "onCancelled- copy fail", Toast.LENGTH_LONG).show();
                Log.d(TAG,"on cancel copy fail");


            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        readMatchDays();
        readMatchTimes();

    }

    // Sign out
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:

                //sign out
                AuthUI.getInstance().signOut(this);
                // user is now signed out

                // Explicit Intent by specifying its class name
                Intent i = new Intent(MatchActivity.this, MainActivity.class);

                // Starts TargetActivity
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //TODO
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in Cancelled ", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }




    private void onSignedOutCleanUp() {
        // unset user name
        mUsername = ANONYMOUS;
        //clear messages from adapter, user not signed in should be able to see msgs
        // mMessageAdapter.clear();
        //detach listener
        //  detachDatabaseReadListener();
    }

}
