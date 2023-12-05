function redirect(name) {
    window.location = 'playlist?name='+name;
}

function delPlaylist(button) {
    let name;
    let parentDiv = button.parentNode;
    let parentID = parentDiv.getAttribute("id");
    let infoUncut = document.getElementById(parentID).children;
    console.log(infoUncut);
    for(i = 0; i < infoUncut.length; i++ ){
        if(infoUncut[i].id === 'playlist_name') {
            name = infoUncut[i].textContent;
        }

    }

    fetch('myplaylists', {
        method: 'DELETE',
        body: name
    }).then(res => document.getElementById(parentID).remove())

}