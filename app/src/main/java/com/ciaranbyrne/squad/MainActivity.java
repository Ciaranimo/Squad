package com.ciaranbyrne.squad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.lang.Boolean.TRUE;


public class MainActivity extends AppCompatActivity {

    //CONSTANTS
    public static final int RC_SIGN_IN = 1;
    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";


    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private DatabaseReference usersDatabase;


    private FirebaseAuth.AuthStateListener mAuthStateListener;


    private String mUsername;


    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences mSharedPreferences;

    private Button btnEditSquad;
    private TextView tvDisplayName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mUsername = ANONYMOUS;

        //Initialize Firebase components
        // get database reference to read data
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        // Initialize Firebase Auth - get user and check logged in

        //GET CURRENT USER INFO
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (user == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = user.getDisplayName();
           /* if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            */
            tvDisplayName = (TextView) findViewById(R.id.tv_display_name);

            tvDisplayName.setText(userInfo());
        }

        // add user instance to realtime database
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    //mDatabase.push().setValue(new User(etNewPlayer.getText().toString(),"1234", TRUE));

                    //usersDatabase.push().setValue(new User(user.getDisplayName(), user.getUid(), TRUE));
                    writeNewUser(user.getUid(),user.getDisplayName(),user.getEmail(),TRUE);


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        // Intent to Edit pplayers screen
        btnEditSquad = (Button) findViewById(R.id.btnEditSquad);

        btnEditSquad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Explicit Intent by specifying its class name
                Intent i = new Intent(MainActivity.this, EditPlayersActivity.class);

                // Starts TargetActivity
                startActivity(i);
            }
        });




    }// End of onCreate

    // METHOD TO WRITE NEW USER WITHOUT DUPLCIATION
    private void writeNewUser(String userId, String name, String email, Boolean playing) {
        User user = new User(name, email, TRUE);

        usersDatabase.child(userId).setValue(user);
    }

    //get logged in user information
    public String userInfo(){
        // Access user information
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            return name;
        }
        return "";

    }

/*
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    */



    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                FirebaseAuth.getInstance().signOut();

                // mFirebaseAuth.signOut();
                //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch(item.getItemId()){
            case R.id.sign_out_menu:
                //sign out
                FirebaseAuth.getInstance().signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */

}
