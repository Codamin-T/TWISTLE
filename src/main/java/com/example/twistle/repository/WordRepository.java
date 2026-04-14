package com.example.twistle.repository;
import com.example.twistle.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {

    @Query(value = "SELECT * FROM word WHERE CHAR_LENGTH(word_text) = :length ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Word findRandomByLength(@Param("length") int length);

    @Query(value = "SELECT * FROM word WHERE CHAR_LENGTH(word_text) = :length AND last_used >= current_date - 100 or last_used is null ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Word findRandomByLengthNotRecent(@Param("length") int length);

    @Query(value = "SELECT * FROM word WHERE CHAR_LENGTH(word_text) = :length AND (last_used >= current_date - 100 or last_used is null)", nativeQuery = true)
    List<Word> findAllRandomByLengthNotRecent(@Param("length") int length);

    @Query(value = "SELECT * FROM word WHERE last_used >= current_date - 100 or last_used is null", nativeQuery = true)
    List<Word> findAllRandomNotRecent();
}