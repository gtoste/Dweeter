package com.example.dweeter;

import java.io.Serializable;

public class Comment implements Serializable {
    public String accountName;
    public String accountUrl;
    public String message;
    public String dateOfCreation;

    public Comment() {
    }

    public Comment(String accountName, String accountUrl, String message, String dateOfCreation) {
        this.accountName = accountName;
        this.accountUrl = accountUrl;
        this.message = message;
        this.dateOfCreation = dateOfCreation;
    }
}
