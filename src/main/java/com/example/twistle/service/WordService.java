package com.example.twistle.service;

import com.example.twistle.model.Word;
import com.example.twistle.repository.WordRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
public class WordService {

    @Autowired
    HttpSession session;

    private WordRepository wordRepository;

    public WordService(WordRepository wordRepository) {
        this.wordRepository =  wordRepository;
    }

    // Gets the daily word for the parameter length.
    @Cacheable(value="word", key="#length")
    public Word getDailyWord(int length){

        List<Word> words = wordRepository.findAllRandomByLengthNotRecent(length);
        
        long today = LocalDate.now().toEpochDay();
        System.out.println("today: " + today);

        int dailyIndex = (int) (today % words.size());
        System.out.println("dailyIndex: " + dailyIndex);

        Word word = words.get(dailyIndex);
        return word;
    }

    //Sets attribute 'lastUsed' to current date.
    public void setLastUsed(Word word){
        if (session.getAttribute("lastUsed") == session.getAttribute("currentWord")) {
            session.setAttribute("lastUsed", word);
            word.setLastUsed(Date.valueOf(LocalDate.now()));

            wordRepository.save(word);
        }

    }

}
