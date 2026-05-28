package com.example.twistle.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LevelController{

    @Autowired
    private HttpSession session;

    private void addUserToModel(Model model){
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        if (session.getAttribute("loggedInUser") != null){
            model.addAttribute("points", session.getAttribute("points"));
        }
    }

    @GetMapping("/level2")
    public String showLevel2(Model model){
        addUserToModel(model);
        return "level2";
    }

    @GetMapping("/level3")
    public String showLevel3(Model model){
        addUserToModel(model);
        return "level3";
    }

    @GetMapping("/level4")
    public String showLevel4(Model model){
        addUserToModel(model);
        return "level4";
    }

    @GetMapping("/level5")
    public String showLevel5(Model model){
        addUserToModel(model);
        return "level5";
    }

    @GetMapping("/level6")
    public String showLevel6(Model model){
        addUserToModel(model);
        return "level6";
    }

    @GetMapping("/level7")
    public String showLevel7(Model model){
        addUserToModel(model);
        return "level7";
    }

    @GetMapping("/level8")
    public String showLevel8(Model model){
        addUserToModel(model);
        return "level8";
    }
}