package com.example.twistle.repository;
import com.example.twistle.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Class to communicate with the profile table in the database.
 */
public interface ProfileRepository extends JpaRepository<Profile, Long>{

    /**
     * Fins a profile by its username.
     * @param username Username to search with.
     * @return Returns a profile with that username.
     */
    Profile findByUsername(String username);
}