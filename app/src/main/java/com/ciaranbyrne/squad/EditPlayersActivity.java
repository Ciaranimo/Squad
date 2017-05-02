package com.ciaranbyrne.squad;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
    private FirebaseDatabase mFirebaseDatabase;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;
    private ChildEventListener mChildEventListener;

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
       // usersDatabase = mFirebaseDatabase.getReference().child("users");
        usersDatabase = mFirebaseDatabase.getReference().child("users").child(userId);
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
                usersGroupDatabase.child(clickedKey).removeValue();
                Toast.makeText(getApplicationContext(), "Player removed from your Squad", Toast.LENGTH_LONG).show();

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
                playerNum = playerNum.replace(" ","");

                String searchNum = playerNum.substring(playerNum.length() - 8); // gets phone , for case with +353 or 086 etc
                writeNewPlayer("ID STRING123", playerName, TRUE, groupId, playerNum, searchNum);
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
    public void writeNewPlayer(String uid, String name, Boolean playing, String groupId, String phoneNum, String searchNum) {
        String groupsKey = mDatabase.child("groups").push().getKey();
        String playersKey = mDatabase.child("players").push().getKey();

        Player player = new Player(uid, name, playing, groupId, phoneNum, searchNum);
        Map<String, Object> playerValues = player.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/players/" + playersKey, playerValues);
        childUpdates.put("/groups/" + groupId + "/members/" + groupsKey, playerValues);
        mDatabase.updateChildren(childUpdates);

        // TODO - Causing issues
        //checkIfPlayerIsUser(phoneNum);
        //checkPlayerIsUser(phoneNum);

        checkPlayerNum(phoneNum);

    }

    //TODO 2nd attempt to find player in users db
    public void checkPlayerIsUser(final String phoneNum){
        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    for(DataSnapshot grandChild : child.getChildren())
                    {
                        if(grandChild.getValue() == phoneNum){
                            Toast.makeText(getApplicationContext(), "****FOUND****", Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // TODO Check to see if player exists in Database, search by phone
    public void checkIfPlayerIsUser(final String phoneNum) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String userKey = ds.getKey();
                //    Log.d("Tag", userKey); passess as ok

                    DatabaseReference userKeyDatabase =  mFirebaseDatabase.getReference().child("users").child(userKey);
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("Tag", String.valueOf(dataSnapshot.child("phoneNum").getValue()));
                            /*
                            if (dataSnapshot.child("phoneNum").getValue() == null) {
                                Toast.makeText(getApplicationContext(), "****NULLL****", Toast.LENGTH_LONG).show();
                                Log.d("Tag", String.valueOf(dataSnapshot.child("phoneNum").getValue()));

                            } else if(dataSnapshot.child("phoneNum").getValue().equals(phoneNum)){
                                // player added is in user database
                                Toast.makeText(getApplicationContext(), "****PLAYER FOUND****", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "****NOT FOUND****", Toast.LENGTH_LONG).show();

                            }
                            */

                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    };
                    userKeyDatabase.addListenerForSingleValueEvent(eventListener);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        usersDatabase.addListenerForSingleValueEvent(valueEventListener);
    }

    //TODO other try
    public void checkPlayerNum(final String phoneNum) {
        DatabaseReference mDatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("users");

        if (!TextUtils.isEmpty(phoneNum)) {
            final Query phoneNumReference = mDatabaseReference.orderByChild("phoneNum").equalTo(phoneNum);

            ValueEventListener phoneNumValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Toast.makeText(getApplicationContext(), "****PLAYER FOUND****", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "****NOT FOUND****", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };

            phoneNumReference.addListenerForSingleValueEvent(phoneNumValueEventListener);


        } else {
            Log.e("Error","phoneNum is null");
        }
    }

    public void fetchContacts() {

        String phoneNumber = null;
        String email = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        StringBuffer output = new StringBuffer();

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {

                    output.append("\n First Name:" + name);

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append("\n Phone number:" + phoneNumber);

                    }

                    phoneCursor.close();

                    // Query and loop for every email of the contact
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (emailCursor.moveToNext()) {

                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));

                        output.append("\nEmail:" + email);

                    }

                    emailCursor.close();
                }

                output.append("\n");
            }

            // outputText.setText(output);
        }
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
