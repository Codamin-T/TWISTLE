package com.example.twistle.service;

import com.example.twistle.model.Profile;
import com.example.twistle.repository.ProfileRepository;
import com.example.twistle.repository.WordRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService{

    @Autowired
    HttpSession session;

    @Autowired
    private ProfileRepository profileRepository;

    public ProfileService(WordRepository wordRepository){
        this.profileRepository =  profileRepository;
    }

    public void addPointsToProfile(int currentRow){
        int amountOfTries = currentRow+1;
        int points = 1;

        Profile profile = profileRepository.findByUsername((String)session.getAttribute("loggedInUser"));

        if (profile == null) return;

        if ((boolean)session.getAttribute("usedHint")){
            points = 1;
        } else {
            if (amountOfTries == 1) {
                points = 5;
            } else if (amountOfTries == 2) {
                points = 3;
            } else if (amountOfTries == 3) {
                points = 2;
            }
        }

        profile.addPoints(points);
        profileRepository.save(profile);

        session.setAttribute("points", profile.getPoints());
        session.setAttribute("usedHint", false);
    }

    public void removePointsFromProfile(int points){
        Profile profile = profileRepository.findByUsername((String)session.getAttribute("loggedInUser"));
        if (profile == null || profile.getPoints() < points) {
            return;
        }

        profile.removePoints(points);
        profileRepository.save(profile);
        session.setAttribute("points", profile.getPoints());
    }
}
