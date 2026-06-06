package com.example.twistle.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * Model class for a user profile.
 * Includes getters and setters for attributes.
 */
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

    /**
     * Gets user id.
     * @return Returns user id.
     * @author Benjamin Torsson
     */
    public Long getId(){
        return id;
    }

    /**
     * Sets user id.
     * @author Benjamin Torsson
     */
    public void setId(Long id){
        this.id = id;
    }

    /**
     * Gets username.
     * @return Returns username.
     * @author Benjamin Torsson
     */
    public String getUsername(){
        return username;
    }

    /**
     * Gets (encrypted) user password.
     * @return Returns user password.
     * @author Benjamin Torsson
     */
    public String getPassword(){
        return password;
    }

    /**
     * Sets (encrypted) password.
     * @param password Password to be set.
     */
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * Adds points to profile.
     * @param pointsToAdd Amount of points to add.
     * @author Marua Alkhafadji
     */
    public void addPoints(Integer pointsToAdd){
        if (this.points == null){
            setPoints(pointsToAdd);
            return;
        }
        points += pointsToAdd;
    }

    /**
     * Removes points from profile.
     * @param pointsToRemove Amount of points to remove.
     * @author Benjamin Torsson
     */
    public void removePoints(Integer pointsToRemove){
        if (this.points == null || this.points - pointsToRemove <= 0){
            setPoints(0);
            return;
        }
        points -= pointsToRemove;
    }

    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Gets the amount of points the profile has.
     * @return Returns the amount of the profile's points.
     * @author Benjamin Torsson
     */
    public int getPoints(){
        if (points == null){
            points = 0;
        }
        return points;
    }

    /**
     * Sets the amount of points of the profile.
     * @param points Amount of points to be set.
     * @author Benjamin Torsson
     */
    public void setPoints(int points){
        this.points = points;
    }

    /**
     * Getter for streak.
     * @return Returns amount of streak days.
     * @author Savannah Norgren
     */
    public int getStreak(){
        return streak;
    }
    /**
     * Setter for streak.
     * @author Savannah Norgren
     */
    public void setStreak(int streak){
       this.streak = streak;
    }
}