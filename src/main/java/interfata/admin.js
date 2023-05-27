
var profileInfoElement = document.getElementById('profile-info');
console.log(document.cookie.valueOf('userId').split('=')[1]);
fetch('/user-info/'.concat(document.cookie.valueOf('userId').split('=')[1]))
  .then(response => response.json())
  .then(data => {
    profileInfoElement.innerHTML = ''
    profileInfoElement.innerHTML = profileInfoElement.innerHTML.concat(
      `
      <p>Nume: ${data.nume}</p>
      <p>Prenume: ${data.prenume}</p>
      <p>Data de nastere: ${data.data_nastere}</p>
      <p>Email: ${data.email}</p>
      `
    )
    if (data.adresa != null || data.adresa != undefined) {
      profileInfoElement.innerHTML = profileInfoElement.innerHTML.concat(`<p>Adresa: ${data.adresa}</p>`);
    }
    if (data.nr_telefon != null || data.nr_telefon != undefined) {
      profileInfoElement.innerHTML = profileInfoElement.innerHTML.concat(`<p>Numar de telefon: ${data.nr_telefon}</p>`);
    }
  })
  .catch(error => {
    console.error('Failed to retrieve user information:', error);
  });
