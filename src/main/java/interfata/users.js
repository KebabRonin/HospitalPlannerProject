fetchUsers();

function fetchUsers() {
    const usersList = document.getElementById("users-list");
    usersList.innerHTML = "";
    usersList.style.display = "block";
  
    const usersTitle = document.createElement("h1");
    usersTitle.textContent = `Users`;
    usersList.appendChild(usersTitle);
  
    fetch(`/users-info`)
      .then((response) => response.json())
      .then((data) => {
        const usersTable = document.createElement("table");
        usersTable.id = "users-table";
  
        const tableHeader = document.createElement("thead");
        tableHeader.innerHTML = `
            <tr>
              <th>ID</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Date of birth</th>
              <th>Adress</th>
              <th>Phone Number</th>
              <th>Email</th>
              <th>Password</th>
              <th>Options</th>
            </tr>
          `;
        usersTable.appendChild(tableHeader);
  
        const tableBody = document.createElement("tbody");
        if (data.length > 0) {
          data.forEach((user) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.nume}</td>
                <td>${user.prenume}</td>
                <td>${user.data_nastere}</td>
                <td>${user.adresa}</td>
                <td>${user.nr_telefon}</td>
                <td>${user.email}</td>
                <td>${user.parola}</td>
                <td>
                  <button class="delete-user" data-user-id="${user.id}">Delete</button>
                </td>
              `;
  
            row
              .querySelector(".delete-user")
              .addEventListener("click", (event) => {
                const userId = event.target.getAttribute(
                  "data-user-id"
                );
                deleteUser(userId);
              });
  
            tableBody.appendChild(row);
          });
        } else {
          const emptyRow = document.createElement("tr");
          emptyRow.innerHTML = `<td colspan="10">No users found.</td>`;
          tableBody.appendChild(emptyRow);
        }
  
        usersTable.appendChild(tableBody);
        usersList.appendChild(usersTable);
      })
      .catch((error) => console.log(error));
}

function deleteUser(userId){
    fetch(`/delete-user/${userId}`, {
        method: 'DELETE'
      })
        .then(response => {
          if (response.ok) {
            console.log('User deleted successfully');
            fetchUsers();
          } else {
            console.error('Error deleting user');
          }
        })
        .catch(error => {
          console.error('Error deleting user', error);
        });
}

function deleteAllData(){
  fetch(`/delete-all-data`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        console.log('All data deleted successfully');
        location.reload();
      } else {
        console.error('Error deleting all data');
      }
    })
    .catch(error => {
      console.error('Error deleting all data', error);
    });
}

function deleteAllUsers(){
  fetch(`/delete-all-users`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        console.log('All users deleted successfully');
        location.reload();
      } else {
        console.error('Error deleting users');
      }
    })
    .catch(error => {
      console.error('Error deleting users', error);
    });
}