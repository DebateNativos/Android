package com.nativos.forumriu.models;

/**
 * Created by Jordan on 25-Nov-16.
 */

public class PlayerModel {

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


}
