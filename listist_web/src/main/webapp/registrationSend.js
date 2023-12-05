function sendForm() {
    let username = document.forms["registration"]["username"].value;
    let email = document.forms["registration"]["email"].value;
    let password = document.forms["registration"]["password"].value;
    let password2 = document.forms["registration"]["password2"].value;
    let emailRGEX = /.+@.*\..+/;
    let regEx = /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{6,16}$/;
    let ok = true;

    if(!emailRGEX.test(email))
    {
        document.getElementById("emailError").textContent = "Email is invalid.";
        ok = false;
    }
    else {
        document.getElementById("emailError").textContent = "";
    }


    if(password2 !== password) {
        document.getElementById("passwordError").innerText = "Passwords do not match.";
        ok = false;
    }
    else {
        if (!regEx.test(password)) {
            document.getElementById("passwordError").innerText = "Password is not valid.";
            ok = false;
        }
        else {
            document.getElementById("passwordError").innerText = "";
        }
    }


    if (ok) {
        fetch('register', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                username: document.getElementById('username').value,
                email: document.getElementById('email').value,
                password: document.getElementById('psw').value
            }),
        }).then(res => res.json())
            .then(data => {
                document.getElementById("usernameError").innerText = `${data.usernameError}` === "null" ? "" : `${data.usernameError}`;

                document.getElementById("emailError").innerText = `${data.emailError}` === "null" ? "" : `${data.emailError}`;
                console.log(data);
              if(`${data.usernameError}` === "null" && `${data.emailError}` === "null") {
                  window.location = "http://localhost:8080/listist_web/main";
              }
            })
    }
    else return false;
}