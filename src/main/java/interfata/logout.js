function logout() {
    document.cookie = 'userId=; expires=Thu, 01-Jan-70 00:00:01 GMT;'; 
    window.location.href="/";
}