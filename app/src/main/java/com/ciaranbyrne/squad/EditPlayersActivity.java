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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;

public class EditPlayersActivity extends AppCompatActivity {

    private TextView resultText;
    //Firebase database variables
    private DatabaseReference playersDatabase;
    private DatabaseReference groupsDatabase;
    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseDatabase;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;
    private ChildEventListener mChildEventListener;

    // instance variables
    private Button btnAddPlayer;
    private EditText etNewPlayer;
    private ListView playersListView;
    private PlayersAdapter playersAdapter;

    // For read permissions on Contacts
    final private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 987;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ciaranbyrne.squad.R.layout.activity_edit_players);

        // get database reference to read data
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        playersDatabase = mFirebaseDatabase.getReference().child("players");
        groupsDatabase = mFirebaseDatabase.getReference().child("groups");
        mDatabase = mFirebaseDatabase.getReference();

        //GET CURRENT USER INFO
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();


        // Initialize references to views
        playersListView = (ListView) findViewById(R.id.list_players);
        // Adding users to groups - Setting Group ID to be the same as User Id
        final String groupId = firebaseUser.getUid();
       // etNewPlayer = (EditText) findViewById(R.id.etNewPlayer);
        // for contacts picker
       // outputText = (TextView) findViewById(R.id.textView1);

        // TODO For contact search
        resultText = (TextView)findViewById(R.id.searchViewResult);


        //Initialize ListView and adapter for Database Reading players list
        List<Player> playerList = new ArrayList<>();
        playersAdapter = new PlayersAdapter(this,R.layout.list_entry, playerList);
        playersListView.setAdapter(playersAdapter);


        // Add user to database
        btnAddPlayer = (Button) findViewById(R.id.btnAddPlayer);
        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String playerName = resultText.getText().toString();
                writeNewPlayer("ID STRING123",playerName,TRUE, groupId);

                Toast.makeText(getApplicationContext(), "" + playerName + " added to your Squad", Toast.LENGTH_LONG).show();
                etNewPlayer.setText("");
            }
        });


        // Read from the database
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               // String value = dataSnapshot.getValue(String.class);
                Player player = dataSnapshot.getValue(Player.class);
                playersAdapter.add(player);

            //    playersAdapter.notifyDataSetChanged();
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
        };
        groupsDatabase.child(groupId).addChildEventListener(mChildEventListener);

        permissionsCheck();


    }// End of onCreate


    // Write player to players & groups node method
    public void writeNewPlayer(String uid, String name, Boolean playing, String groupId){
        String key = mDatabase.child("groups").push().getKey();
        Player player = new Player(uid, name, playing, groupId);
        Map<String, Object> playerValues = player.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/players" , playerValues);
        childUpdates.put("/groups/" + groupId + "/" + key, playerValues);
        mDatabase.updateChildren(childUpdates);
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

        Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        StringBuffer output = new StringBuffer();

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                if (hasPhoneNumber > 0) {

                    output.append("\n First Name:" + name);

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append("\n Phone number:" + phoneNumber);

                    }

                    phoneCursor.close();

                    // Query and loop for every email of the contact
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);

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

    public void permissionsCheck(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }else{
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

    // TODO METHOD FOR SEARCHING CONTACTS
    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) findViewById(R.id.etNewPlayer);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);
    }


    // TODO Handle suggestion pick user action
    @Override
    protected void onNewIntent(Intent intent) {
        if (ContactsContract.Intents.SEARCH_SUGGESTION_CLICKED.equals(intent.getAction())) {
            //handles suggestion clicked query
            String displayName = getDisplayNameForContact(intent);
            resultText.setText(displayName);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            resultText.setText("should search for query: '" + query + "'...");
        }
    }
    // got contact picker from here 0
    // https://looksok.wordpress.com/2013/06/15/android-searchview-tutorial-edittext-with-phone-contacts-search-and-autosuggestion/

    // TODO get contact display name
    private String getDisplayNameForContact(Intent intent) {
        Cursor phoneCursor = getContentResolver().query(intent.getData(), null, null, null, null);
        phoneCursor.moveToFirst();
        int idDisplayName = phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        String name = phoneCursor.getString(idDisplayName);

       // int idDisplayNum = phoneCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        //String phoneNum = phoneCursor.getString(idDisplayNum);

        phoneCursor.close();
        return name;
    }


}
