package com.ciaranbyrne.squad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;

public class EditPlayersActivity extends AppCompatActivity {

    //Firebase instance variables
    private DatabaseReference playersDatabase;
    private DatabaseReference groupsDatabase;
    private DatabaseReference mDatabase;
    private FirebaseListAdapter mAdapter;
    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;

    private User user;
    private Player player;
    private Group group;

    private Button btnAddPlayer;
    private EditText etNewPlayer;
    private ListView playersListView;

    private ArrayList<String> playerArrayList = new ArrayList<>();
    private Map<String, Boolean> members = new HashMap<>();
    private Map<String, Boolean> groups = new HashMap<>();

    private static final String TAG = "MyActivity";



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
      //  Player player = new Player("ID12345", "John Doe", TRUE,"no group test" );
        //playersDatabase.push().setValue(player);

        // test creating new Group
        //GET CURRENT USER INFO
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        String uId = firebaseUser.getUid();
        String name = firebaseUser.getDisplayName();

        // ERROR is something to do with Firebase user strings
       User user = new User(uId, name);


        // Adding users to groups
        String groupId = groupsDatabase.push().getKey();
        Group group = new Group(groupId, name);
       // groupsDatabase.child(groupId).setValue(group);
        members.put("JIM",true);
        members.put("TONY",true);

        group.setMembers(members);
        groupsDatabase.child(groupId).setValue(group);
        Log.i(TAG,group.toString());
        // TEST
      //  writeGroup(groupId,uId);


        //*TEST* Adding player to that group
        final Player player = new Player(uId,name,TRUE,groupId);
        playersDatabase.push().setValue(player);
        playersListView = (ListView) findViewById(R.id.list_players);


        //TVAC TUTORIAL 13 - Retrieve data - NOT WORKING
        final ArrayAdapter<String> playersArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playerArrayList);
        playersListView.setAdapter(playersArrayAdapter);


        playersDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);

              // Player player = dataSnapshot.getValue(Player.class);
                playerArrayList.add(value);

                playersArrayAdapter.notifyDataSetChanged();
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


        /*
        // Create  Firebase ListAdapter sub class
        // TODO follow TVAC tutorial, remember I do not need to add Objects just get String Values from Object instead!
        mAdapter = new FirebaseListAdapter<Player>(this, Player.class, android.R.layout.two_line_list_item, playersDatabase) {
            @Override
            protected void populateView(View view, Player player, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(player.getName());
                ((TextView)view.findViewById(android.R.id.text2)).setText(String.valueOf(player.getGroupId()));


            }
        };
        playersListView.setAdapter(mAdapter);
        */
      //  playersDatabase.removeValue(position);


        etNewPlayer = (EditText) findViewById(R.id.etNewPlayer);
        // TODO GET CONTACT INFO -  crashing app- from stack overflow http://stackoverflow.com/questions/1721279/how-to-read-contacts-on-android-2-0

        
        // Add user to database TODO Add input Player to Group
        btnAddPlayer = (Button) findViewById(R.id.btnAddPlayer);
        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playersDatabase.push().setValue(new Player("ID STRING", etNewPlayer.getText().toString(), TRUE, "GROUP ID GOES HERE"));
                etNewPlayer.setText("");
            }
        });




    }// End of onCreate
    /*
    private void writeGroup(String groupId, String memberId) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("groups").push().getKey();
        Group group = new Group(groupId, memberId);
        Map<String, Object> groupValues = group.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/groups/" + key, groupValues);
        childUpdates.put("/members/" + memberId + "/" + key, groupValues);

        mDatabase.updateChildren(childUpdates);
    }
    */
    // stop listening for changes in the Firebase database by using onDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
