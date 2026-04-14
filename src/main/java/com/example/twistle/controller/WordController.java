package com.example.twistle.controller;

import com.example.twistle.model.*;
import com.example.twistle.repository.*;
import com.example.twistle.service.WordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
public class WordController {

    private final WordRepository wordRepository;
    private final HttpSession session;

    @Autowired
    WordService wordService;

    public WordController(WordRepository wordRepository, HttpSession session, WordService wordService) {
        this.wordRepository = wordRepository;
        this.session = session;
        //this.wordService = wordService;
    }

    // Directs user to Twistle menu
    @GetMapping("/word-menu")
    public String wordMenu(Model model){
        return "word-guess-menu";
    }

    // Gets guessing screen and daily word for a specific word length.
    @GetMapping("/word-guess/{length}")
    public String wordGuess(@PathVariable int length, Model model){
        System.out.println("Pressed length:"+length);
        model.addAttribute("length", length);

        Word word = wordService.setDailyWords(length);
        model.addAttribute("word", word);
        System.out.println("Word to guess: " + word.getWord_text());

        session.setAttribute("currentWord", word);
        return "word-guess-game";
    }

    // Directs to success screen
    @GetMapping("/word-guess/{length}/success")
    public String wordGuessSuccess(@PathVariable int length, Model model){
        return "word-guess-success";
    }

    // Receives users guess and processes it
    @PostMapping("/word-guess")
    public String WordGuess(String guessText){
        Word word = (Word)session.getAttribute("currentWord");
        String wordText = word.getWord_text().toLowerCase();

        if (guessText.toLowerCase().equals(wordText)) {
            System.out.println("Word guessed correctly");

            return "redirect:/word-guess/"+wordText.length()+"/success";
        }
        return "redirect:/word-guess/"+wordText.length();
    }
}
