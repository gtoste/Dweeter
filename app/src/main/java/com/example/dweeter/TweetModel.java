package com.example.dweeter;

import java.io.Serializable;

public class TweetModel implements Serializable {
    public String path;
    public String accountNick;
    public String accountUrl;
    public String message;
    public String linkTo;
    public int likesCount;
    public String dateOfCreation;

    public TweetModel(String path, String accountNick, String accountUrl, String message, String linkTo, int likesCount, String dateOfCreation) {
        this.accountNick = accountNick;
        this.accountUrl = accountUrl;
        this.message = message;
        this.path = path;
        this.linkTo = linkTo;
        this.likesCount = likesCount;
        this.dateOfCreation = dateOfCreation;
    }

    public TweetModel()
    {
    }
}
