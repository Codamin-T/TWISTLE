
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
let word = "";

//Length of the word
let wordLength = 0;

//Current player guess
let currentGuess = "";

// Current row
let currentRow = 0;

// All game bubbles
let bubbles = [];

//total amount of rows in each level
let totalRows = [];

// Next level button
let nextLvlBtn = document.querySelector(".next-level-button");

// HTML attribute for next level link
let nextlvlBtnEnabled = nextLvlBtn.getAttribute("href");

let gameOver = false;

let seconds = 0;
let timerInterval = null;
let useTimer = false;

var winSound = new Audio('WIN_SOUND.mp3');

let hintedLetter = "";

let currentGuessArray = [];


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
    nextLvlBtn.style.pointerEvents = "none";

    wordLength = Number(document.querySelector("#game").dataset.length);

    fetch(`/getSavedState/${wordLength}`)
        .then(res => res.json())
        .then(savedState => {
            if (savedState && savedState.word) {
                word = savedState.word;
                startGame();
                restoreGameState(savedState);
            } else {
                fetch(`/word/${wordLength}`)
                    .then(res => res.text())
                    .then(data => {
                        word = data.trim();
                        startGame();
                    });
            }
        });
}


// Start the game
 function startGame() {
    bubbles = document.querySelectorAll(".bubble");
    totalRows = Number(document.getElementById("game").dataset.rows);

    document.addEventListener("keydown", handleKeyPress);

    //document.getElementById("instructions-modal").style.display = "block";

    keyPressEvents();

    createKeyboard();

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

        if (getTotalGuessLength() === wordLength){
            updateKeyboard("Enter", "available");
        }
    }

    else if (key === "Backspace") {
        if (getTotalGuessLength() === wordLength){
            updateKeyboard("Enter", "unavailable");
        }
        removeLetter();
    }

    else if (key === "Enter") {
        if (nextLvlBtn.hasAttribute("href")) {
            nextLvlBtn.click();
            return;
        }

        if (checkGuess() === true || combinedLength < wordLength){
            updateKeyboard("Enter", "unavailable");
        }

        document.getElementById("Enter").classList.remove("available");
    }

}

function getTotalGuessLength(){
    let combinedLength = 0;
    let userCount = 0;
    for (let i = 0; i < wordLength; i++) {
        if (currentGuessArray[i] != null || currentGuess[userCount]) {
            combinedLength++;
            if (currentGuessArray[i] == null) userCount++;
        }
    }
    return combinedLength;
}


//Check is input is a vaild letter

 function isLetter(key) {
     let hintCount = currentGuessArray.filter(x => x != null).length;

     return /^[a-zA-Z]$/.test(key)
         && currentGuess.length < wordLength - hintCount;
}

 //Add letter to guess
 function addLetter(key) {

    let startRowIndex = currentRow * wordLength;

    for (let i = 0; i < wordLength; i++) {
        let targetIndex = startRowIndex + i;

        if (bubbles[targetIndex].textContent === "") {
            bubbles[targetIndex].textContent = key.toUpperCase();
            currentGuess += key.toLowerCase();
            break;
        }
    }
}


