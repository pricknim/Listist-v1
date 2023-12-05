function sendLogin() {
    fetch('login', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            username: document.getElementById('username').value,
            password: document.getElementById('psw').value
        }),
    }).then(res => res.json())
        .then(data => {
            document.getElementById("usernameError").innerText = `${data.usernameError}` === "null" ? "" : `${data.usernameError}`;

            document.getElementById("pswError").innerText = `${data.pswError}` === "null" ? "" : `${data.pswError}`;

            if(`${data.usernameError}` === "null" && `${data.pswError}` === "null") {
                window.location = "http://localhost:8080/listist_web/myplaylists";
            }
        })

}