package com.example.twistle.controller;

import com.example.twistle.model.Profile;
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
import java.util.Random;

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

    @ResponseBody
    @GetMapping("/unlockedLevels")
    public Map<String, Boolean> showUnlockedLevels(){
        return (HashMap) session.getAttribute("unlockedLevels");
    }

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

    @ResponseBody
    @GetMapping("/getSavedState/{level}")
    public Map<String, Object> getSavedState(@PathVariable String level){
        Map<String, Object> levelStates = (Map<String, Object>) session.getAttribute("levelStates");
        if (levelStates == null) return new HashMap<>();
        Object state = levelStates.get(level);
        if (state == null) return new HashMap<>();
        return (Map<String, Object>) state;
    }

    @ResponseBody
    @GetMapping("/getPoints")
    public String getPoints(){
        return String.valueOf(profileRepository.findByUsername((String) session.getAttribute("loggedInUser")).getPoints());
    }

    @ResponseBody
    @GetMapping("/useHint")
    public void useHint(){
        session.setAttribute("usedHint", true);
        profileService.removePointsFromProfile(10);
    }
}