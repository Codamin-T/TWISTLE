package com.example.twistle.controller;

import com.example.twistle.model.*;
import com.example.twistle.repository.*;
import com.example.twistle.service.WordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for getting daily words.
 * Uses service class to get the word and sets the session variable for it.
 */
@Controller
public class WordController {

    private final WordRepository wordRepository;
    private final HttpSession session;

    @Autowired
    WordService wordService;

    public WordController(WordRepository wordRepository, HttpSession session, WordService wordService) {
        this.wordRepository = wordRepository;
        this.session = session;
        this.wordService = wordService;
    }

    /**
     * Gets a word based on the given length and returns it as text
     * @param length Length of the word to get.
     * @return Returns the daily word.
     */
    @GetMapping("/word/{length}")
    @ResponseBody
    public String getWord(@PathVariable int length) {
        Word word = wordService.getDailyWord(length);
        session.setAttribute("currentWord", word);
        if (word == null) {
            return "";
        }
        return word.getWordText();
    }
}


