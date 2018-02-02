package com.zeeroapps.instalogin.Model;

/**
 * Created by razan on 2/1/18.
 */

public class User {

    private String username;
    private String name;
    private String gender;
    private String email;

    public User() {

    }

    public User(String username) {
        this.username= username;

    }


    public String getUsername(){
        return this.username;
    }
}