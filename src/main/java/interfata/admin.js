function formatDate(inputDate) {
  const dateParts = inputDate.split('-');
  const year = dateParts[0];
  const month = dateParts[1];
  const day = dateParts[2];
  return `${day}-${month}-${year}`;
}

var profileInfoElement = document.getElementById('profile-details');
console.log(document.cookie.valueOf('userId').split('=')[1]);
fetch('/user-info/'.concat(document.cookie.valueOf('userId').split('=')[1]))
  .then(response => response.json())
  .then(data => {
    profileInfoElement.innerHTML = ''
    profileInfoElement.innerHTML = profileInfoElement.innerHTML.concat(
      `
      <p><strong>First Name:</strong>  ${data.prenume}</p>
      <p><strong>Last Name:</strong>  ${data.nume}</p>
      <p><strong>Date of Birth:</strong>  ${formatDate(data.data_nastere)}</p>
      <p><strong>Email:</strong>  ${data.email}</p>
      `
    )
    if (data.adresa != null || data.adresa != undefined) {
      profileInfoElement.innerHTML = profileInfoElement.innerHTML.concat(`<p><strong>Address:</strong>  ${data.adresa}</p>`);
    }
    if (data.nr_telefon != null || data.nr_telefon != undefined) {
      profileInfoElement.innerHTML = profileInfoElement.innerHTML.concat(`<p><strong>Phone Number:</strong>  ${data.nr_telefon}</p>`);
    }
  })
  .catch(error => {
    console.error('Failed to retrieve user information:', error);
  });
