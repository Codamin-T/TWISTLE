var height = 5;
var width = 3;

var row = 0; //current guess
var row = 0; //current letter for attempt

var gameOver = false;
var word = "SAW";

window.onload = function(){
    initialize();
}

function initialize(){
    for (let r = 0; r < height; r++){
        for (let c = 0; c < width; c++){
            let tile = document.createElement("span");
            tile.id = r.toString() + "-" + c.toString();
            tile.classList.add("tile");
            tile.innerText = "P";
            document.getElementById("board").appendChild(tile);
            print("AAAAAAAAAA");
        }
    }
}