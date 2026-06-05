package com.example.twistle.controller;

import com.example.twistle.model.Profile;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for mapping html files to the different levels.
 * Also adds the current logged-in user to those files, to show points etc.
 */
@Controller
public class LevelController{

    @Autowired
    private HttpSession session;

    /**
     * Adds logged-in user to the html code.
     * @param model For passing logged-in user to a model object.
     * @author Sara Ibrahim
     */
    private void addUserToModel(Model model){
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        if (session.getAttribute("loggedInUser") != null){
            model.addAttribute("points", session.getAttribute("points"));
        }
    }

    /**
     * Maps html file to show level 2.
     * @param model For passing logged-in user to the html code.
     * @return Name of html file.
     * @author Marua Alkhafadji
     */
    @GetMapping("/level2")
    public String showLevel2(Model model){
        addUserToModel(model);
        return "level2";
    }

    /**
     * Maps html file to show level 3.
     * @param model For passing logged-in user to the html code.
     * @return Name of html file.
     * @author Sara Ibrahim
     */
    @GetMapping("/level3")
    public String showLevel3(Model model){
        addUserToModel(model);
        return "level3";
    }

    /**
     * Maps html file to show level 4.
     * @param model For passing logged-in user to the html code.
     * @return Name of html file.
     * @author Sara Ibrahim
     */
    @GetMapping("/level4")
    public String showLevel4(Model model){
        addUserToModel(model);
        return "level4";
    }

    /**
     * Maps html file to show level 5.
     * @param model For passing logged-in user to the html code.
     * @return Name of html file.
     * @author Sara Ibrahim
     */
    @GetMapping("/level5")
    public String showLevel5(Model model){
        addUserToModel(model);
        return "level5";
    }

    /**
     * Maps html file to show level 6.
     * @param model For passing logged-in user to the html code.
     * @return Name of html file.
     * @author Sara Ibrahim
     */
    @GetMapping("/level6")
    public String showLevel6(Model model){
        addUserToModel(model);
        return "level6";
    }

    /**
     * Maps html file to show level 7.
     * @param model For passing logged-in user to the html code.
     * @return Name of html file.
     * @author Savannah Norgren
     */
    @GetMapping("/level7")
    public String showLevel7(Model model){
        addUserToModel(model);
        return "level7";
    }

    /**
     * Maps html file to show level 8.
     * @param model For passing logged-in user to the html code.
     * @return Name of html file.
     * @author Savannah Norgren
     */
    @GetMapping("/level8")
    public String showLevel8(Model model){
        addUserToModel(model);
        return "level8";
    }
}