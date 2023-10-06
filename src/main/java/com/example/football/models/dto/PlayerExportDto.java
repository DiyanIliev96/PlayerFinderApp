package com.example.football.models.dto;

import com.example.football.models.entity.Position;

public class PlayerExportDto {

    private String firstName;

    private String lastName;

    private Position position;

    private String teamName;

    private String stadiumName;

    public PlayerExportDto() {
    }

    public PlayerExportDto(String firstName, String lastName, Position position, String teamName, String stadiumName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.teamName = teamName;
        this.stadiumName = stadiumName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Player - %s %s",getFirstName(),getLastName())).append(System.lineSeparator());
        sb.append("\t").append(String.format("Position - %s",getPosition())).append(System.lineSeparator());
        sb.append("\t").append(String.format("Team - %s",getTeamName())).append(System.lineSeparator());
        sb.append("\t").append(String.format("Stadium - %s",getStadiumName())).append(System.lineSeparator());
        return sb.toString().trim();
    }
}
