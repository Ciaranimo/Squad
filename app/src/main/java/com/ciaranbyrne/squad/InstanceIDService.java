package com.ciaranbyrne.squad;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by ciaranbyrne on 13/05/2017.
 */

// Code Ref - https://firebase.google.com/docs/cloud-messaging/android/client & https://www.codeproject.com/articles/1121218/android-firebase-cloud-messaging-tutorial


public class InstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token refresh",   refreshedToken);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(firebaseUser.getUid())
                    .child("refreshedToken")
                    .setValue(refreshedToken);
        }
    }


}
