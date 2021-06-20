package com.example.keepeverythingsafe;


public class User {
    public String username, phoneNO,password,email,profilepic;

    public User(String username, String email, String phoneNO, String password , String profilepic){
        this.username = username;
        this.phoneNO = phoneNO;
        this.email = email;
        this.phoneNO = phoneNO;
        this.password = password;
        this.profilepic = profilepic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNO() {
        return phoneNO;
    }

    public void setPhoneNO(String phoneNO) {
        this.phoneNO = phoneNO;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }


}