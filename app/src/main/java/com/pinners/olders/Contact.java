package com.pinners.olders;

/**
 * Created by Administrator on 2016-06-21.
 */
public class Contact {
    private int id;
    private String name;
    private String phone_number;
    private String favorite;

    public Contact(){ }
    public Contact(int id, String name, String phone_number, String favorite){
        this.id = id;
        this.name = name;
        this.phone_number = phone_number;
        this.favorite = favorite;
    }

    public Contact(String name, String phone_number, String favorite){
         this.name = name;
         this.phone_number = phone_number;
         this.favorite = favorite;
    }

    public int getID(){
        return this.id;
    }
    public void setID(int id){
        this.id = id;
    }

    public String getName(){return this.name;}
    public void setName(String name){
        this.name = name;
    }

    public String getPhoneNumber(){
        return this.phone_number;
    }
    public void setPhoneNumber(String phone_number){
        this.phone_number = phone_number;
    }

    public String getFavorite(){
        return this.favorite;
    }
    public void setFavorite(String favorite){
        this.favorite = favorite;
    }


}
