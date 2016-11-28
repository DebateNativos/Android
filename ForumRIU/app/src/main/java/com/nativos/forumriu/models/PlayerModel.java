package com.nativos.forumriu.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jordan on 25-Nov-16.
 */

public class PlayerModel implements Parcelable {

    int role;
    int warnings;
    int debate;

    public PlayerModel() {
    }

    public PlayerModel(int debate, int role, int warnings) {
        this.debate = debate;
        this.role = role;
        this.warnings = warnings;
    }

    protected PlayerModel(Parcel in) {
        role = in.readInt();
        warnings = in.readInt();
        debate = in.readInt();
    }

    public static final Creator<PlayerModel> CREATOR = new Creator<PlayerModel>() {
        @Override
        public PlayerModel createFromParcel(Parcel in) {
            return new PlayerModel(in);
        }

        @Override
        public PlayerModel[] newArray(int size) {
            return new PlayerModel[size];
        }
    };

    public int getDebate() {
        return debate;
    }

    public void setDebate(int debate) {
        this.debate = debate;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getWarnings() {
        return warnings;
    }

    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(role);
        dest.writeInt(warnings);
        dest.writeInt(debate);
    }
}