// Remove last letter
 function removeLetter() {
     if (currentGuess.length === 0) {
         return;
     }

     let startRowIndex = currentRow * wordLength;

     for (let i = wordLength - 1; i >= 0; i--) {
         let targetIndex = startRowIndex + i;

         if (bubbles[targetIndex].textContent !== "" && currentGuessArray[i] == null) {
             bubbles[targetIndex].textContent = "";
             currentGuess = currentGuess.slice(0, -1);
             break;
         }
     }
}


 //Check the guess
 function checkGuess() {
    let combinedGuess = "";
    let userLetterCount = 0;

    for (let i = 0; i < wordLength; i++) {
        if (currentGuessArray[i] != null) {
            combinedGuess += currentGuessArray[i].toLowerCase();
        } else {
            if (currentGuess[userLetterCount]) {
                combinedGuess += currentGuess[userLetterCount];
                userLetterCount++;
            }
        }
    }

    if (combinedGuess.length !== wordLength) return;
    colorLettersOnGuess(combinedGuess);

    // WIN CONDTION
    if (combinedGuess === word) {
        gameOver = true;
        saveGameState();
        playWinSound();
        stopTimer();
        fetch(`completeLevel/${wordLength}${currentRow.toString()}`);

        winAnimations();
        setPointsOnScreen();
        return true;

    }  else if (combinedGuess != word && (currentRow +1) == totalRows){
        gameOver = true;
        saveGameState();
        stopTimer();
        setTimeout(() => {
               document.getElementById("correctWord").innerText = word;
               document.getElementById("loseModal").style.display = "block";
           }, 100);
        } else{
        saveGameState();
        }

     currentRow++;
     currentGuess = "";

     let activeHint = [...currentGuessArray];

     if (currentRow < totalRows) {
         for (let i = 0; i < wordLength; i++) {

             if (activeHint[i] != null){
                 let bubbleNextRow = (currentRow * wordLength) + i;
                 bubbles[bubbleNextRow].textContent = activeHint[i].toUpperCase();
                 bubbles[bubbleNextRow].classList.add("hinted");
             }
         }
     }

     currentGuessArray = activeHint;
 }

function createKeyboard() {
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
}

function colorLettersOnGuess(guess){
    const remainingLetters = {};

    // Count letters in the word that are NOT in the correct position
    for (let i = 0; i < wordLength; i++) {
        if (guess[i] !== word[i]) {
            const c = word[i];
            remainingLetters[c] = (remainingLetters[c] || 0) + 1;
        }
    }

    // First pass: mark greens
    for (let i = 0; i < wordLength; i++) {
        const index = currentRow * wordLength + i;
        const letter = guess[i];
        if (letter === word[i]) {
            bubbles[index].classList.add("correct");
            updateKeyboard(letter, "correct");
        }
    }

    // Second pass: mark yellows and greys
    for (let i = 0; i < wordLength; i++) {
        const index = currentRow * wordLength + i;
        const letter = guess[i];
        if (letter === word[i]) continue;

        if (remainingLetters[letter] > 0) {
            bubbles[index].classList.add("wrong-position");
            updateKeyboard(letter, "wrong-position");
            remainingLetters[letter]--;
        } else {
            bubbles[index].classList.add("wrong");
            updateKeyboard(letter, "wrong");
        }
    }
}

function playWinSound(){
    winSound.volume = 0.1;
    winSound.playbackRate = 1 + wordLength/20;
    winSound.preservesPitch = false;
    winSound.play();
}

function winAnimations(){
    nextLvlBtn.style.opacity = "1";
    nextLvlBtn.style.cursor = "pointer"

    setTimeout(() => {
        document.getElementById("modal").style.display = "block";

        let earned;
        if (currentRow === 0) earned = 5;
        else if (currentRow === 1) earned = 3;
        else if (currentRow === 2) earned = 2;
        else earned = 1;
        let earnedEl = document.getElementById("earnedPoints");
        if (earnedEl) {
            earnedEl.innerText = earned;
        }

        confetti({
            particleCount: 150,
            spread: 80,
            origin: { y: 0.6 }
        });

        nextLvlBtn.style.transform = "scale(1.2)";
        setTimeout(() => {
            nextLvlBtn.style.transform = "";
            nextLvlBtn.setAttribute("href", nextlvlBtnEnabled);
            nextLvlBtn.style.pointerEvents = "";
        }, 200);
    }, 100);
}

