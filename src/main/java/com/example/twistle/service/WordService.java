package com.example.twistle.service;

import com.example.twistle.model.Word;
import com.example.twistle.repository.WordRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class WordService{

    private static final Logger log = LoggerFactory.getLogger(WordService.class);

    @Autowired
    HttpSession session;

    private WordRepository wordRepository;

    public WordService(WordRepository wordRepository){
        this.wordRepository =  wordRepository;
    }

    // Gets the daily word for the parameter length.
    @Cacheable(value="word", key="#length")
    public Word getDailyWord(int length){

        List<Word> words = wordRepository.findAllRandomByLengthNotRecent(length);

        if (words.isEmpty()){
            log.warn("No non-recent words of length {}; falling back to full pool.", length);
            words = wordRepository.findAllByLength(length);
            if (words.isEmpty()){
                throw new IllegalStateException("No words of length " + length + " in database");
            }
        }

        long dateToday = LocalDate.now().toEpochDay();
        int dailyIndex = (int) Math.floorMod(dateToday, words.size());

        return words.get(dailyIndex);
    }

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
