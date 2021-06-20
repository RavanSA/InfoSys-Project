package com.example.keepeverythingsafe;

public class Info {

    public String email;
    public String title;
    public String password;

    public Info() {

    }

    public Info(String email,String password, String title ) {
        this.email = email;
        this.title = title;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }




}