function saveGameState() {
    const guesses = [];
    for (let row = 0; row <= currentRow; row++) {
        const letters = [];
        const colors = [];
        for (let col = 0; col < wordLength; col++) {
            const index = row * wordLength + col;
            letters.push(bubbles[index].textContent);
            if (bubbles[index].classList.contains("correct")) {
                colors.push("correct");
            } else if (bubbles[index].classList.contains("wrong-position")) {
                colors.push("wrong-position");
            } else if (bubbles[index].classList.contains("wrong")) {
                colors.push("wrong");
            } else {
                colors.push("");
            }
        }
        guesses.push({ letters, colors });
    }

    fetch(`/saveGameState/${wordLength}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ word: word, currentRow, gameOver, guesses })
    });
}

function restoreGameState(savedState) {
    currentRow = savedState.currentRow+1;
    gameOver = savedState.gameOver || false;

    const guesses = savedState.guesses || [];
    for (let row = 0; row < guesses.length; row++) {
        const guess = guesses[row];
        for (let col = 0; col < wordLength; col++) {
            const index = row * wordLength + col;
            bubbles[index].textContent = guess.letters[col];
            if (guess.colors[col]) {
                bubbles[index].classList.add(guess.colors[col]);
                updateKeyboard(guess.letters[col].toLowerCase(), guess.colors[col]);
            }
        }
    }

    if (gameOver) {
        document.removeEventListener("keydown", handleKeyPress);

        const lastGuess = guesses[guesses.length - 1];
        const wasWin = lastGuess && lastGuess.colors.every(c => c === "correct");
        if (wasWin) {
            nextLvlBtn.style.opacity = "1";
            nextLvlBtn.style.cursor = "pointer";
            nextLvlBtn.setAttribute("href", nextlvlBtnEnabled);
            nextLvlBtn.style.pointerEvents = "";
        }
    }
}

function setPointsOnScreen(){
    let pointsText = document.getElementById("points");
    let currentPoints = parseInt(pointsText.innerText);

    if(pointsText != null) {
        if (hintedLetter.length > 0) {
            currentPoints += 1;
        } else {
            if (currentRow === 0) {
                currentPoints += 5;
            } else if (currentRow === 1) {
                currentPoints += 3;
            } else if (currentRow === 2) {
                currentPoints += 2;
            } else {
                currentPoints += 1;
            }
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
    //Timer
    const startBtn = document.getElementById("startTimerBtn");
        if (startBtn) {
            startBtn.addEventListener("click", () => {
                useTimer = true;
                startTimer();
                startBtn.style.display = "none";

            });
        }

    // Instructions popup
         const instructionsModal = document.getElementById("instructionsModal");
         if (instructionsModal) {
             instructionsModal.style.display = "flex";

             const dismiss = () => {
                 instructionsModal.style.display = "none";
             };
             document.getElementById("instructionsCloseButton").addEventListener("click", dismiss);
             document.getElementById("instructionsGotItBtn").addEventListener("click", dismiss);
         }
});

document.addEventListener("DOMContentLoaded", () =>{
    const hintBtn = document.getElementById("hint");
    if (hintBtn) {
        hintBtn.addEventListener("click", () => {
            removePointsForHint();
        });
    }
});

function removePointsForHint(){
    if (hintedLetter.length > 0) return;

    addHintLetter()

    let pointsText = document.getElementById("points");
    if(pointsText != null){
        let currentPoints = parseInt(pointsText.innerText);
        if (currentPoints < 10){
            currentPoints = 0;
        } else {
            currentPoints -= 10;
        }
        pointsText.innerText = currentPoints;
    }
}

function addHintLetter() {
    let letterArray = word.split('');
    let availableIndexed = [];

    for (let i = 0; i < wordLength; i++) {
        let currentLetter = letterArray[i].toUpperCase()
        let keyTile = document.getElementById("Key" + currentLetter);

        if (!keyTile.classList.contains("correct")) {
            availableIndexed.push(i);
        }
    }

    let randomIndex = Math.floor(Math.random() * availableIndexed.length);
    let index = availableIndexed[randomIndex];
    let letter = letterArray[index];

    hintedLetterIndex = index;
    hintedLetter = letter;

    let targetBubbleIndex = (currentRow * wordLength) + index;
    bubbles[targetBubbleIndex].textContent = letter.toUpperCase();
    bubbles[targetBubbleIndex].classList.add("hinted");
    updateKeyboard(letter, "hinted");

    currentGuessArray[index] = hintedLetter;

    fetch(`useHint`);
}

loadWord();