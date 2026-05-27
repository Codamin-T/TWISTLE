
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

//total amount of rows in each level
let totalRows = [];

// Next level button
let nextLvlBtn = document.querySelector(".next-level-button");

// HTML attribute for next level link
let nextEnabled = nextLvlBtn.getAttribute("href");

let gameOver = false;

let seconds = 0;
let timerInterval = null;
let useTimer = false;

var winSound = new Audio('WIN_SOUND.mp3');


function startTimer() {
    stopTimer();

    seconds = 0;
    const timerElement = document.getElementById("timer");
    timerInterval = setInterval(() => {
        seconds++;
        let mins = Math.floor(seconds / 60);
        let secs = seconds % 60;
        timerElement.innerText =
            `${String(mins).padStart(2, "0")}:${String(secs).padStart(2, "0")}`;
    }, 1000);
}

function stopTimer() {
    clearInterval(timerInterval);
    timerInterval = null;
}


//Fetch a random word from the server
function loadWord() {
    nextLvlBtn.removeAttribute("href");
    nextLvlBtn.style.opacity = "0.5";
    nextLvlBtn.style.cursor = "not-allowed";

    WORD_LENGTH = Number(
        document.querySelector("#game").dataset.length
    );

    fetch(`/word/${WORD_LENGTH}`)
        .then(res => res.text())
        .then(data => {
            WORD = data.trim();
            startGame();
        });
}


// Start the game
 function startGame() {
    bubbles = document.querySelectorAll(".bubble");
    totalRows = Number(document.getElementById("game").dataset.rows);

    document.addEventListener("keydown", handleKeyPress);

    keyPressEvents();

    initialize();

    document.getElementById("closeButton").addEventListener("click", () => {
        document.getElementById("modal").style.display = "none";
    });

    document.getElementById("loseCloseButton").addEventListener("click", () => {
        document.getElementById("loseModal").style.display = "none";
    });

}


// Handle key input

 function handleKeyPress(e) {
    let key;

    if (isLetter(e) === false && e.toString().length>10) {
        key = e.key;
    } else {
        key = e;
    }

    if (isLetter(key)) {
        addLetter(key);

        if (currentGuess.length === WORD_LENGTH){
            updateKeyboard("Enter", "available");
        }
    }

    else if (key === "Backspace") {
        if (currentGuess.length === WORD_LENGTH){
            updateKeyboard("Enter", "unavailable");
        }
        removeLetter();
    }

    else if (key === "Enter") {
        // Clicks 'Next level' button if it's enabled
        if(nextLvlBtn.hasAttribute("href")) {
            nextLvlBtn.click();
            return;
        }

        if (checkGuess() === true || currentGuess.length < WORD_LENGTH){
            updateKeyboard("Enter", "unavailable");
        }

    }

}


//Check is input is a vaild letter

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
    colorLettersOnGuess();

    // WIN CONDTION
    if (currentGuess === WORD) {
        playWinSound();
        stopTimer();
        fetch(`completeLevel/${WORD_LENGTH}${currentRow.toString()}`);

        winAnimations();
        setPointsOnScreen();
        return true;
    }  else if (currentGuess != WORD && (currentRow +1) == totalRows){
        stopTimer();
        setTimeout(() => {
               document.getElementById("correctWord").innerText = WORD;
               document.getElementById("loseModal").style.display = "block";
           }, 100);
        }
     currentRow++;
         currentGuess = "";
 }

function initialize() {
// Create the key board
    let keyboard = [
        ["Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"],
        ["A", "S", "D", "F", "G", "H", "J", "K", "L", " "],
        ["Enter", "Z", "X", "C", "V", "B", "N", "M", "⌫" ]
    ]

    for (let i = 0; i < keyboard.length; i++) {
        let currRow = keyboard[i];
        let keyboardRow = document.createElement("div");
        keyboardRow.classList.add("keyboard-row");

        for (let j = 0; j < currRow.length; j++) {
            let keyTile = document.createElement("div");

            let key = currRow[j];
            keyTile.innerText = key;
            if (key == "Enter") {
                keyTile.id = "Enter";
            }
            else if (key == "⌫") {
                keyTile.id = "Backspace";
            }
            else if ("A" <= key && key <= "Z") {
                keyTile.id = "Key" + key; // "Key" + "A";
            }

            keyTile.addEventListener("click", processKey);

            if (key == "Enter") {
                keyTile.classList.add("enter-key-tile");
            } else {
                keyTile.classList.add("key-tile");
            }
            keyboardRow.appendChild(keyTile);
        }
        document.body.appendChild(keyboardRow);
    }
    /*document.addEventListener("keyup", (e) => {
            processInput(e);
        })*/
}

