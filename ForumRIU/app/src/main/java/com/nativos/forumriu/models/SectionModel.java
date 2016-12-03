package com.nativos.forumriu.models;

/**
 * Created by Jordan on 27-Nov-16.
 */

public class SectionModel {

    String name;
    int minutes;
    boolean activeSection;
    int sectionNumber;

    public SectionModel() {
    }

    public SectionModel(boolean activeSection, int minutes, String name) {
        this.activeSection = activeSection;
        this.minutes = minutes;
        this.name = name;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(int sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public boolean getActiveSection() {
        return activeSection;
    }

    public void setActiveSection(boolean activeSection) {
        this.activeSection = activeSection;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

