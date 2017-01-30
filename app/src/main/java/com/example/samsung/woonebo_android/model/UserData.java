package com.example.samsung.woonebo_android.model;

/**
 * Created by SAMSUNG on 2016-11-14.
 */
public class UserData {
    String email;
    String password;

    public UserData(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
