package com.example.digibro;

public class UserRepository {
    private static UserRepository instance;
    private String userEmail;

    private UserRepository() {
        // Private constructor to prevent instantiation
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String email) {
        userEmail = email;
    }
}




//    UserRepository userRepository = UserRepository.getInstance();
//    String userEmail = userRepository.getUserEmail();


//    UserRepository userRepository = UserRepository.getInstance();
//userRepository.setUserEmail(null);
// we have to set null the email after complt the task in the app


