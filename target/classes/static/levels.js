/**
 * Loads unlocked levels from the server and disables locked level links in the UI.
 * @author Sara
 */


document.addEventListener("DOMContentLoaded", function () {

    fetch("/unlockedLevels")
        .then(function(response) {
            return response.json();
        })
        .then(function(unlockedLevels) {



            var levels = [1, 2, 3, 4, 5, 6, 7, 8];
            levels.forEach(function(level) {
                var previousLevel = level - 1;
                var key = previousLevel;

                if (unlockedLevels[key] === false) {
                    var Link = document.querySelector(".b" + level + " a");
                    if (Link) {
                        Link.removeAttribute("href");
                        Link.style.opacity = "0.4";
                        Link.style.cursor = "not-allowed";
                        Link.innerText = "🔒";
                    }
                }
            });

        });
});