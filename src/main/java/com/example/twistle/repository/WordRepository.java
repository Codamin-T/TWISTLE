package com.example.twistle.repository;
import com.example.twistle.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Class to communicate with the word table in the database.
 */
public interface WordRepository extends JpaRepository<Word, Long>{

    /**
     * Gets random word from the database, with desired length. Limits query by column 'last_used', set to 30 days.
     * @author Benjamin Torsson
     */
    @Query(value = "SELECT * FROM word WHERE CHAR_LENGTH(word_text) = :length AND (last_used >= current_date - 30 or last_used is null)", nativeQuery = true)
    List<Word> findAllRandomByLengthNotRecent(@Param("length") int length);

}