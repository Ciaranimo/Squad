package com.ciaranbyrne.squad;

/**
 * Created by ciaranbyrne on 01/05/2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class InputPhoneFragment extends Fragment{

    private EditText etInputNum;
    private Button btnSavePhoneNum;

    //Firebase database

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersDatabase;
    private DatabaseReference groupsDatabase;


    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_phone_num_fragment, container, false);

        //FIREBASE
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        String uId = firebaseUser.getUid();
        groupsDatabase = mFirebaseDatabase.getReference().child("groups");


        final FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        usersDatabase = mFirebaseDatabase.getReference().child("users");
        // Initialize variables
        etInputNum = (EditText) view.findViewById(R.id.etInputPhoneNum);
        btnSavePhoneNum = (Button)view.findViewById(R.id.btnSavePhoneNum);


        //button to edit players
        //set on click listener

        btnSavePhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ph = etInputNum.getText().toString();
                String searchNum = ph.substring( ph.length()-7);
                Log.d("FRAGM",searchNum);
                usersDatabase.child(firebaseUser.getUid()).child("phoneNum").setValue(ph);
                usersDatabase.child(firebaseUser.getUid()).child("searchNum").setValue(searchNum);
                Toast.makeText(getActivity()," Save button clicked" + ph, Toast.LENGTH_LONG).show();

                etInputNum.setText("");

              //  checkingNumber(ph);


                getView().setVisibility(View.GONE);
            }
        });

        return view;


    }// end onCreate

    private void savePhoneNum(String phoneNum) {
        String ph = "TESTSTSTST";
        usersDatabase.setValue(ph);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    // TODO ********* works  - check to see if user exists in groups node
    public void checkingNumber(final String userPhoneNum){
        DatabaseReference mDatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("groups");

        final Query query = mDatabaseReference;
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {

                    Player mPlayer = dataSnapshot.getValue(Player.class);
                    String playerPhone = mPlayer.getPhoneNum();
                    String groupId = mPlayer.getGroupId();

                    if (playerPhone != null || !playerPhone.equals("")){
                        playerPhone = playerPhone.replace(" ", "");
                        Log.d("TAGG 3",playerPhone);
                        Log.d("TAGG 4",userPhoneNum);

                        // String userSearchNum = userPhone.substring( userPhone.length()-7);
                        //     String playerSearchNum = playerPhoneNum.substring( playerPhoneNum.length() -7);

                        //Log.d("TAGG 5",userSearchNum);

                        if(playerPhone.equals(userPhoneNum)) {
                            String name = mPlayer.getName();

                            Toast.makeText(getActivity(), "* found **" + " " + name, Toast.LENGTH_LONG).show();
                            //TODO copy match details to user node
                            moveFirebaseRecord(groupsDatabase.child(groupId).child("matches"),
                                    usersDatabase.child(firebaseUser.getUid()).child("groups"));

                        }else if(playerPhone == null){
                            Toast.makeText(getActivity(), "3 CHECKECK", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "THERE IS A PLAYER IN DB WITH PHONE NULL " , Toast.LENGTH_LONG).show();

                        }

                    }
                } else {
                    Toast.makeText(getActivity(), "****NOT FOUND****", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(getActivity(), "COPY FAILED", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "COPY SUCCESS", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "onCancelled- copy fail", Toast.LENGTH_LONG).show();

            }
        });

    }

}
