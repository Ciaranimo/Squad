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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InputPhoneFragment extends Fragment{

    private EditText etInputNum;
    private Button btnSavePhoneNum;

    //Firebase database

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersDatabase;
    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_phone_num_fragment, container, false);

        //FIREBASE
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        String uId = firebaseUser.getUid();

        final FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabase = mFirebaseDatabase.getReference().child("users");
        // Initialize variables
        etInputNum = (EditText) view.findViewById(R.id.etInputPhoneNum);
        btnSavePhoneNum = (Button)view.findViewById(R.id.btnSavePhoneNum);


        //button to edit players
        //set on click listener

        // TODO Why is this not saving the phone number -seems to work after first login
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
}
