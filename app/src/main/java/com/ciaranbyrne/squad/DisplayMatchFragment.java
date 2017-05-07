package com.ciaranbyrne.squad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
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


/**
 * Created by ciaranbyrne on 05/05/2017.
 */

public class DisplayMatchFragment extends Fragment {

    private TextView tvAddedBy;
    private TextView tvMatchTime;
    private TextView tvMatchDay;
    private Switch switchPlaying;

    private Button btnConfirmStatus;

    //Firebase database

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersDatabase;
    private DatabaseReference groupsDatabase;
    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.display_match_details_fragment, container, false);


        //FIREBASE
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        final String uId = firebaseUser.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabase = mFirebaseDatabase.getReference().child("users");
        groupsDatabase = mFirebaseDatabase.getReference().child("groups");

        tvAddedBy = (TextView) view.findViewById(R.id.tvAddedByMatchDisplay);
        tvMatchTime = (TextView) view.findViewById(R.id.tvMatchTime);
        tvMatchDay = (TextView) view.findViewById(R.id.tvMatchDay);
        switchPlaying = (Switch) view.findViewById(R.id.switchPlay);
        btnConfirmStatus = (Button) view.findViewById(R.id.btnPlayingConfirm);


        switchPlaying.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isPlaying) {
                Snackbar.make(compoundButton, "Playing: " + isPlaying, Snackbar.LENGTH_LONG)
                        .setAction("ACTION", null).show();
            }
        });

        btnConfirmStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Boolean isPlayingExtraMatch = switchPlaying.isChecked();

                String phTest = "0876877924";
                String groupTest = "irlmWlRNRiWeF9THOKUilzwOfid2";

                // TODO


                usersDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() || dataSnapshot.getValue() != null){

                            User mUser = dataSnapshot.getValue(User.class);
                            String userPhone = mUser.getPhoneNum();

                            Group mGroup = dataSnapshot.child("groups").getValue(Group.class);
                            String groupId = mGroup.getGroupId();
                            checkingNumberInMembers(userPhone,isPlayingExtraMatch.toString(),groupId);


                        }else{

                            //TODO
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                usersDatabase.child(uId).child("additionalMatch").setValue(isPlayingExtraMatch);
            }
        });

        readAddedBy(uId);
        readMatchDay(uId);
        readMatchTime(uId);



        return view;
    }// end onCreate

    public void checkingNumberInMembers(final String userPhoneNum, final String isPlayingExtraMatch,String groupId){
        DatabaseReference mDatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupId).child("members");
        Log.d("DUPL0",userPhoneNum);

        final Query query = mDatabaseReference;
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String ds = dataSnapshot.toString();
                if (dataSnapshot.getValue() != null || !dataSnapshot.exists() || !ds.equals("")) {
                    Log.d("DUPL1",ds);
                    Player mPlayer = dataSnapshot.getValue(Player.class);
                    String playerPhone = mPlayer.getPhoneNum();
                    Log.d("DUPL2",playerPhone);

                    if (playerPhone == null || playerPhone.equals("") ){
                        Toast.makeText(getActivity(), "****NOT FOUND****", Toast.LENGTH_LONG).show();


                    } else {
                        //TODO
                        Log.d("DUPL 3",playerPhone);
                        Log.d("DUPL 4",userPhoneNum);

                        if(playerPhone != null){
                          //  pla = userPhone.replace(" ", "");

                            if(playerPhone.equals(userPhoneNum)) {
                                String groupId = mPlayer.getGroupId();
                                String playerPushKey = dataSnapshot.getKey();

                                String extraMatch = isPlayingExtraMatch.toString();
                               updateResponse(groupId, firebaseUser.getUid(),playerPushKey,extraMatch);

                                Toast.makeText(getActivity(), "FOUND HERE" + " " + groupId, Toast.LENGTH_LONG).show();

                            }else if(playerPhone == null){
                                Toast.makeText(getActivity(), "3 CHECKECK", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getActivity(), "THERE IS A USER IN DB WITH PHONE NULL " , Toast.LENGTH_LONG).show();

                            }
                        }else{
                            Toast.makeText(getActivity(), "USER PHONE NULL " , Toast.LENGTH_LONG).show();

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

    private void updateResponse(String groupId, String thisUserId, final String playerPushKey, final String isPlayingExtraMatch) {
        usersDatabase.child(thisUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() || dataSnapshot.getValue() != null){

                    User mUser = dataSnapshot.getValue(User.class);
                    String userPhone = mUser.getPhoneNum();

                    Group mGroup = dataSnapshot.child("groups").getValue(Group.class);
                    String groupId = mGroup.getGroupId();

                    Boolean extraMatch= Boolean.valueOf(isPlayingExtraMatch);
                    Log.d("extraMatch",extraMatch.toString());
                    mUser.setAdditionalMatch(extraMatch);

                    groupsDatabase.child(groupId).child("members").child(playerPushKey).setValue(mUser);

                }else{

                    //TODO
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
/*
    public void getUserPhoneNum(String uId){
        usersDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() || dataSnapshot.getValue() != null){

                    User mUser = dataSnapshot.getValue(User.class);
                    String userPhone = mUser.getPhoneNum();

                    Group mGroup = dataSnapshot.child("groups").getValue(Group.class);
                    String groupId = mGroup.getGroupId();

                   groupsDatabase.child(groupId).child("members").child(userPhone).child("playing").setValue()
                    if(userPhone != null){


                    }
                }else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
*/



    //Read added by
    private void readAddedBy(String uid) {
        usersDatabase.child(uid).child("groups").child("adminName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ds = dataSnapshot.toString();
                if (dataSnapshot.exists() || !ds.equals("") || dataSnapshot.getValue() != null) {
                    String adminName = dataSnapshot.getValue().toString();
                    tvAddedBy.setText(adminName);

                } else {

                    Toast.makeText(getActivity(), " Read error", Toast.LENGTH_SHORT).show();

                    Log.e("Read Error", dataSnapshot.toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), " Database error", Toast.LENGTH_SHORT).show();

                Log.e("Read Error", "Database error");
            }
        });
    }

    //Read match day
    private void readMatchDay(String uid) {
        usersDatabase.child(uid).child("groups").child("matchDay").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ds = dataSnapshot.toString();
                if (dataSnapshot.exists() || !ds.equals("") || dataSnapshot.getValue() != null) {
                    String matchDay = dataSnapshot.getValue().toString();
                    tvMatchDay.setText(matchDay);
                } else {

                    Toast.makeText(getActivity(), " Read error", Toast.LENGTH_SHORT).show();

                    Log.e("Read Error", dataSnapshot.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), " Database error", Toast.LENGTH_SHORT).show();

                Log.e("Read Error", "Database error");
            }
        });
    }

    // Read match time
    private void readMatchTime(String uid) {
        usersDatabase.child(uid).child("groups").child("matchTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ds = dataSnapshot.toString();
                if (dataSnapshot.exists() || !ds.equals("") || dataSnapshot.getValue() != null) {
                    String matchTime = dataSnapshot.getValue().toString();
                    tvMatchTime.setText(matchTime);
                } else {
                    Toast.makeText(getActivity(), " Read error", Toast.LENGTH_SHORT).show();

                    Log.e("Read Error", dataSnapshot.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), " Database error", Toast.LENGTH_SHORT).show();

                Log.e("Read Error", databaseError.toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
