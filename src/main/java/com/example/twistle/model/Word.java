package com.example.twistle.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "word")
public class Word {

    @Id
    @Column(name = "word_text")
    private String wordText;
    private Timestamp last_used;


    public String getWord_text() {
        return wordText;
    }
    public void setWord_text(String wordText) {
        this.wordText = wordText;
    }
    public Timestamp getLast_used() {
        return last_used;
    }
    public void setLast_used(Timestamp last_used) {
        this.last_used = last_used;
    }
}

