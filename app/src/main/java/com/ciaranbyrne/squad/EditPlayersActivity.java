package com.ciaranbyrne.squad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditPlayersActivity extends AppCompatActivity {

    //Firebase instance variables
    private DatabaseReference playersDatabase;
    private DatabaseReference groupsDatabase;
    private FirebaseListAdapter mAdapter;
    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;

    private User user;
    private Player player;
    private Group group;

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
        // TODO FIREBASE USER
       Player player = new Player("Tom", "12345");
        playersDatabase.push().setValue(player);

        // test creating new Group
        //GET CURRENT USER INFO
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        String uId = firebaseUser.getUid();
        String name = firebaseUser.getDisplayName();

        // ERROR is something to do with Firebase user strings
       User user = new User(uId, name);

        Group group = new Group();

        group.addMember(user);
        groupsDatabase.push().setValue(group);

        ListView playersView = (ListView) findViewById(R.id.list_players);

        // Create  Firebase ListAdapter sub class
        mAdapter = new FirebaseListAdapter<User>(this, User.class, android.R.layout.two_line_list_item, playersDatabase) {
            @Override
            protected void populateView(View view, User user, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(user.getName());
              //TODO  ((TextView)view.findViewById(android.R.id.text2)).setText(String.valueOf(user.getPlaying()));


            }
        };
        playersView.setAdapter(mAdapter);

        etNewPlayer = (EditText) findViewById(R.id.etNewPlayer);
        // TODO GET CONTACT INFO -  crashing app- from stack overflow http://stackoverflow.com/questions/1721279/how-to-read-contacts-on-android-2-0

        
        // Add user to database
        btnAddPlayer = (Button) findViewById(R.id.btnAddPlayer);
        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //TODO playersDatabase.push().setValue(new User(etNewPlayer.getText().toString(),"EMAIL STRING", TRUE));
                etNewPlayer.setText("");
            }
        });




    }// End of onCreate

    // stop listening for changes in the Firebase database by using onDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
