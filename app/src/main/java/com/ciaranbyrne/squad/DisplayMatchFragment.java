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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        String uId = firebaseUser.getUid();

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


        readAddedBy(uId);
      //  readMatchDay(uId);
      //  readMatchTime(uId);

        return view;
    }// end onCreate


    //Read added by
    private void readAddedBy(String uid) {
        usersDatabase.child(uid).child("groups").child("adminName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ds = dataSnapshot.toString();
                if (!dataSnapshot.exists() || ds.equals("")) {

                } else {

                    Toast.makeText(getActivity(), " Read error", Toast.LENGTH_SHORT).show();

                    Log.e("Read Error", dataSnapshot.toString());

                    String adminName = dataSnapshot.getValue().toString();
                    tvAddedBy.setText(adminName);
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
                if (dataSnapshot.exists()) {
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
                if (dataSnapshot.exists()) {
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
