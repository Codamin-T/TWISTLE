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
        this.wordService = wordService;
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
       
       
     
         Word word = (Word)session.getAttribute("currentWord");
        
        /// DET ÄR BARA TEST 🚦🚦🚦🚦 DEN RADEN SKICKAR WORD FRÅN JAVA TILL HTML-SIADAN SÅ ATT JAVA SCRIPT KAN ANVÄNDA DEN SENARE
        model.addAttribute("word", /*word*/  word.getWord_text());
        
        word = wordService.getDailyWord(length);
        session.setAttribute("currentWord", word);
        model.addAttribute("length", length);
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
        if (word == null) {
            return "redirect:/word-menu";
        }
        String wordText = word.getWord_text().toLowerCase();
        if (guessText.toLowerCase().equals(wordText)) {
            System.out.println("Word guessed correctly");
            return "redirect:/word-guess/"+wordText.length()+"/success";
        }
      return "redirect:/word-guess/"+wordText.length();
    }
    
    
    
    
    //test 🤔🤔🤔🤔
   
/*
* Fetch a word based on the given length and returns it as text .
 */
    @GetMapping("/word/{length}")
    @ResponseBody
    public String getWord(@PathVariable int length) {
        Word word = wordService.getDailyWord(length);
        session.setAttribute("currentWord", word);
        if (word == null) {
            return "";
        }
        return word.getWord_text();
    }
}


