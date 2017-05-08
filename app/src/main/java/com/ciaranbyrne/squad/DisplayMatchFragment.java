package com.ciaranbyrne.squad;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private TextView tvPlayingStatus;
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
            public void onCheckedChanged(CompoundButton compoundButton, boolean playingExtra) {

                usersDatabase.child(firebaseUser.getUid()).child("playingExtra").setValue(playingExtra);



                //Snackbar.make(compoundButton, "Playing: " + playingExtra, Snackbar.LENGTH_SHORT)
                     //   .setAction("ACTION", null).show();
            }
        });




        btnConfirmStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                usersDatabase.child(firebaseUser.getUid()).child("groups").child("groupId").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                       final String groupId = dataSnapshot.getValue().toString();

                        usersDatabase.child(firebaseUser.getUid()).child("phoneNum").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String ph = dataSnapshot.getValue().toString();
                                checkingNumberInMembers(ph,groupId);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


        readMatchTime(firebaseUser.getUid());
         readPlaying();
        readAddedBy(firebaseUser.getUid());
        readMatchDay(firebaseUser.getUid());
        return view;
    }// end onCreate


    // Read playing times from DB
    private void readPlaying() {
        usersDatabase.child(firebaseUser.getUid()).child("playingExtra").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists() || dataSnapshot.getValue() == null) {

                    Toast.makeText(getActivity(), "NULL", Toast.LENGTH_SHORT).show();

                }
                else{
                    //Boolean playingExtra = Boolean.valueOf(dataSnapshot.getValue().toString());
                    Boolean playingExtra = dataSnapshot.getValue(Boolean.class);
                    switchPlaying.setChecked(playingExtra);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();

            }
        });
    }


        public void checkingNumberInMembers(final String userPhoneNum, String groupId){
        DatabaseReference mDatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupId).child("members");
        Log.d("DUPL0",userPhoneNum);

        Query query = mDatabaseReference;
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // String ds = dataSnapshot.getValue().toString();
                if (dataSnapshot.getValue() != null || !dataSnapshot.exists() ) {

                    //   Log.d("DUPL1", ds);
                    final Player mPlayer = dataSnapshot.getValue(Player.class);
                    String playerPhone = mPlayer.getPhoneNum();
                    if (playerPhone != null){
                        // Log.d("DUPL2", playerPhone);

                        if (playerPhone == null || playerPhone.equals("") || playerPhone.length() == 0) {
                            Toast.makeText(getActivity(), "****NOT FOUND****", Toast.LENGTH_SHORT).show();
                        } else {
                            //TODO
                            //   Log.d("DUPL 3", playerPhone);
                            //   Log.d("DUPL 4", userPhoneNum);

                            if (playerPhone != null) {

                                if (playerPhone.equals(userPhoneNum)) {
                                    final String groupId = mPlayer.getGroupId();
                                    final String playerPushKey = dataSnapshot.getKey();


                                    usersDatabase.child(firebaseUser.getUid()).child("playingExtra").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null){
                                                //    Log.d("DUPL 5", groupId);
                                                //  Log.d("DUPL 6", playerPushKey);

                                                Boolean em = dataSnapshot.getValue(Boolean.class);

                                                //Boolean em = mPlayer.getPlayingExtra(Boolean.class);

                                                Toast.makeText(getActivity(), "FOUND HERE" + " " + groupId, Toast.LENGTH_SHORT).show();
                                                //  Log.d("DUPL 8", mPlayer.toString());
                                                // TODO Why isnt this working - USE THE MOVE DATA METHOD - CURRRENTLY NOT COPYING DATA OVER PROPERTLY
                                                  groupsDatabase.child(groupId).child("members").child(playerPushKey).child("playingExtra").setValue(em);

                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }

                                    });


                                } else if (playerPhone == null) {
                                    Toast.makeText(getActivity(), "3 CHECKECK", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "THERE IS A USER IN DB WITH PHONE NULL ", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                Toast.makeText(getActivity(), "USER PHONE NULL ", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }else{
                        Toast.makeText(getActivity(), "player phone null", Toast.LENGTH_SHORT).show();

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

                    Player mPlayer = dataSnapshot.getValue(Player.class);
                    String userPhone = mPlayer.getPhoneNum();

                    Group mGroup = dataSnapshot.child("groups").getValue(Group.class);
                    String groupId = mGroup.getGroupId();
                    //   String p = isPlayingExtraMatch;
                    //    Log.d("DUPL8",p);

                    //     Boolean extraMatch= Boolean.valueOf(p);
                    //    Log.d("extraMatch",extraMatch.toString());
                    //    mPlayer.setPlayingExtra(extraMatch);

                    //   groupsDatabase.child(groupId).child("members").child(playerPushKey).setValue(mPlayer);

                }else{

                    //TODO
                }
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
                            Toast.makeText(getActivity(), "COPY FAILED", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "COPY SUCCESS", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "onCancelled- copy fail", Toast.LENGTH_SHORT).show();

            }
        });

    }


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
