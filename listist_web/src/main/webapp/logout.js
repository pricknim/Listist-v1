function logout() {
    window.location = 'main';
    fetch('logout', {
        method: 'GET',
    })
}