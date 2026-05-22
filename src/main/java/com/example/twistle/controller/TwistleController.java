package com.example.twistle.controller;

import com.example.twistle.model.*;
import com.example.twistle.repository.*;
import com.example.twistle.config.*;
import com.example.twistle.service.ProfileService;
import com.example.twistle.service.WordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
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
    @Autowired
    private WordService wordService;
    @Autowired
    private ProfileService profileService;

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
    public String processAddProfile(Profile profile, RedirectAttributes redirectAttributes) {
        System.out.println("ad process add profile");
        if (profileRepository.findByUsername(profile.getUsername()) != null) {
            System.out.println("tried to register with existing username");
            return "redirect:/login";
        } else if (profile.getPassword().length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password should be 6 characters");
            return "redirect:/register";
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
    public String processLogin(Profile profile, RedirectAttributes redirectAttributes) {
        System.out.println("login process login");
        //Måste kontollera om användaren bara är felstavat först
        Profile existingProfile = profileRepository.findByUsername(profile.getUsername());
        if (existingProfile == null) {
            redirectAttributes.addFlashAttribute("error", "Wrong username or password");
            return "redirect:/login";
        }
        if (passwordEncoder.matches(profile.getPassword(), existingProfile.getPassword())) {
            System.out.println("Successfully logged in");
            session.setAttribute("loggedInUser", existingProfile.getUsername());
            return "redirect:/play";
        } else {
            redirectAttributes.addFlashAttribute("error", "Wrong username or password");
            return "redirect:/login";
        }
        //return "redirect:/login"; //7 maj
    }

    @GetMapping("/logout")
    public String logout() {
        session.removeAttribute("loggedInUser");
        return "redirect:/";
    }

    // Directs user to play menu page. Creates a HashMap to store if each level is unlocked.
    @GetMapping("/play")
    public String showPlay(Model model) {
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));

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

    // Called when a level is completed, unlocks next level.
    @ResponseBody
    @GetMapping("/completeLevel/{level}{tries}")
    public void completeLevel(@PathVariable String level, @PathVariable String tries) {

        int currentRow = Integer.parseInt(level.substring(1));
        level = level.substring(0, 1);

        profileService.addPointsToProfile(currentRow);

        wordService.setLastUsed((Word)session.getAttribute("currentWord"));
        Map<String, Boolean> unlockedLevels;
        unlockedLevels = (HashMap)session.getAttribute("unlockedLevels");

        // If the player goes directly to a level site without going to /play, the HashMap has to be created here.
        if (unlockedLevels == null){
            unlockedLevels = new HashMap<>();
            for (int i= 1; i <=8; i++){
                int lvl = Integer.parseInt(level) - 1;
                if (i == 1 || i == lvl){
                    unlockedLevels.put(String.valueOf(i), true);
                    continue;
                }
                unlockedLevels.put(String.valueOf(i), false);
            }
        }
        unlockedLevels.put(level, true);
        session.setAttribute("unlockedLevels", unlockedLevels);
    }

    // Gets unlocked levels in a HashMap
    @ResponseBody
    @RequestMapping(value = "/unlockedLevels", method = RequestMethod.GET)
    public Map<String, Boolean> showUnlockedLevels() {
        return (HashMap)session.getAttribute("unlockedLevels");
    }

    // The following methods are mappings to each level's HTML file:

    @GetMapping("/sida2")
    public String showSida2(Model model) {

        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        return "level2";
    }

    @GetMapping("/sida3")
    public String showSida3(Model model){
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        return "level3";
    }

    @GetMapping("/sida4")
    public String showSida4(Model model) {
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        return "level4";
    }

    @GetMapping("/sida5")
    public String showSida5(Model model){
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        return "level5";
    }

    @GetMapping("/sida6")
    public String showSida6(Model model){
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        return "level6";
    }

    @GetMapping("/sida7")
    public String showSida7(Model model) {
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        return "level7";
    }

    @GetMapping("/sida8")
    public String showSida8(Model model) {
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        return "level8";
    }
}

