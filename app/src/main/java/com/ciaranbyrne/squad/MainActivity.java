package com.ciaranbyrne.squad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private DatabaseReference rootDatabase;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String mUsername;
    private String mUserId;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences mSharedPreferences;

    private Button btnEditSquad;
    private TextView tvDisplayName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mUsername = ANONYMOUS;

        //Initialize Firebase components
        // get database reference to read data
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        rootDatabase = mFirebaseDatabase.getReference();
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        groupsDatabase = FirebaseDatabase.getInstance().getReference().child("groups");

        //  mUserId = mFirebaseAuth.getCurrentUser().getUid();

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

        // Firebase Authentication state listener initialize - from Firebase docs
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            //on signedInInitialize called
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //check if user logged in
                if (user != null) {
                    //user is signed in
                    //calls methods at boottom
                    onSignedInInitialize(user.getDisplayName(), user.getUid());
                    // add user instance to realtime database

                    writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail(), null, null, null,null);
                    readUserInfo(user.getDisplayName());
                    Toast.makeText(MainActivity.this, "Signed in", Toast.LENGTH_SHORT).show();


                } else {
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
        //  User loggedInUser = new User();
        // String userPhone = loggedInUser.getPhoneNum();
        //  checkingNumber(userPhone);

    }// End of onCreate

    // METHOD TO WRITE NEW USER WITHOUT DUPLCIATION
    private void writeNewUser(final String userId, final String name, final String email,
                              final String phoneNum, final String searchNum, final Boolean playingExtra,  String groupId) {
        final User user = new User(userId, name, email, phoneNum, searchNum, playingExtra, groupId);

        // Checks if user exists
        if (usersDatabase != null) {
            usersDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("MAIN", dataSnapshot.toString());
                    if (dataSnapshot.getValue() != null) {
                        //user exists, do something
                        //  Toast.makeText(MainActivity.this,"User exists ",Toast.LENGTH_SHORT).show();

                        userHasPhoneNumber(mUserId);

                    } else {
                        //user does not exist, add to DB
                        usersDatabase.child(userId).setValue(user);
                        userHasPhoneNumber(mUserId);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    //  Write new group so we dont get null error with new users
    private void writeNewGroup(String userId) {
        final String groupId = userId;

        groupsDatabase.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            Group group = new Group(groupId, groupId);

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                } else {
                    //group does not exist, add to DB
                    groupsDatabase.child(groupId).setValue(group);
                    //     Toast.makeText(MainActivity.this,"User doesnt exist ",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //TODO Check if user has been added to match
    private void userAddedToMatch(final String userId) { // 2
        if (userId != null) {
            usersDatabase.child(userId).child("groups").child("groupId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String ds = dataSnapshot.toString();
                    Log.d("DS", ds);
                    if (ds.length() == 0 || dataSnapshot.getValue() == null) {
                        Log.d("DS", dataSnapshot.toString());
                        // user has not been added to a match
                        Toast.makeText(MainActivity.this, "User has not been added to any matches ", Toast.LENGTH_SHORT).show();

                    } else {
                    //    Toast.makeText(MainActivity.this, "User has been added to a match ", Toast.LENGTH_SHORT).show();

                        if(usersDatabase.child(userId).child("playingExtra") != null){
                            // value in here alread
                            Toast.makeText(MainActivity.this, "VALUE IN USERS PLAY EXTRA NODE ALREADY ", Toast.LENGTH_SHORT).show();

                        }else{
                            // empty
                            usersDatabase.child(userId).child("playingExtra").setValue(false);
                        }


                        // Begin the transaction
                        FragmentTransaction fragT = getSupportFragmentManager().beginTransaction();
                        // Replace the contents of the container with the new fragment
                        fragT.replace(R.id.your_placeholder, new DisplayMatchFragment());
                        // or ft.add(R.id.your_placeholder, new FooFragment());
                        // Complete the changes added above
                        fragT.commit();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "ERROR ", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    //  Check if user has input phone number -
    private void userHasPhoneNumber(final String userId) {
        if (userId != null) {
            // Checks if phonNum exists

            usersDatabase.child(userId).child("phoneNum").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    String ds = dataSnapshot.toString();
                    if (ds.length() == 0 || dataSnapshot.getValue() == null) {
                        //user does not have phone number, do something else
                        Toast.makeText(MainActivity.this, "User does NOT have phone num ", Toast.LENGTH_SHORT).show();

                        // CODE REF - http://stackoverflow.com/questions/14347588/show-hide-fragment-in-android
                        // Begin the transaction
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        // Replace the contents of the container with the new fragment
                        ft.replace(R.id.your_placeholder, new InputPhoneFragment());

                        // or ft.add(R.id.your_placeholder, new FooFragment());
                        // Complete the changes added above
                        ft.commit();
                    } else {

                        //user has phone number, do something
                      //  Toast.makeText(MainActivity.this, "User has phone Num ", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "User has phone");
                        userAddedToMatch(userId); // 3
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
        }
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

            // TODO IF STATEMENT TO FOR USER ADDED TO MATCH
            if (usersDatabase.child(user.getUid()).child("phoneNum") == null) {

                //user does not have phone do not start check for match
                Toast.makeText(MainActivity.this, "User does not have phone do not start match check", Toast.LENGTH_SHORT).show();
            } else {
                // user has phoe num
                userAddedToMatch(user.getUid()); // 1

            }

        }
    }




    //read listener attached /  //
    private void onSignedInInitialize(String username, String userId) {
        mUsername = username;
        mUserId = userId;

        writeNewGroup(mUserId);

        //call method when user signed in
        //   attachDatabaseReadListener();
    }

    // TODO
    private void attachDatabaseReadListener() {

    }

    //get logged in user information
    public String userInfo() {
        // Access user information
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            String userId = user.getUid();
            //Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
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
    public void onResume() {
        super.onResume();
        //Auth state change called - database listener attacehd
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    // Sign out
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in Cancelled ", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }




    private void onSignedOutCleanUp() {
        // unset user name
        mUsername = ANONYMOUS;
        //clear messages from adapter, user not signed in should be able to see msgs
        // mMessageAdapter.clear();
        //detach listener
        //  detachDatabaseReadListener();
    }
}