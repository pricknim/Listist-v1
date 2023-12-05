function removeSong(button) {
    let title;
    let artist;
    let parentDiv = button.parentNode;
    let parentID = parentDiv.getAttribute("id");
    let infoUncut = document.getElementById(parentID).children;
    console.log(infoUncut);
    for(i = 0; i < infoUncut.length; i++ ){
        if(infoUncut[i].id === 's_title1') {
            title = infoUncut[i].textContent;
        }

        if(infoUncut[i].id === 's_artist1') {
            artist = infoUncut[i].textContent;
        }
    }

    fetch('playlist', {
        method: 'DELETE',
        body: JSON.stringify( {
            title: title,
            artist: artist
        }),
    }).then(res => res.json())
        .then(data => {
                document.getElementById("error").innerText = `${data.error}` === "null" ? "" : `${data.error}`;

                if(`${data.error}` === "null") {
                    document.getElementById(parentID).remove();
                    let song = document.createElement("div");
                    song.className = "song_container";
                    let newTitle = document.createElement("div");
                    newTitle.innerHTML = title;
                    newTitle.className = "song_name";
                    newTitle.id = "s_title2";
                    let newArtist = document.createElement("div");
                    newArtist.innerHTML = artist;
                    newArtist.className = "song_name";
                    newArtist.id = "s_artist2";
                    let hypen = document.createElement("div");
                    hypen.innerHTML = " - ";
                    hypen.className = "song_name";

                    song.append(newTitle);
                    song.append(hypen);
                    song.append(newArtist);
                    document.getElementById("content2").appendChild(song);

                }
        }
        )

}

function addSong(button) {
    let title;
    let artist;
    let parentDiv = button.parentNode;
    let parentID = parentDiv.getAttribute("id");
    let infoUncut = document.getElementById(parentID).children;
    console.log(infoUncut);
    for(i = 0; i < infoUncut.length; i++ ){
        if(infoUncut[i].id === 's_title2') {
            title = infoUncut[i].textContent;
        }

        if(infoUncut[i].id === 's_artist2') {
            artist = infoUncut[i].textContent;
        }
    }

    fetch('playlist', {
        method: 'POST',
        body: JSON.stringify( {
            title: title,
            artist: artist
        }),
    }).then(res => res.json())
        .then(data => {
            document.getElementById("error").innerText = `${data.error}` === "null" ? "" : `${data.error}`;

            if(`${data.error}` === "null") {
                document.getElementById(parentID).remove();
                let song = document.createElement("div");
                song.className = "song_container";
                let newTitle = document.createElement("div");
                newTitle.innerHTML = title;
                newTitle.className = "song_name";
                newTitle.id = "s_title1";
                let newArtist = document.createElement("div");
                newArtist.innerHTML = artist;
                newArtist.className = "song_name";
                newArtist.id = "s_artist1";
                let hypen = document.createElement("div");
                hypen.innerHTML = " - ";
                hypen.className = "song_name";

                song.append(newTitle);
                song.append(hypen);
                song.append(newArtist);
                document.getElementById("content1").appendChild(song);

            }
        })

}