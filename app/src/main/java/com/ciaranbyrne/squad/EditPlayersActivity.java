package com.ciaranbyrne.squad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EditPlayersActivity extends AppCompatActivity {

    //Firebase instance variables
    private DatabaseReference mDatabase;

    // list to store players in and list for keys
    private ListView mPlayersList;
    private ArrayList<String> mPlayerNames = new ArrayList<>();
    private ArrayList<String> mKeys = new ArrayList<>(); // store key fromDB in this ArrayList

    private Button btnAddPlayer;
    private EditText etNewPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ciaranbyrne.squad.R.layout.activity_edit_players);



        // get database reference to read data
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // instantiate arraylist to show players
        mPlayersList = (ListView) findViewById(R.id.list_players);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mPlayerNames);

        mPlayersList.setAdapter(arrayAdapter);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class); //convert from object to string when getting snapshot
                // add to array list
                mPlayerNames.add(value);

                // get key from data snapshot
                String key = dataSnapshot.getKey();
                mKeys.add(key);

                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                String value = dataSnapshot.getValue(String.class);
                String key = dataSnapshot.getKey();

                // find out index of value you have changed
                int index = mKeys.indexOf(key);

                // now we know index of arraylist to change
                // we pass in index and value we want to change
                mPlayerNames.set(index, value);

                arrayAdapter.notifyDataSetChanged();

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

        //TODO Add Names of players to list view

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Name");


        btnAddPlayer = (Button) findViewById(R.id.btnAddPlayer);

        // Something going wrong down here, hash map to string problem
        /*
        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etNewPlayer = (EditText) findViewById(R.id.etNewPlayer);

                mPlayerNames.add(etNewPlayer.getText().toString());
                etNewPlayer.setText("");
                arrayAdapter.notifyDataSetChanged();


                 // FROM TVAC
                // 1. Create child in root object
                //String name = String.valueOf(mPlayersList.getItemIdAtPosition(0));
                String name = etNewPlayer.getText().toString().trim();

                // 2. Assign some value to child object
                // Create hash map to store object to database
                HashMap<String, String> datamap = new HashMap<String, String>();
                datamap.put("Name", name);

                // Push to database
                mDatabase.push().setValue(datamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Toast.makeText(EditPlayersActivity.this, "Stored...", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(EditPlayersActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                 /// END TVAC
            }// end on click
        }); // end on click listener
        */



    }
}
