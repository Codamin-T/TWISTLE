package com.example.twistle.model;

import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "word")
public class Word {

    @Id
    @Column(name = "word_text")
    private String wordText;
    private Date last_used;
    
    
    
    public String getWord_text() {
        return wordText;
    }
    
    public void setWordText(String wordText) {
        this.wordText = wordText;
    }

    public Date getLast_used() {
        return last_used;
    }
    public void setLast_used(Date last_used) {
        this.last_used = last_used;
    }
}

