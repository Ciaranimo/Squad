package com.ciaranbyrne.squad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    //CONSTANTS
    public static final int RC_SIGN_IN = 1;
    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";


    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersDatabase;
    private DatabaseReference groupsDatabase;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String mUsername;
    private String userId;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences mSharedPreferences;

    private Button btnEditSquad;
    private TextView tvDisplayName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mUsername = ANONYMOUS;

        //Initialize Firebase components
        // get database reference to read data
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        groupsDatabase = FirebaseDatabase.getInstance().getReference("groups");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        userId = mFirebaseAuth.getCurrentUser().getUid();

        //Initialize views
        tvDisplayName = (TextView) findViewById(R.id.tv_display_name);

        // Intent to Edit players screen
        btnEditSquad = (Button) findViewById(R.id.btnEditSquad);

        btnEditSquad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Explicit Intent by specifying its class name
                Intent i = new Intent(MainActivity.this, MatchActivity.class);

                // Starts TargetActivity
                startActivity(i);
            }
        });

        // Firebase Authentication state listener initialize TODO
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            //on signedInInitialize called
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //check if user logged in
                if(user != null){
                    //user is signed in
                    //calls methods at boottom
                    onSignedInInitialize(user.getDisplayName());
                    // add user instance to realtime database
                   writeNewUser(user.getUid(),user.getDisplayName(),user.getEmail(),"");
                    readUserInfo(user.getDisplayName());
                    Toast.makeText(MainActivity.this, "Signed in" , Toast.LENGTH_SHORT).show();
                }else{
                    //user is not signed in - commence with login flow (built in to firebase)
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        userHasPhoneNumber(userId);


    }// End of onCreate

    // METHOD TO WRITE NEW USER WITHOUT DUPLCIATION
    private void writeNewUser(final String userId, final String name, final String email, final String phoneNum) {
        final User user = new User(userId, name, email, phoneNum);

        // Checks if user exists
        usersDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    //user exists, do something
                  //  Toast.makeText(MainActivity.this,"User exists ",Toast.LENGTH_SHORT).show();

                }else {
                    //user does not exist, do something else
                    usersDatabase.child(userId).setValue(user);
               //     Toast.makeText(MainActivity.this,"User doesnt exist ",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // TODO Check if user has input phone number
    private void userHasPhoneNumber(String userId){
        // Checks if user exists
        usersDatabase.child(userId).child("phoneNum").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.getValue().equals("") ){
                    //user has phone, do something
                    Toast.makeText(MainActivity.this,"User has phone Num ",Toast.LENGTH_SHORT).show();

                }else {
                    //user does not have phone number, do something else
                    Toast.makeText(MainActivity.this,"User doesnt have phone num ",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //GET CURRENT USER INFO

    private void readUserInfo(String mUsername) {
        user = mFirebaseAuth.getCurrentUser();
        if (user == null) {
            // Not signed in, launch the Sign In activity
            //   startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = user.getDisplayName();

           /* if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            */

            tvDisplayName.setText(userInfo());
        }
    }


    //read listener attached /  //
    private void onSignedInInitialize(String username) {
        mUsername = username;
        //call method when user signed in
     //   attachDatabaseReadListener();
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


    @Override
    public void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //Auth state change called - database listener attacehd
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    // Sign out
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out_menu:
                //sign out
                AuthUI.getInstance().signOut(this);
                // user is now signed out

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

    //TODO
    @Override
    public void onActivityResult (int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this,"Sign in Cancelled ",Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    //
    private void onSignedOutCleanUp(){
        // unset user name
        mUsername = ANONYMOUS;
        //clear messages from adapter, user not signed in should be able to see msgs
       // mMessageAdapter.clear();
        //detach listener
      //  detachDatabaseReadListener();
    }

    private void checkUserPhoneNum(){
        //if(usersDatabase.child(userId))
    }

}
