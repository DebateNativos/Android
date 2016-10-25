package com.nativos.forumriu.models;

/**
 * Created by Jordan on 22-Oct-16.
 */

public class UserModel {

    private int idUser;
    private String name;
    private String lastname;
    private String lastname2;
    private String email;


    public UserModel() {
    }

    public UserModel(int idUser, String email, String lastname2, String lastname, String name) {
        this.idUser = idUser;
        this.email = email;
        this.lastname2 = lastname2;
        this.lastname = lastname;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastname2() {
        return lastname2;
    }

    public void setLastname2(String lastname2) {
        this.lastname2 = lastname2;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
