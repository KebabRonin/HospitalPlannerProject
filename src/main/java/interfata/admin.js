
var profileInfoElement = document.getElementById('profile-info');

fetch('/user-info')
  .then(response => response.json())
  .then(data => {
    console.log(data.nume + " " + data.prenume + " " + data.adresa + " " + data.email)
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
