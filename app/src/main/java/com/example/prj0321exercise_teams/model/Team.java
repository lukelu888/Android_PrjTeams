package com.example.prj0321exercise_teams.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Team implements Serializable {
    private int id;
    private String name;
    private String photo;
    private Project project;
    private ArrayList<String> members;

    public Team() {
    }

    public Team(int id, String name, String photo, Project project, ArrayList<String> members) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.project = project;
        this.members = members;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id:" + id +
                ", name:'" + name + '\'' +
                ", photo:'" + photo + '\'' +
                ", project:" + project +
                ", members:" + members +
                '}';
    }
}
