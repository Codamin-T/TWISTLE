package com.example.twistle.service;

import com.example.twistle.model.Profile;
import com.example.twistle.model.Word;
import com.example.twistle.repository.ProfileRepository;
import com.example.twistle.repository.WordRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProfileService {

    @Autowired
    HttpSession session;

    @Autowired
    private ProfileRepository profileRepository;

    public ProfileService(WordRepository wordRepository) {
        this.profileRepository =  profileRepository;
    }

    public void addPointsToProfile(int currentRow){
        int amountOfTries = currentRow+1;
        int points = 1;

        Profile profile = profileRepository.findByUsername((String)session.getAttribute("loggedInUser"));

        if (amountOfTries == 1) {
            points = 5;
        } else if (amountOfTries == 2) {
            points = 3;
        } else if (amountOfTries == 3) {
            points = 2;
        }

        profile.addPoints(points);
        profileRepository.save(profile);

        session.setAttribute("points", profile.getPoints());
    }
}
