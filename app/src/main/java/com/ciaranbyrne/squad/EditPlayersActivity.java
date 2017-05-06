package com.ciaranbyrne.squad;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;

public class EditPlayersActivity extends AppCompatActivity {

    static final String TAG = "EditPlayersActivity";

    //Firebase database variables
    private DatabaseReference playersDatabase;
    private DatabaseReference groupsDatabase;
    private DatabaseReference usersDatabase;
    private DatabaseReference usersGroupDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference phoneNumReference;
    private DatabaseReference matchesDatabase;
    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference usersGroupsRef;
    private DatabaseReference matchesRef;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;
    private ChildEventListener mChildEventListener;

    private ChildEventListener matchChildEventListener;


    // 7 Firebase Authenticate TODO
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // instance variables
    private TextView resultText;
    private TextView resultNum;
    private Button btnAddPlayer;
    private SearchView etNewPlayer;
    private ListView playersListView;
    private PlayersAdapter playersAdapter;
    private ArrayList<Player> playerList;
    // ARRAY LIST FOR KEYS
    private ArrayList<String> keysList;


    // For read permissions on Contacts
    final private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 987;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ciaranbyrne.squad.R.layout.activity_edit_players);

        //GET CURRENT USER INFO
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        // Adding users to groups - Setting Group ID to be the same as User Id
        final String groupId = firebaseUser.getUid();
        String userId = firebaseUser.getUid();

        // get database reference to read data
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        playersDatabase = mFirebaseDatabase.getReference().child("players");
        groupsDatabase = mFirebaseDatabase.getReference().child("groups");
        usersDatabase = mFirebaseDatabase.getReference().child("users");
        //usersDatabase = mFirebaseDatabase.getReference().child("users").child(userId);
        usersGroupDatabase = mFirebaseDatabase.getReference().child("groups").child(groupId).child("members");

        mDatabase = mFirebaseDatabase.getReference();

        // Initialize references to views
        playersListView = (ListView) findViewById(R.id.list_players);

        etNewPlayer = (SearchView) findViewById(R.id.etNewPlayer);
        // for contacts picker
        // outputText = (TextView) findViewById(R.id.textView1);

        //  For contact search
        resultText = (TextView) findViewById(R.id.searchViewResult);
        resultNum = (TextView) findViewById(R.id.searchViewNum);

        //Initialize ListView and adapter for Database Reading players list
        playerList = new ArrayList<>();
        playersAdapter = new PlayersAdapter(this, R.layout.list_entry, playerList);
        playersListView.setAdapter(playersAdapter);
        //  INITIALIZE KEYS ARRAY
        keysList = new ArrayList<>();

        // LONG CLICK REMOVES PLAYERS
        playersListView.setOnItemLongClickListener(new OnItemLongClickListener() {


            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                String clickedKey = keysList.get(position);
               // clickedKey = clickedKey.get
                Log.d("1DATA",clickedKey);

                DatabaseReference usersGroupRef = usersGroupDatabase.child(clickedKey).child("phoneNum");
               final Query query = usersGroupRef;

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String ds = dataSnapshot.toString();
                        Log.d("1DATA",dataSnapshot.toString());
                        if (dataSnapshot == null || ds.length() == 0) {
                            Toast.makeText(getApplicationContext(), "NULL",Toast.LENGTH_SHORT).show();
                        }else{
                            String phoneNum = dataSnapshot.getValue().toString();
                            Log.d("1DATA PHONE",phoneNum);
                           //TODO checkNumForDelete(phoneNum);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "DATABASE ERROR", Toast.LENGTH_SHORT).show();

                    }
                });
                if (clickedKey != null) {
                    usersGroupDatabase.child(clickedKey).removeValue();
                    //usersDatabase.child()
                    Toast.makeText(getApplicationContext(), "Player removed from your Squad", Toast.LENGTH_SHORT).show();
                    Log.d("KEY", clickedKey);
                    checkNumForDelete(clickedKey);
                }else{
                    Toast.makeText(getApplicationContext(), "Player does not exist", Toast.LENGTH_SHORT).show();

                }

                return true;
            }
        });


        // Add player to database
        btnAddPlayer = (Button) findViewById(R.id.btnAddPlayer);
        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String playerName = resultText.getText().toString();

                // TODO SUB STRING TO ALLOW SEARCHING
                String playerNum = resultNum.getText().toString();
                if (playerNum.equals("") || playerNum.length() == 0){
                    Toast.makeText(getApplicationContext(), "player num null", Toast.LENGTH_LONG).show();


                }else{
                    playerNum = playerNum.replace(" ","");
                    playerNum = playerNum.replace(" ","");

                    Log.d("TAGG 1",playerNum);
                }
                writeNewPlayer( playerName, TRUE, groupId, playerNum);
                resultText.setText("");
                resultNum.setText("");

                Toast.makeText(getApplicationContext(), "" + playerName + " added to your Squad", Toast.LENGTH_LONG).show();
            }
        });


        // Read from the database
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, dataSnapshot.getKey() + ":" + dataSnapshot.getValue().toString());

                // String value = dataSnapshot.getValue(String.class);
                Player player = dataSnapshot.getValue(Player.class);
                //  ADDING KEY TO KEYS LIST
                keysList.add(dataSnapshot.getKey().toString());

                playersAdapter.add(player);

                updateListView();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                String deletedKey = dataSnapshot.getKey();
                int removedIndex = keysList.indexOf(deletedKey);
                keysList.remove(removedIndex);
                playerList.remove(removedIndex);
                updateListView();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        groupsDatabase.child(groupId).child("members").addChildEventListener(mChildEventListener);

        permissionsCheck();


    }// End of onCreate


    //  updates list view and adapter, called in child event listener
    private void updateListView() {
        playersAdapter.notifyDataSetChanged();
        playersListView.invalidate();
        Log.d(TAG, "Length: " + playerList.size());
    }

    // Write player to players & groups node method
    public void writeNewPlayer( String name, Boolean playing, final String groupId, String phoneNum) {

        if(phoneNum.length() != 0){
            if(phoneNum.contains("+353")){

                phoneNum = phoneNum.replace("+353", "0");

            }
        }
        final String ph = phoneNum;
      // String groupsKey = mDatabase.child("groups").push().getKey();
        String groupsKey = phoneNum;
      // String playersKey = mDatabase.child("players").push().getKey();
        String playersKey = phoneNum;

        Player player = new Player( name, playing, groupId, phoneNum);
        Map<String, Object> playerValues = player.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/players/" + playersKey, playerValues);
        childUpdates.put("/groups/" + groupId + "/members/" + groupsKey, playerValues);
     //   mDatabase.updateChildren(childUpdates);

        Log.d("write player phone ",phoneNum);

         checkingNumber(phoneNum);


        mDatabase.updateChildren(childUpdates);

    }

    // TODO delete match from user's node
    public void deleteMathcFromUser(String uId){
        String userId = uId;
        if(usersDatabase.child(userId).child("groups") != null) {
            usersDatabase.child(uId).child("groups").removeValue();
        }
    }

    // TODO *****
    public void checkNumForDelete(final String playerPhoneNum){
        DatabaseReference mDatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("users");

        final Query query = mDatabaseReference;
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String ds = dataSnapshot.toString();
                if (dataSnapshot.getValue() != null || !dataSnapshot.exists() || !ds.equals("")) {

                    User mUser = dataSnapshot.getValue(User.class);
                    String userPhone = mUser.getPhoneNum();
                    if (userPhone == null || userPhone.equals("") || userPhone.length() == 0 ){

                        Toast.makeText(getApplicationContext(), "USER PHONE NULL YOY OYOY OY " , Toast.LENGTH_LONG).show();

                        // String userSearchNum = userPhone.substring( userPhone.length()-7);
                        //     String playerSearchNum = playerPhoneNum.substring( playerPhoneNum.length() -7);

                        //Log.d("TAGG 5",userSearchNum);
                        //     Log.d("TAGG 6", playerSearchNum);
                        if(userPhone == null){
                            Toast.makeText(getApplicationContext(), "USER PHONE NULL " , Toast.LENGTH_LONG).show();


                            }
                        }else{

                            //TODO

                        if(userPhone.equals(playerPhoneNum)) {
                            String uId = mUser.getUid();
                            deleteMathcFromUser(uId);
                            Toast.makeText(getApplicationContext(), "FOUND NUM TO DELETE" + " " + uId, Toast.LENGTH_LONG).show();

                        }else if(userPhone == null){
                            Toast.makeText(getApplicationContext(), "DELETE NUM CHECK NULL", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "THERE IS A USER IN DB WITH PHONE NULL " , Toast.LENGTH_LONG).show();

                        }
                    }
                } else {
                    //TODO
                    Toast.makeText(getApplicationContext(), "****NOT FOUND****", Toast.LENGTH_LONG).show();
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

    // working now .. TODO not anymore
    public void checkingNumber(final String playerPhoneNum){
        DatabaseReference mDatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("users");

        final Query query = mDatabaseReference;
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String ds = dataSnapshot.toString();
                if (dataSnapshot.getValue() != null || !dataSnapshot.exists() || !ds.equals("")) {

                    User mUser = dataSnapshot.getValue(User.class);
                    String userPhone = mUser.getPhoneNum();
                    if (userPhone == null || userPhone.equals("") ){
                        Toast.makeText(getApplicationContext(), "****NOT FOUND****", Toast.LENGTH_LONG).show();


                } else {
                    //TODO
                        Log.d("TAGG 3",userPhone);
                        Log.d("TAGG 4",playerPhoneNum);

                        if(userPhone != null){
                            userPhone = userPhone.replace(" ", "");

                            if(userPhone.equals(playerPhoneNum)) {
                                String uId = mUser.getUid();
                                moveFirebaseRecord(groupsDatabase.child(firebaseUser.getUid()).child("matches"),
                                        usersDatabase.child(uId).child("groups"));

                                Toast.makeText(getApplicationContext(), "* found **" + " " + uId, Toast.LENGTH_LONG).show();

                            }else if(userPhone == null){
                                Toast.makeText(getApplicationContext(), "3 CHECKECK", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "THERE IS A USER IN DB WITH PHONE NULL " , Toast.LENGTH_LONG).show();

                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "USER PHONE NULL " , Toast.LENGTH_LONG).show();

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


// Working now
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
                                Toast.makeText(getApplicationContext(), "COPY FAILED", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "COPY SUCCESS", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "onCancelled- copy fail", Toast.LENGTH_LONG).show();

                }
            });

    }

    public void writeUsersGroups(String playerId){
        usersGroupsRef = mFirebaseDatabase.getReference().child("users").child(playerId).child("groups");
        matchesRef = mFirebaseDatabase.getReference().child("groups").child(firebaseUser.getUid()).child("matches");

// Listen for status updates
        usersGroupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    // TODO NOT WORKING
    public void copyFirebaseData(String playerId) {
        //FROM
        matchesRef = mFirebaseDatabase.getReference().child("groups").child(firebaseUser.getUid()).child("matches");

        Log.d("COPY TAG + matchesRef", matchesRef.toString());
        //TO
        usersGroupsRef = mFirebaseDatabase.getReference().child("users").child(playerId).child("matches");
        Log.d("COPY TAG + usersGroups", usersGroupsRef.toString());

        matchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("COPY TAG + dataSnap", dataSnapshot.toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("COPY TAG + snapshot", snapshot.toString());

                    String snapshotKey = snapshot.getKey();

                    Log.d("COPY TAG + snapshot key", snapshotKey.toString());

                    String mt = snapshot.child("matchTime").getValue(String.class);

                    Log.d("COPY TAG +  match time", mt);

                    String md = snapshot.child("matchDay").getValue(String.class);
                 //   int mn = snapshot.child("matchNumbers").getValue(Integer.class);
                    Boolean et = snapshot.child("evenTeams").getValue(Boolean.class);
                    Boolean wk = snapshot.child("weekly").getValue(Boolean.class);
                    String groupId = snapshot.child("groupId").getValue(String.class);

                    usersGroupsRef.child(snapshotKey).child("matchTime").setValue(mt);
                    usersGroupsRef.child(snapshotKey).child("matchDay").setValue(md);
                 //   usersGroupsRef.child(snapshotKey).child("matchNumbers").setValue(mn);
                    usersGroupsRef.child(snapshotKey).child("evenTeams").setValue(et);
                    usersGroupsRef.child(snapshotKey).child("weekly").setValue(wk);
                    usersGroupsRef.child(snapshotKey).child("groupId").setValue(groupId);

                    Toast.makeText(getApplicationContext(), "WORKED" + groupId, Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Do your work here
                    // fetchContacts();
                    setupSearchView();

                } else {
                    // Permission Denied
                    Toast.makeText(EditPlayersActivity.this, "READ_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    public void permissionsCheck() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            //do your work here
            //  fetchContacts();
            setupSearchView();

        }
    }

    //  METHOD FOR SEARCHING CONTACTS
    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) findViewById(R.id.etNewPlayer);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);
    }


    //  Handle suggestion pick user action
    @Override
    protected void onNewIntent(Intent intent) {
        if (ContactsContract.Intents.SEARCH_SUGGESTION_CLICKED.equals(intent.getAction())) {
            //handles suggestion clicked query
            String displayName = getDisplayNameForContact(intent);
            String displayNum = getPhoneNumForContact(intent);
            resultText.setText(displayName);
            resultNum.setText(displayNum);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            resultText.setText("should search for query: '" + query + "'...");
        }
    }
    // got contact picker from here
    // https://looksok.wordpress.com/2013/06/15/android-searchview-tutorial-edittext-with-phone-contacts-search-and-autosuggestion/

    //  get contact display name
    private String getDisplayNameForContact(Intent intent) {
        Cursor phoneCursor = getContentResolver().query(intent.getData(), null, null, null, null);
        phoneCursor.moveToFirst();
        int idDisplayName = phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

        String name = phoneCursor.getString(idDisplayName);

        phoneCursor.close();
        return name;
    }

    // get contact Phone num
    private String getPhoneNumForContact(Intent intent) {
        Cursor phoneCursor = getContentResolver().query(intent.getData(), null, null, null, null);
        phoneCursor.moveToFirst();

        String hasPhone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        String contactId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts._ID));
        if (hasPhone.equalsIgnoreCase("1"))
            hasPhone = "true";
        else
            hasPhone = "false";

        String phoneNumber = null;
        if (Boolean.parseBoolean(hasPhone)) {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            if (phones != null) {
                while (phones.moveToNext()) {
                    phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                }
            }

            phones.close();
        }
        return phoneNumber;
    }

}
