package com.nativos.forumriu.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Jordan on 25-Oct-16.
 */

public class DebateModel implements Parcelable{

    private int id;
    private String name;
    private String date;
    private boolean isActive;

    public DebateModel() {
    }

    public DebateModel(String date, int id, boolean isActive, String name) {
        this.date = date;
        this.id = id;
        this.isActive = isActive;
        this.name = name;
    }

    protected DebateModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        date = in.readString();
        isActive = in.readByte() != 0;
    }

    public static final Creator<DebateModel> CREATOR = new Creator<DebateModel>() {
        @Override
        public DebateModel createFromParcel(Parcel in) {
            return new DebateModel(in);
        }

        @Override
        public DebateModel[] newArray(int size) {
            return new DebateModel[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(date);
        dest.writeByte((byte) (isActive ? 1 : 0));
    }
}
