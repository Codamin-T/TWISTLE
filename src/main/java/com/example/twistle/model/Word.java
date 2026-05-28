package com.example.twistle.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "word")
public class Word{

    @Id
    @Column(name = "wordText")
    private String wordText;
    private Date lastUsed;

    public String getWordText(){
        return wordText;
    }
    
    public void setWordText(String wordText){
        this.wordText = wordText;
    }

    public void setLastUsed(Date lastUsed){
        this.lastUsed = lastUsed;
    }
}

