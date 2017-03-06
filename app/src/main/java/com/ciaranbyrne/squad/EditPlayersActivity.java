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
    private DatabaseReference mDatabase;
    private FirebaseListAdapter mAdapter;


    private Button btnAddPlayer;
    private EditText etNewPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ciaranbyrne.squad.R.layout.activity_edit_players);

        // get database reference to read data
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        // instantiate arraylist to show players
        //mPlayersList = (ListView) findViewById(R.id.list_players);

        // From github code, testing creating new User
        User user = new User("Tom", "1234", TRUE);
        mDatabase.push().setValue(user);

        ListView playersView = (ListView) findViewById(R.id.list_players);

        // Create customer Firebase ListAdapter sub class
        // TODO look at GITHUB tutorial
        mAdapter = new FirebaseListAdapter<User>(this, User.class, android.R.layout.two_line_list_item, mDatabase) {
            @Override
            protected void populateView(View view, User user, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(user.getName());
                ((TextView)view.findViewById(android.R.id.text2)).setText(String.valueOf(user.getPlaying()));


            }
        };
        playersView.setAdapter(mAdapter);

        // From Github: Send Chat messages
        // Add user to database
        etNewPlayer = (EditText) findViewById(R.id.etNewPlayer);
        btnAddPlayer = (Button) findViewById(R.id.btnAddPlayer);
        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.push().setValue(new User(etNewPlayer.getText().toString(),"1234", TRUE));
                etNewPlayer.setText("");
            }
        });

        /* TODO Querying last 5 entries from github tut, gives error at runtime
        mDatabase.limitToLast(5).addValueEventListener(new ValueEventListener() {
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
