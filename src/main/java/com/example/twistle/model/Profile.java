package com.example.twistle.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "profile")
public class Profile{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private Integer points;
    private Integer streak;
    private Timestamp createdAt;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void addPoints(Integer pointsToAdd){
        if (this.points == null){
            setPoints(pointsToAdd);
            return;
        }
        points += pointsToAdd;
    }

    public void removePoints(Integer pointsToRemove){
        if (this.points == null || this.points - pointsToRemove <= 0){
            setPoints(0);
            return;
        }
        points -= pointsToRemove;
    }

    // The following methods are not used yet:

    public void setUsername(String username){
        this.username = username;
    }

    public int getPoints(){
        if (points == null){
            points = 0;
        }
        return points;
    }

    public void setPoints(int points){
        this.points = points;
    }

//    public int getStreak(){
//        return streak;
//    }
//
//    public void setStreak(int streak){
//        this.streak = streak;
//    }

    public Timestamp getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAtt(Timestamp created_at){
        this.createdAt = created_at;
    }
}