function colorLettersOnGuess(){
    for (let i = 0; i < WORD_LENGTH; i++) {
        const index = currentRow * WORD_LENGTH + i;
        const letter = currentGuess[i];
        const correct = WORD[i];

        if (letter === correct) {
            bubbles[index].classList.add("correct");
            updateKeyboard(letter, "correct");
        }
        else if (WORD.includes(letter)) {
            bubbles[index].classList.add("wrong-position");
            updateKeyboard(letter, "wrong-position");
        }
        else {
            bubbles[index].classList.add("wrong");
            updateKeyboard(letter, "wrong");
        }
    }
}

function playWinSound(){
    winSound.volume = 0.1;
    winSound.playbackRate = 1 + WORD_LENGTH/20;
    winSound.preservesPitch = false;
    winSound.play();
}

function winAnimations(){
    nextLvlBtn.style.opacity = "1";
    nextLvlBtn.style.cursor = "pointer"

    setTimeout(() => {
        document.getElementById("modal").style.display = "block";

        confetti({
            particleCount: 150,
            spread: 80,
            origin: { y: 0.6 }
        });

        nextLvlBtn.style.transform = "scale(1.2)";
        setTimeout(() => {
            nextLvlBtn.style.transform = "";
            nextLvlBtn.setAttribute("href", nextEnabled);
        }, 200);
    }, 100);
}

function setPointsOnScreen(){
    let pointsText = document.getElementById("points");
    if(pointsText != null){
        let currentPoints = parseInt(pointsText.innerText);
        if (currentRow === 0) {
            currentPoints += 5;
        } else if (currentRow === 1) {
            currentPoints += 3;
        } else if (currentRow === 2) {
            currentPoints += 2;
        } else {
            currentPoints += 1;
        }
        pointsText.innerText = currentPoints;
    }
}

function processKey() {
    e = { "code" : this.id };
    let clickedKey = String(this.id).replace("Key", "");
    processInput(handleKeyPress);
    handleKeyPress(clickedKey);
}

function processInput(e) {
    if (gameOver) return;

    // alert(e.code);
    if ("KeyA" <= e.code && e.code <= "KeyZ") {
        if (col < width) {
            let currTile = document.getElementById(currentRow.toString() + '-' + col.toString());
            if (currTile.innerText == "") {
                currTile.innerText = e.code[3];
                col += 1;
            }
        }
    }
    else if (e.code == "Backspace") {
        if (0 < col && col <= width) {
            col -=1;
        }
        let currTile = document.getElementById(currentRow.toString() + '-' + col.toString());
        currTile.innerText = "";
    }

    else if (e.code == "Enter") {
        update();
    }

    if (!gameOver && currentRow == totalRows) {
        gameOver = true;
        document.getElementById("answer").innerText = word;
    }
}

function updateKeyboard(letter, status) {
    if (letter === "Enter"){
        const enterTile = document.getElementById(letter);
        if (status === "available") {
            enterTile.classList.add(status);
        } else {
            enterTile.classList.remove("available");
        }
        return;
    }

    const keyTile = document.getElementById("Key" + letter.toUpperCase());
    if (!keyTile) return;

    // Priority: correct > wrong-position > wrong. Never downgrade.
    if (keyTile.classList.contains("correct")) return;
    if (keyTile.classList.contains("wrong-position") && status !== "correct") return;

    keyTile.classList.remove("correct", "wrong-position", "wrong");
    keyTile.classList.add(status);
}

/*
* Animates keys to be smaller when the keys are pressed down using event listeners for key-up and key-down.
* Uses the same listeners to allow 'control+backspace' to delete the whole word.
 */
function keyPressEvents(){
    let backSpaceDown = false;
    addEventListener("keydown", (event) => {
        let keyTile = document.getElementById("Key" + event.key.toUpperCase());
        if (keyTile != null) {
            keyTile.classList.add("is-pressed");
        }

        if (event.key === "Backspace") {
            document.getElementById("Backspace").classList.add("is-pressed");
            backSpaceDown = true;
        }
        if(event.ctrlKey && backSpaceDown){
            while (currentGuess.length > 0){
                handleKeyPress("Backspace");
            }
        }
    });

    addEventListener("keyup", (event) => {
        let keyTile = document.getElementById("Key" + event.key.toUpperCase());
        if (keyTile != null) {
            keyTile.classList.remove("is-pressed");
        }

        if (event.key === "Backspace") {
            document.getElementById("Backspace").classList.remove("is-pressed");
            backSpaceDown = false;
        }
    });

    addEventListener("blur", () => {
        backSpaceDown = false;
    });

}
document.addEventListener("DOMContentLoaded", () => {
    const startBtn = document.getElementById("startTimerBtn");
    startBtn.addEventListener("click", () => {
        useTimer = true;
//TOG BORT LOADWORD
        startTimer();
        startBtn.style.display = "none";
    });
});

loadWord();




