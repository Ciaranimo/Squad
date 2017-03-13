package com.ciaranbyrne.squad;

import android.net.Uri;

/**
 * Created by ciaranbyrne on 05/03/2017.
 */

public class User {

    private String name;
    private Boolean mPlaying;

    // Firebase variables for user
    private String email;
    private Uri photoUrl;
    private Boolean emailVerified;
    private String uid;

    public User(){

    }

    public User (String name, String uid, Boolean playing){

        name = name;
        uid = uid;
        mPlaying = playing;

        email = email;
        photoUrl = photoUrl;
        emailVerified = emailVerified;


    }

   public String getName(){
       return name;
   }

   public void setName(String name){
       this.name = name;
   }

   public String getUid(){
       return uid;
   }

   public void setUid(String uid){
       this.uid = uid;
   }

   public Boolean getPlaying(){
       return mPlaying;
   }

   public void setPlaying(Boolean playing){
       this.mPlaying = playing;
   }

   public String getEmail(){
       return email;
   }

   public void setEmail(String email){
       this.email = email;
   }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
