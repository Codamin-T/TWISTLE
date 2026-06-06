package com.example.twistle.model;

import jakarta.persistence.*;

import java.sql.Date;

/**
 * Model class for a word.
 * Includes getters and setters for attributes.
 */
@Entity
@Table(name = "word")
public class Word{

    @Id
    @Column(name = "wordText")
    private String wordText;
    private Date lastUsed;

    /**
     * Gets the text of the word.
     * @return Returns the word text as a String.
     * @author Benjamin Torsson
     */
    public String getWordText(){
        return wordText;
    }

    /**
     * Sets the text of a word.
     * @param wordText Text to be set.
     * @author Benjamin Torsson
     */
    public void setWordText(String wordText){
        this.wordText = wordText;
    }

    /**
     * Sets the date of when the word was last used.
     * @param lastUsed The date to be set.
     * @author Benjamin Torsson
     */
    public void setLastUsed(Date lastUsed){
        this.lastUsed = lastUsed;
    }
}

