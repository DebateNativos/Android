package com.nativos.forumriu.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jordan on 25-Nov-16.
 */

public class PlayerModel implements Parcelable {

    private int role;
    private int warnings;
    private int debate;
    private String team;
    private boolean isTalking;
    private int minutes;

    public PlayerModel() {
    }

    public PlayerModel(int debate, boolean isTalking, int minutes, int role, String team, int warnings) {
        this.debate = debate;
        this.isTalking = isTalking;
        this.minutes = minutes;
        this.role = role;
        this.team = team;
        this.warnings = warnings;
    }

    protected PlayerModel(Parcel in) {
        role = in.readInt();
        warnings = in.readInt();
        debate = in.readInt();
        team = in.readString();
        isTalking = in.readByte() != 0;
        minutes = in.readInt();
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

    public boolean getIsTalking() {
        return isTalking;
    }

    public void setIsTalking(boolean talking) {
        isTalking = talking;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

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
        dest.writeString(team);
        dest.writeByte((byte) (isTalking ? 1 : 0));
        dest.writeInt(minutes);
    }
}
