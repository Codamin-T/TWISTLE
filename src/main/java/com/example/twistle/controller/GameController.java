package com.example.twistle.controller;

import com.example.twistle.model.Word;
import com.example.twistle.repository.ProfileRepository;
import com.example.twistle.service.ProfileService;
import com.example.twistle.service.WordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for incoming and outgoing game logic using service classes.
 * Handles requests and stores temporary data in session variables.
 */
@Controller
public class GameController{

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private HttpSession session;
    @Autowired
    private WordService wordService;
    @Autowired
    private ProfileService profileService;

    /**
     * When user clicks Play now.
     * Initializes current user if it is logged in
     * Initializes a map for which levels are unlocked.
     * @param model Used to pass arguments to html file.
     * @return Name of html file.
     * @author Benjamin Torsson
     */
    @GetMapping("/play")
    public String showPlay(Model model){
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        if (session.getAttribute("loggedInUser") != null){
            session.setAttribute("points", profileRepository.findByUsername((String) session.getAttribute("loggedInUser")).getPoints());
            session.setAttribute("usedHint", false);
            model.addAttribute("points", session.getAttribute("points"));
        }

        Map<String, Boolean> unlockedLevels = (HashMap<String, Boolean>) session.getAttribute("unlockedLevels");

        if (unlockedLevels == null){
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

    /**
     * Called if player completes a level
     * Stores points in the database.
     * Unlocks next level.
     * @param level Which level is completed
     * @param tries How many tries it took to complete the level, determines amount of points.
     * @author Sara Ibrahim
     */
    @ResponseBody
    @GetMapping("/completeLevel/{level}{tries}")
    public void completeLevel(@PathVariable String level, @PathVariable String tries){
        int currentRow = Integer.parseInt(level.substring(1));
        level = level.substring(0, 1);

        profileService.addPointsToProfile(currentRow);
        wordService.setLastUsed((Word) session.getAttribute("currentWord"));

        Map<String, Boolean> unlockedLevels = (HashMap) session.getAttribute("unlockedLevels");

        if (unlockedLevels == null){
            unlockedLevels = new HashMap<>();
            for (int i = 1; i <= 8; i++){
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

    /**
     * Getter for the map containing which levels are unlocked.
     * @return Unlocked levels map.
     * @author Sara Ibrahim
     */
    @ResponseBody
    @GetMapping("/unlockedLevels")
    public Map<String, Boolean> showUnlockedLevels(){
        return (HashMap) session.getAttribute("unlockedLevels");
    }

    /**
     * Saves the current game state. Level and guesses.
     * @param level Which level to save.
     * @param state Guesses on that level.
     * @author Sara Ibrahim
     */
    @ResponseBody
    @PostMapping("/saveGameState/{level}")
    public void saveGameState(@PathVariable String level, @RequestBody Map<String, Object> state){
        Map<String, Object> levelStates = (Map<String, Object>) session.getAttribute("levelStates");
        if (levelStates == null){
            levelStates = new HashMap<>();
        }
        levelStates.put(level, state);
        session.setAttribute("levelStates", levelStates);
    }

    /**
     * Getter for saved game state.
     * @param level Which level to get saved guesses to.
     * @return Returns a map containing the guesses for that level.
     * @author Sara Ibrahim
     */
    @ResponseBody
    @GetMapping("/getSavedState/{level}")
    public Map<String, Object> getSavedState(@PathVariable String level){
        Map<String, Object> levelStates = (Map<String, Object>) session.getAttribute("levelStates");
        if (levelStates == null) return new HashMap<>();
        Object state = levelStates.get(level);
        if (state == null) return new HashMap<>();
        return (Map<String, Object>) state;
    }

    /**
     * Getter for a users points.
     * @return Returns the amount of points for the logged-in user.
     * @author Sara Ibrahim
     */
    @ResponseBody
    @GetMapping("/getPoints")
    public String getPoints(){
        return String.valueOf(profileRepository.findByUsername((String) session.getAttribute("loggedInUser")).getPoints());
    }

    /**
     * Called when a hint is used.
     * Sets session variable for if a hint has been used to true.
     * Calls a service class to remove 10 points from the user.
     * @author Benjamin Torsson
     */
    @ResponseBody
    @GetMapping("/useHint")
    public void useHint(){
        session.setAttribute("usedHint", true);
        profileService.removePointsFromProfile(10);
    }
}