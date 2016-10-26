package com.nativos.forumriu.models;

import java.util.Date;

/**
 * Created by Jordan on 25-Oct-16.
 */

public class DebateModel {

    private int id;
    private String name;
    private Date date;
    private boolean isActive;

    public DebateModel() {
    }

    public DebateModel(Date date, int id, boolean isActive, String name) {
        this.date = date;
        this.id = id;
        this.isActive = isActive;
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
}
