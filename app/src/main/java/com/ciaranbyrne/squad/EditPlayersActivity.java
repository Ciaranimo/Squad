package com.ciaranbyrne.squad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.lang.Boolean.TRUE;

public class EditPlayersActivity extends AppCompatActivity {

    //Firebase instance variables
    private DatabaseReference playersDatabase;
    private DatabaseReference groupsDatabase;
    private FirebaseListAdapter mAdapter;


    private Button btnAddPlayer;
    private EditText etNewPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ciaranbyrne.squad.R.layout.activity_edit_players);

        // get database reference to read data
        playersDatabase = FirebaseDatabase.getInstance().getReference().child("players");
        groupsDatabase = FirebaseDatabase.getInstance().getReference().child("groups");
        // instantiate arraylist to show players
        //mPlayersList = (ListView) findViewById(R.id.list_players);

        // testing creating new User
       // User user = new User("Tom", "EMAIL STRING", TRUE);
        // playersDatabase.push().setValue(user);

        ListView playersView = (ListView) findViewById(R.id.list_players);

        // Create customer Firebase ListAdapter sub class
        mAdapter = new FirebaseListAdapter<User>(this, User.class, android.R.layout.two_line_list_item, playersDatabase) {
            @Override
            protected void populateView(View view, User user, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(user.getName());
                ((TextView)view.findViewById(android.R.id.text2)).setText(String.valueOf(user.getPlaying()));


            }
        };
        playersView.setAdapter(mAdapter);

        etNewPlayer = (EditText) findViewById(R.id.etNewPlayer);
        // TODO GET CONTACT INFO -  crashing app- from stack overflow http://stackoverflow.com/questions/1721279/how-to-read-contacts-on-android-2-0
        /*
        etNewPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.Contacts._ID));
                    String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (Boolean.parseBoolean(hasPhone)) {
                        // You know it has a number so now query it like this
                        Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);
                        while (phones.moveToNext()) {
                            String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        phones.close();
                    }

                    Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
                    while (emails.moveToNext()) {
                        // This would allow you get several email addresses
                        String emailAddress = emails.getString(
                                emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    emails.close();
                }
                cursor.close();
            }
        });
        */
        
        // Add user to database
        btnAddPlayer = (Button) findViewById(R.id.btnAddPlayer);
        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playersDatabase.push().setValue(new User(etNewPlayer.getText().toString(),"EMAIL STRING", TRUE));
                etNewPlayer.setText("");
            }
        });




        /* TODO Querying last 5 entries from github tut, gives error at runtime
        playersDatabase.limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot msgSnapshot: snapshot.getChildren()) {
                    User player = msgSnapshot.getValue(User.class);
                    Log.i("User", player.getName());
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
               // Log.e("Chat", "The read failed: " + error.getText());
            }
        });
        */

    }// End of onCreate

    // stop listening for changes in the Firebase database by using onDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
