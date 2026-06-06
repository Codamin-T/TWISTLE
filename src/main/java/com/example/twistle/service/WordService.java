package com.example.twistle.service;

import com.example.twistle.model.Word;
import com.example.twistle.repository.WordRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Service class for Word.
 * Includes logic for setting attributes of a Profile object, and getting a word from the database.
 */
@Service
public class WordService{

    @Autowired
    HttpSession session;

    private WordRepository wordRepository;

    public WordService(WordRepository wordRepository){
        this.wordRepository =  wordRepository;
    }

    /**
     * Getter for the daily word of a specific length.
     * @param length Length of the word to get.
     * @return Returns the daily word with that length.
     */
    // Gets the daily word for the parameter length.
    @Cacheable(value="word", key="#length")
    public Word getDailyWord(int length){

        List<Word> words = wordRepository.findAllRandomByLengthNotRecent(length);
        
        long dateToday = LocalDate.now().toEpochDay();
        System.out.println("today: " + dateToday);

        int dailyIndex = (int) (dateToday % words.size());
        System.out.println("dailyIndex: " + dailyIndex);

        Word word = words.get(dailyIndex);
        return word;
    }

    /**
     * Sets the date for when a word was last used. Uses the current date.
     * @param word Word to be set to the current date.
     * @author Benjamin Torsson
     */
    //Sets attribute 'lastUsed' to current date.
    public void setLastUsed(Word word){
        if (word == null) return;
        if (session.getAttribute("lastUsed") == session.getAttribute("currentWord")){
            session.setAttribute("lastUsed", word);
            word.setLastUsed(Date.valueOf(LocalDate.now()));

            wordRepository.save(word);
        }

    }

}
