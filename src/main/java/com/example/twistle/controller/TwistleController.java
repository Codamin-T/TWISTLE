package com.example.twistle.controller;

import com.example.twistle.model.*;
import com.example.twistle.repository.*;
import com.example.twistle.config.*;
import com.example.twistle.service.GuessService;
import com.example.twistle.service.WordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TwistleController {

    private SecurityConfig securityConfig;
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private GuessService guessService;
    @Autowired
    private WordService wordService;
    @Autowired
    private HttpSession session;
    @Autowired
    private LazyInitializationExcludeFilter eagerJpaMetamodelCacheCleanup;

    public TwistleController() {
      this.securityConfig = new SecurityConfig();
      this.passwordEncoder = securityConfig.passwordEncoder();
    }
    // Index redirection
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("profiles", profileRepository.findAll());
        return "start";
    }

    // Directs user to word list-page, and fills it with all words in the database.
    @GetMapping("/word_list")
    public String wordList(Model model) {
        model.addAttribute("words", wordRepository.findAll());
        return "word_list";
    }

    // Directs user to add word-page
    @GetMapping("/add_word")
    public String showAddForm(Word word) {
        return "add_word";
    }

    // Receives a new word from the user, adds it to the database.
    @PostMapping("/add_word")
    public String processAddWord(Word word) {
        wordRepository.save(word);
        return "redirect:/";
    }

    // Directs user to register page
    @GetMapping("/register")
    public String showRegisterForm(Profile profile) {
        return "register";
    }

    // Receives and handlers register process.
    @PostMapping("/register")
    public String processAddProfile(Profile profile) {
        System.out.println("ad process add profile");
        if (profileRepository.findByUsername(profile.getUsername()) != null) {
            System.out.println("tried to register with existing username");
            return "redirect:/login";
        } else if (profile.getPassword().length() < 6) {
            return "redirect:/register-error";
        }
        String encodedPassword = passwordEncoder.encode(profile.getPassword());
        profile.setPassword(encodedPassword);
        profileRepository.save(profile);
        return "redirect:/";
    }

    // Directs user to register error page, placeholder.
    @GetMapping("/register-error")
    public String showRegisterError() {
        return "register-error";
    }

    // Directs user to login page
    @GetMapping("/login")
    public String showLoginForm(Model model){
        model.addAttribute("profile", new Profile());
        return "login";
    }

    // Receives login request and handles it.
    @PostMapping("/login")
    public String processLogin(Profile profile) {
        System.out.println("login process login");
        Profile existingProfile = profileRepository.findByUsername(profile.getUsername());
        if (existingProfile == null) {
            return "redirect:/register";
        }
        if (passwordEncoder.matches(profile.getPassword(), existingProfile.getPassword())) {
            System.out.println("Successfully logged in");
            return "redirect:/play";
        }
        return "redirect:/";
    }

    @GetMapping("/play")
    public String showPlay() {
        return "play";
    }

    @GetMapping("/play/guess/2")
    public String showGuess2(Model model) {
        Word word = wordService.getDailyWord(2);

        int[] correctedWordArray = new int[2];
        for (int i = 0; i < correctedWordArray.length; i++) {
            correctedWordArray[i] = 2;
        }

        model.addAttribute("word", word);
        model.addAttribute("correctedWordArray", correctedWordArray);

        System.out.println("Word to guess: " + word.getWord_text());
        session.setAttribute("currentWord", word);
        return "guess2";
    }

    @PostMapping("/play/guess/2")
    public String processPlay(String guessText) {
        Word wordToGuess = (Word)session.getAttribute("currentWord");
        String wordToGuessString = wordToGuess.getWord_text();

        int[] correctedWordArray = guessService.guessWord(guessText, wordToGuessString);

        for(int i=0; i<correctedWordArray.length; i++){
            if(correctedWordArray[i] == 0){
                System.out.println("Letter at index: " + i + " is correct");
            } else if (correctedWordArray[i] == 1){
                System.out.println("Letter at index: " + i + " is in the word");
            } else {
                System.out.println("Letter at index: " + i + " is incorrect");
            }
        }
        session.setAttribute("correctedWordArray", correctedWordArray);

        if(wordToGuessString.equals(guessText)){
            return "redirect:/play";
        }
        return "guess2";
    }
}
