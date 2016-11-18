package com.nativos.forumriu.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jordan on 22-Oct-16.
 */

public class UserModel implements Parcelable{

    private int idUser;
    private String name;
    private String lastname;
    private String lastname2;
    private String email;
    private String status;


    public UserModel() {
    }

    public UserModel(String email, int idUser, String lastname2, String lastname, String name, String status) {
        this.email = email;
        this.idUser = idUser;
        this.lastname2 = lastname2;
        this.lastname = lastname;
        this.name = name;
        this.status = status;
    }

    protected UserModel(Parcel in) {
        idUser = in.readInt();
        name = in.readString();
        lastname = in.readString();
        lastname2 = in.readString();
        email = in.readString();
        status = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idUser);
        dest.writeString(name);
        dest.writeString(lastname);
        dest.writeString(lastname2);
        dest.writeString(email);
        dest.writeString(status);
    }
}
