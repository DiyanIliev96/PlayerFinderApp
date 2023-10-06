package com.example.football.models.dto;

import com.example.football.models.entity.Position;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerImportDto {

    @XmlElement(name = "first-name")
    @NotNull
    @Size(min = 2)
    private String firstName;
    @XmlElement(name = "last-name")
    @NotNull
    @Size(min = 2)
    private String lastName;
    @NotNull
    @Email
    private String email;
    @NotNull
    @XmlElement(name = "birth-date")
    private String birthDate;
    @NotNull
    private Position position;
    @NotNull
    private TownBaseDto town;
    @NotNull
    private TeamBaseDto team;
    @NotNull
    private StatBaseDto stat;

    public PlayerImportDto() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public TownBaseDto getTown() {
        return town;
    }

    public void setTown(TownBaseDto town) {
        this.town = town;
    }

    public TeamBaseDto getTeam() {
        return team;
    }

    public void setTeam(TeamBaseDto team) {
        this.team = team;
    }

    public StatBaseDto getStat() {
        return stat;
    }

    public void setStat(StatBaseDto stat) {
        this.stat = stat;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
