package com.example.twistle.controller;

import com.example.twistle.model.*;
import com.example.twistle.repository.*;
import com.example.twistle.config.*;
import com.example.twistle.service.ProfileService;
import com.example.twistle.service.WordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

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
        session.setAttribute("loggedInUser", profile.getUsername());
        return "redirect:/play";
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
        System.out.println("login process login: " + profile.getPassword());
        //Måste kontollera om användaren bara är felstavat först
        Profile existingProfile = profileRepository.findByUsername(profile.getUsername());
        if (existingProfile == null) {
            System.out.println("Not existing profile");
            redirectAttributes.addFlashAttribute("error", "Wrong username or password");
            return "redirect:/login";
        }
        if (passwordEncoder.matches(profile.getPassword(), existingProfile.getPassword())) {
            System.out.println("Successfully logged in: " + profile.getPassword() + " & " + existingProfile.getPassword());
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
        if(session.getAttribute("loggedInUser") != null) {
            session.setAttribute("points", profileRepository.findByUsername((String) session.getAttribute("loggedInUser")).getPoints());
            model.addAttribute("points", session.getAttribute("points"));
        }

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
        model.addAttribute("points", session.getAttribute("points"));

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

    @ResponseBody
    @PostMapping("/saveGameState/{level}")
    public void saveGameState(@PathVariable String level, @RequestBody Map<String, Object> state) {
        Map<String, Object> levelStates = (Map<String, Object>) session.getAttribute("levelStates");
        if (levelStates == null) {
            levelStates = new HashMap<>();
        }
        levelStates.put(level, state);
        session.setAttribute("levelStates", levelStates);
    }

    @ResponseBody
    @GetMapping("/getSavedState/{level}")
    public Map<String, Object> getSavedState(@PathVariable String level) {
        Map<String, Object> levelStates = (Map<String, Object>) session.getAttribute("levelStates");
        if (levelStates == null) return new HashMap<>();
        Object state = levelStates.get(level);
        if (state == null) return new HashMap<>();
        return (Map<String, Object>) state;
    }

    @ResponseBody
    @RequestMapping(value = "/getPoints", method = RequestMethod.GET)
    public String getPoints() {
        return String.valueOf(profileRepository.findByUsername((String)session.getAttribute("loggedInUser")).getPoints());
    }

    // The following methods are mappings to each level's HTML file:

    @GetMapping("/level2")
    public String showLevel2(Model model) {
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        if(session.getAttribute("loggedInUser") != null){
            model.addAttribute("points", session.getAttribute("points"));
        }
        return "level2";
    }

    @GetMapping("/level3")
    public String showLevel3(Model model){
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        if(session.getAttribute("loggedInUser") != null){
            model.addAttribute("points", session.getAttribute("points"));
        }
        return "level3";
    }

    @GetMapping("/level4")
    public String showLevel4(Model model) {
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        if(session.getAttribute("loggedInUser") != null){
            model.addAttribute("points", session.getAttribute("points"));
        }
        return "level4";
    }

    @GetMapping("/level5")
    public String showLevel5(Model model){
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        if(session.getAttribute("loggedInUser") != null){
            model.addAttribute("points", session.getAttribute("points"));
        }
        return "level5";
    }

    @GetMapping("/level6")
    public String showLevel6(Model model){
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        if(session.getAttribute("loggedInUser") != null){
            model.addAttribute("points", session.getAttribute("points"));
        }
        return "level6";
    }

    @GetMapping("/level7")
    public String showLevel7(Model model) {
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        if(session.getAttribute("loggedInUser") != null){
            model.addAttribute("points", session.getAttribute("points"));
        }
        return "level7";
    }

    @GetMapping("/level8")
    public String showLevel8(Model model) {
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        if(session.getAttribute("loggedInUser") != null){
            model.addAttribute("points", session.getAttribute("points"));
        }
        return "level8";
    }
}

