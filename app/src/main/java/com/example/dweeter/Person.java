package com.example.dweeter;

import java.io.Serializable;

public class Person implements Serializable {
    public String name;
    public String url;
    public String uid;

    public Person() {
    }

    public Person(String name, String url, String uid) {
        this.name = name;
        this.url = url;
        this.uid = uid;
    }
}
