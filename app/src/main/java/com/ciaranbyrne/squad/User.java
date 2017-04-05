package com.ciaranbyrne.squad;

/**
 * Created by ciaranbyrne on 05/03/2017.
 */

public class User {

    public String mName;
    public Boolean mPlaying;

    // Firebase variables for user
    public String email;
    public Boolean emailVerified;
   // public String mUid;

    public User(){

    }

    public User ( String name, String email, Boolean playing){

      //  mUid = uid;
        mName = name;
        email = email;
        mPlaying = playing;

        emailVerified = emailVerified;
    }

   public String getName(){
       return mName;
   }

   public void setName(String name){
       this.mName = name;
   }
/*
   public String getUid(){
       return mUid;
   }

   public void setUid(String uid){
       this.mUid = uid;
   }
*/
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


    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
