package com.ciaranbyrne.squad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    //CONSTANTS
    public static final int RC_SIGN_IN = 1;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Firebase Authentication state listener
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //check if this user is logged in
                if (user != null){
                    //user is signed in
                    Toast.makeText(MainActivity.this, "You;re signed in",Toast.LENGTH_SHORT).show();

                }else {
                    //user is signed out - sign in flow needed
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    // saves users credentials to try log them in - set to false
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER
                                    )
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };
    }

    protected void onPause(){
        super.onPause();
        //remove Authentication state listener
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    protected void onResuem(){
        super.onResume();
        //attach Firebase Authentication state listener
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
