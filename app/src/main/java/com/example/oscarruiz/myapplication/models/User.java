package com.example.oscarruiz.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Oscar Ruiz on 09/08/2017.
 */

public class User implements Serializable {

    /**
     * User ID
     */
    @SerializedName("id")
    private int id;

    /**
     * User name
     */
    @SerializedName("name")
    private String name;

    /**
     * User birthdate
     */
    @SerializedName("birthdate")
    private String birthdate;

    /**
     * Constructor
     */
    public User(String name, String birthdate) {
        this.name = name;
        this.birthdate = birthdate;
    }

    public User(int id, String name, String birthdate) {
        this.id = id;
        this.name = name;
        this.birthdate = birthdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
