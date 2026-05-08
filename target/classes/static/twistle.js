
/* Description
* This is a wordle style word guessing game
*
*
*  The player must guess a hidden word by typing letters
* on the keyboard. The word is randomly selected fron a server, and the player has
* a limited number of attempts to find the correct answer.
*
* After each guess, the game gives feedback by coloring the letters
* Green : correct letter in the correct position
* Yellow: correct letter in the wrong position
* Gray : letter is not in the word
*
* The game continues until the correct word is guessed
* */


// The correct word to guess
let WORD = "";

//Length of the word
let WORD_LENGTH = 0;

//Current player guess
let currentGuess = "";

// Current row
let currentRow = 0;

// All ganme bubbles
let bubbles = [];



//Fetch a random word from the server

function loadWord() {

    WORD_LENGTH = Number(
        document.querySelector("#game").dataset.length
    );

    fetch(`/word/${WORD_LENGTH}`)
        .then(res => res.text())
        .then(data => {
            WORD = data.trim();

            // just for testing 🚦🚦
            alert(" The word is   " + WORD);
            //

            startGame();
        });
}


// Start the game
 function startGame() {

    bubbles = document.querySelectorAll(".bubble");

    document.addEventListener("keydown", handleKeyPress);
}


// Handel key input

 function handleKeyPress(e) {

    const key = e.key;

    if (isLetter(key)) {
        addLetter(key);
    }

    else if (key === "Backspace") {
        removeLetter();
    }

    else if (key === "Enter") {
        checkGuess();
    }

}


//Check is input is availd letter

 function isLetter(key) {
    return /^[a-zA-Z]$/.test(key)
        && currentGuess.length < WORD_LENGTH;
}

 //Add letter to guess
 function addLetter(key) {

    currentGuess += key.toLowerCase();

    const index =
        currentRow * WORD_LENGTH +
        currentGuess.length - 1;

    bubbles[index].textContent = key.toUpperCase();
}


// Remove last letter
 function removeLetter() {

    if (currentGuess.length === 0) return;

    const index =
        currentRow * WORD_LENGTH +
        currentGuess.length - 1;

    currentGuess = currentGuess.slice(0, -1);
    bubbles[index].textContent = "";
}

 //Check the guess
 function checkGuess() {

    if (currentGuess.length !== WORD_LENGTH) return;

    for (let i = 0; i < WORD_LENGTH; i++) {

        const index =
            currentRow * WORD_LENGTH + i;

        const letter = currentGuess[i];
        const correct = WORD[i];

        if (letter === correct) {
            bubbles[index].classList.add("correct");
        }
        else if (WORD.includes(letter)) {
            bubbles[index].classList.add("wrong-position");
        }
        else {
            bubbles[index].classList.add("wrong");
        }
    }
    // WIN CONDTION
    if (currentGuess === WORD) {
        const len = String(WORD_LENGTH);
        fetch(`completeLevel/${WORD_LENGTH}`);

        setTimeout(() => {
            alert("Right word!");
        }, 100);
    }
    currentRow++;
    currentGuess = "";
}
// Start game
loadWord();

