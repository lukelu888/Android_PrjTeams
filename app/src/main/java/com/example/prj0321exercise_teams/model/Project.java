package com.example.prj0321exercise_teams.model;

import java.io.Serializable;

public class Project implements Serializable {
    private String title;
    private String description;
    private int total;

    public Project() {
    }

    public Project(String title, String description, int total) {
        this.title = title;
        this.description = description;
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Project{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", total=" + total +
                " user stories }";
    }
}
