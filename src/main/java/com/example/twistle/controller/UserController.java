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

/**
 * Controller for incoming and outgoing user logic using the repository class.
 * Handles requests and stores temporary data in session variables.
 */
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

    /**
     * Maps html file to show start page.
     * @return Name of html file.
     * @author Heba Sadlah
     */
    @GetMapping("/")
    public String showStartPage(){
        return "start";
    }

    /**
     * Maps html file to show register page.
     * @param profile Profile to add.
     * @return Name of html file.
     * @author Heba Sadlah
     */
    @GetMapping("/register")
    public String showRegisterForm(Profile profile){
        return "register";
    }

    /**
     * Called when a user registers a profile.
     * Adds the profile to the database if the values are valid. Encodes the password before saving. Sets session variable for current user.
     * Redirects to login-page if the username already exists.
     * @param profile Profile to add to database
     * @param redirectAttributes Attribute for error handling if the password is less than 6 characters.
     * @return Redirection depending on valid/invalid values.
     * @author Heba Sadlah
     */
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

    /**
     * Maps html file to show login page.
     * @param model Profile to use for values.
     * @return Name of html file.
     * @author Heba Sadlah
     */
    @GetMapping("/login")
    public String showLoginForm(Model model){
        model.addAttribute("profile", new Profile());
        return "login";
    }

    /**
     * Called when a user logs in.
     * Checks if input values are of an existing profile. If not, an errormessage is shown and the user can try again.
     * If the values are values, the session variable for current user is set and the play-page is shown.
     * @param profile Profile containing input values.
     * @param redirectAttributes Attribute to show error messages.
     * @return Redirection depending on valid/invalid values.
     * @author Marua Alkhafadji
     */
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

    /**
     * Called when a user logs out.
     * Removes session attribute for current user.
     * @return Redirection to start page.
     * @author Sara Ibrahim
     */
    @GetMapping("/logout")
    public String logout(){
        session.removeAttribute("loggedInUser");
        return "redirect:/";
    }
}
