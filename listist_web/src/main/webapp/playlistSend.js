function createPlaylist() {
    let name = document.getElementById("title").value;
    const selected = document.querySelectorAll('input[type="checkbox"]:checked');
    const songs = [];

    for(i = 0; i < selected.length; i++) {

        let parent = selected[i].parentNode;
        let parentparent = parent.parentNode;
        let parent2id = parentparent.getAttribute("id");
        let infoUncut = document.getElementById(parent2id).children;
        //console.log(infoUncut);
        for (j = 0; j < infoUncut.length; j++) {
            if (infoUncut[j].id === 's_title') {
                title = infoUncut[j].textContent;
            }

            if (infoUncut[j].id === 's_artist') {
                artist = infoUncut[j].textContent;
            }
        }
        songs.push({title, artist});
    }

    fetch('createplaylist', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            name: name,
            songs: songs
        }),
    }).then(res => res.json())
        .then(data => {
            document.getElementById("error").innerText = `${data.error}` === "null" ? "" : `${data.error}`;

            if(`${data.error}` === "null") {
                window.location = "http://localhost:8080/listist_web/myplaylists";
            }
        })
}