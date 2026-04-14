package com.example.twistle.service;

import com.example.twistle.model.Word;
import com.example.twistle.repository.WordRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WordService {
    private WordRepository wordRepository;

    public WordService(WordRepository wordRepository) {
        this.wordRepository =  wordRepository;

    }

    @Cacheable(value="word", key="#length")
    public Word setDailyWords(int length){
        List<Word> words = wordRepository.findAllRandomByLengthNotRecent(length);

        long today = LocalDate.now().toEpochDay();
        System.out.println("today: " + today);

        int dailyIndex = (int) (today % words.size());
        System.out.println("dailyIndex: " + dailyIndex);

        Word word = words.get(dailyIndex);
        return word;
    }

}
