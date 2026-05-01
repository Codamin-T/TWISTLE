package com.example.twistle.controller;

import com.example.twistle.model.*;
import com.example.twistle.repository.*;
import com.example.twistle.config.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TwistleController {

    private SecurityConfig securityConfig;
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private HttpSession session;

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
    public String showLoginForm(Model model) {
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
    public String showPlay(Model model) {
        Map<String, Boolean> unlockedLevels = (HashMap<String, Boolean>)session.getAttribute("unlockedLevels");

        if (unlockedLevels == null) {
            unlockedLevels = new HashMap<>();
            unlockedLevels.put("1", true);
            unlockedLevels.put("2", false);
            unlockedLevels.put("3", false);
            unlockedLevels.put("4", false);
            unlockedLevels.put("5", false);
            unlockedLevels.put("6", false);
            unlockedLevels.put("7", false);
            unlockedLevels.put("8", false);

            session.setAttribute("unlockedLevels", unlockedLevels);
        }
        model.addAttribute("unlockedLevels", unlockedLevels);

        return "play";
    }

    @PostMapping("/completeLevel")
    @ResponseBody
    public void completeLevel(@RequestParam String level) {
        // Boolean[] unlockedLevelsArray = (Boolean[]) session.getAttribute("unlockedLevelsArray");
        Map<String, Boolean> unlockedLevels = (HashMap<String, Boolean>) session.getAttribute("unlockedLevels");
        unlockedLevels.put(level, true);
        session.setAttribute("unlockedLevels", unlockedLevels);
    }

    @ResponseBody
    @RequestMapping(value = "/unlockedLevels", method = RequestMethod.GET)
    public Map<String, Boolean> showUnlockedLevels() {
        return (HashMap)session.getAttribute("unlockedLevels");
    }

    @GetMapping("/sida2")
    public String showSida2() {
        //FOR UNLOCKING NEXT LEVEL, SETS THIS LEVEL TO COMPLETE
        /*
        HashMap<String, Boolean> unlockedLevels = (HashMap<String, Boolean>)session.getAttribute("unlockedLevels");
        unlockedLevels.put("level2_complete", true); */
        return "sida2";
    }

    @GetMapping("/sida3")
    public String showSida3(){
        return"sida3";
    }

    @GetMapping("/sida4")
    public String showSida4(){
        return "sida4";
    }

    @GetMapping("/sida5")
    public String showSida5(){return "sida5";}

    @GetMapping("/sida6")
    public String showSida6(){
        return"sida6";
    }

    @GetMapping("/sida7")
    public String showSida7() {
        return "sida7";
    }

    @GetMapping("/sida8")
    public String showSida8() {
        return "sida8";
    }
}

