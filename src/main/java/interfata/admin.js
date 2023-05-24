
var profileInfoElement = document.getElementById('profile-info');
console.log(document.cookie.valueOf('userId').split('=')[1]);
fetch('/user-info/'.concat(document.cookie.valueOf('userId').split('=')[1]))
  .then(response => response.json())
  .then(data => {
    profileInfoElement.innerHTML = `
      <p>Nume: ${data.nume}</p>
      <p>Prenume: ${data.prenume}</p>
      <p>Data de nastere: ${data.data_nastere}</p>
      <p>Adresa: ${data.adresa}</p>
      <p>Numar de telefon: ${data.nr_telefon}</p>
      <p>Email: ${data.email}</p>
    `;
  })
  .catch(error => {
    console.error('Failed to retrieve user information:', error);
  });
