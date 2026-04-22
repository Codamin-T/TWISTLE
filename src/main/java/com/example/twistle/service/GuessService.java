package com.example.twistle.service;

import org.springframework.stereotype.Service;

@Service
public class GuessService {

    public int[] guessWord(String wordGuessed, String wordToGuess){
        wordGuessed = wordGuessed.toLowerCase();
        wordToGuess = wordToGuess.toLowerCase();

        int[] correctedWordArray = new int[wordGuessed.length()];

        for(int i=0;i<wordToGuess.length();i++){
            if (isCorrect(wordToGuess, wordGuessed.charAt(i), i)){
                correctedWordArray[i] = 0;
            } else if (isInWord(wordToGuess, wordGuessed.charAt(i))){
                correctedWordArray[i] = 1;
            } else {
                correctedWordArray[i] = 2;
            }
        }
        return correctedWordArray;
    }

    private boolean isCorrect(String word, char letter, int index){
        return word.charAt(index) == letter;
    }

    private boolean isInWord(String word, char character){
        return word.contains(String.valueOf(character));
    }
}
