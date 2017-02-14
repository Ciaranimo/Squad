package com.ciaranbyrne.squad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.email.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    //CONSTANTS
    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private FirebaseAuth.AuthStateListener mAuthStateListener;


    private String mUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        //Initialize Firebase components
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
           /* if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            */
        }
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
    //TODO
    //sign out button STEP 6 - need to build button xml, getting error on mGoogleApiclient - can try alternate one in FriendlyChat
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */

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
