package com.example.jproject;

public class User {
    private int UserID;
    private String Username;
    private String Email;
    private String Phone;
    private String Password;

    public User(){
        UserID=0;
        Username="shreya";
        Email="sh@gmail.com";
        Phone="9876543234";
        Password="passshreya";
    }

    public int getUserID() {
        return UserID;
    }

    public String getEmail() {
        return Email;
    }

    public String getPassword() {
        return Password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setUsername(String username) {
        Username = username;
    }
}


