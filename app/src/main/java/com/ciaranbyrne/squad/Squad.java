package com.ciaranbyrne.squad;

/**
 * Created by ciaranbyrne on 05/03/2017.
 */

public class Squad {

    private String mName;
    private String mUid;
    private Boolean mPlaying;

    public Squad(){

    }

    public Squad (String name, String uid, Boolean playing){

        mName = name;
        mUid = uid;
        mPlaying = playing;
    }

   public String getName(){
       return mName;
   }

   public void setName(String name){
       mName = name;
   }

   public String getUid(){
       return mUid;
   }

   public void setUid(String uid){
       mUid = uid;
   }

   public Boolean getPlaying(){
       return mPlaying;
   }

   public void setPlaying(Boolean playing){
       mPlaying = playing;
   }



}
