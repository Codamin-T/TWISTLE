package com.example.twistle.controller;

import com.example.twistle.model.Profile;
import com.example.twistle.repository.ProfileRepository;
import com.example.twistle.config.SecurityConfig;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private SecurityConfig securityConfig;
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private HttpSession session;

    public UserController() {
        this.securityConfig = new SecurityConfig();
        this.passwordEncoder = securityConfig.passwordEncoder();
    }

    @GetMapping("/")
    public String showStartPage(){
        return "start";
    }

    @GetMapping("/register")
    public String showRegisterForm(Profile profile){
        return "register";
    }

    @PostMapping("/register")
    public String processAddProfile(Profile profile, RedirectAttributes redirectAttributes) {
        if (profileRepository.findByUsername(profile.getUsername()) != null) {
            System.out.println("tried to register with existing username");
            return "redirect:/login";
        } else if (profile.getPassword().length() < 6){
            redirectAttributes.addFlashAttribute("error", "Password should be 6 characters");
            return "redirect:/register";
        }

        String encodedPassword = passwordEncoder.encode(profile.getPassword());
        profile.setPassword(encodedPassword);
        profileRepository.save(profile);
        session.setAttribute("loggedInUser", profile.getUsername());
        return "redirect:/play";
    }

    @GetMapping("/register-error")
    public String showRegisterError(){
        return "register-error";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model){
        model.addAttribute("profile", new Profile());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(Profile profile, RedirectAttributes redirectAttributes){
        Profile existingProfile = profileRepository.findByUsername(profile.getUsername());
        if (existingProfile == null) {
            redirectAttributes.addFlashAttribute("error", "Wrong username or password");
            return "redirect:/login";
        }
        if (passwordEncoder.matches(profile.getPassword(), existingProfile.getPassword())){
            session.setAttribute("loggedInUser", existingProfile.getUsername());
            return "redirect:/play";
        } else {
            redirectAttributes.addFlashAttribute("error", "Wrong username or password");
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String logout(){
        session.removeAttribute("loggedInUser");
        session.invalidate();
        return "redirect:/";
    }
}
