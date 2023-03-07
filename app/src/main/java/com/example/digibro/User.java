package com.example.digibro;

public class User {
    private static User instance;
    private String user;

    private User() {
        // Private constructor to prevent instantiation
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String domain) {
        user = domain;
    }
}



  //  User user = User.getInstance();
//    String user = user.getUser();


//    User user = User.getInstance();
//user.setUser(null);
// we have to set null the email after complt the task in the app