package com.example.dweeter;

import java.io.Serializable;

public class Comment implements Serializable {
    public String accountName;
    public String accountUrl;
    public String message;

    public Comment() {
    }

    public Comment(String accountName, String accountUrl, String message) {
        this.accountName = accountName;
        this.accountUrl = accountUrl;
        this.message = message;
    }
}
