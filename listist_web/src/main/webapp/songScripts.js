function sendSong() {
    fetch('songs', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            title: document.getElementById('title').value,
            artist: document.getElementById('artist').value
        }),
    }).then(res => res.json())
        .then(data => {
            document.getElementById("error").innerText = `${data.error}` === "null" ? "" : `${data.error}`;

            if(`${data.error}` === "null") {
                let song = document.createElement("div");
                song.className = "song_container";
                let newName = document.createElement("div");
                newName.innerHTML = document.getElementById('title').value + " - " + document.getElementById('artist').value;
                newName.className = "song_name";
                song.appendChild(newName);
                document.getElementById("content").appendChild(song);

                document.getElementById("title").value = "";
                document.getElementById("artist").value = "";
            }

        })
}

function deleteSong(button) {
    let title;
    let artist;
    let parentDiv = button.parentNode;
    let parentID = parentDiv.getAttribute("id");
    let infoUncut = document.getElementById(parentID).children;
    console.log(infoUncut);
    for(i = 0; i < infoUncut.length; i++ ){
        if(infoUncut[i].id === 's_title') {
            title = infoUncut[i].textContent;
        }

        if(infoUncut[i].id === 's_artist') {
            artist = infoUncut[i].textContent;
        }
    }

    fetch('songs', {
        method: 'DELETE',
        body: JSON.stringify( {
            title: title,
            artist: artist
        }),
    }).then(res => document.getElementById(parentID).remove())

}