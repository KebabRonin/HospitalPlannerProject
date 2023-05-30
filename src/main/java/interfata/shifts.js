function formatDate(inputDate) {
  const dateParts = inputDate.split('-');
  const year = dateParts[0];
  const month = dateParts[1];
  const day = dateParts[2];
  return `${day}-${month}-${year}`;
}

function formatBackDate(inputDate) {
  const dateParts = inputDate.split('-');
  const year = dateParts[2];
  const month = dateParts[1];
  const day = dateParts[0];
  return `${day}-${month}-${year}`;
}

function getDoctorInfo(id_doctor) {
    return fetch(`/doctors-info/${id_doctor}`)
      .then((response) => {
        if (!response.ok) {
          throw new Error('Network response was not OK');
        }
        return response.json();
      })
      .then((doctor) => {
        if (!doctor || Object.keys(doctor).length === 0) {
          throw new Error('Doctor not found');
        }
        const { nume, prenume, id_cabinet } = doctor;
  
        return fetch(`/cabinet-info/${id_cabinet}`)
          .then((response) => {
            if (!response.ok) {
              throw new Error('Network response was not OK');
            }
            return response.json();
          })
          .then((cabinet) => {
            if (!cabinet || Object.keys(cabinet).length === 0) {
              throw new Error('Cabinet not found');
            }
            const { denumire } = cabinet;
  
            return { nume, prenume, denumire };
          })
          .catch(() => {
            return { nume, prenume, denumire: undefined };
          });
      })
      .catch(() => {
        return { nume: undefined, prenume: undefined, denumire: undefined };
      });
}
  
function getCabinetInfo(id_cabinet) {
  return fetch(`/cabinet-info/${id_cabinet}`)
      .then((response) => response.json())
      .then((cabinet) => {
      const { denumire } = cabinet;
      return { denumire };
      })
      .catch((error) => console.log(error));
}
  
function fetchShifts() {
  const shiftsList = document.getElementById("shifts-list");
  shiftsList.innerHTML = "";
  shiftsList.style.display = "block";

  const shiftsTitle = document.createElement("h1");
  shiftsTitle.textContent = `Shifts`;
  shiftsList.appendChild(shiftsTitle);

  fetch(`/all-shifts`)
    .then((response) => response.json())
    .then((data) => {
      const shiftsByDate = {};
      data.forEach((shift) => {
        const date = formatDate(shift.zi);
        if (!shiftsByDate[date]) {
          shiftsByDate[date] = [];
        }
        shiftsByDate[date].push(shift);
      });

      Object.keys(shiftsByDate).forEach((date) => {
        const shiftsTable = document.createElement("table");
        shiftsTable.id = `shifts-table-${date}`;

        const tableCaption = document.createElement("caption");
        tableCaption.textContent = date;
        shiftsTable.appendChild(tableCaption);

        const tableHeader = document.createElement("thead");
        tableHeader.innerHTML = `
            <tr>
              <th>ID</th>
              <th>Doctor</th>
              <th>Cabinet</th>
              <th>Date</th>
              <th>Start Hour</th>
              <th>End Hour</th>
              <th>Options</th>
            </tr>
          `;
        shiftsTable.appendChild(tableHeader);

        const tableBody = document.createElement("tbody");
        const shifts = shiftsByDate[date];
        if (shifts.length > 0) {
          shifts.forEach((shift) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${shift.id}</td>
                <td id="doctor-name-${shift.id_doctor}"></td>
                <td id="cabinet-name-${shift.id_doctor}"></td>
                <td>${shift.zi}</td>
                <td>${shift.startHour}</td>
                <td>${shift.endHour}</td>
                <td>
                  <button class="edit-option" data-shift-id="${shift.id}">Edit</button>
                </td>
              `;

            getDoctorInfo(shift.id_doctor)
              .then((doctor) => {
                const doctorNameCell = row.querySelector(`#doctor-name-${shift.id_doctor}`);
                const cabinetNameCell = row.querySelector(`#cabinet-name-${shift.id_doctor}`);
                if (typeof doctor.nume === "undefined") {
                  doctorNameCell.textContent = "Unassigned";
                  cabinetNameCell.textContent = "Unassigned";
                } else {
                  doctorNameCell.textContent = `${doctor.nume} ${doctor.prenume}`;
                  cabinetNameCell.textContent = doctor.denumire;
                }
              })
              .catch((error) => console.log(error));

            row
              .querySelector(".edit-option")
              .addEventListener("click", (event) => {
                const shiftId = event.target.getAttribute(
                  "data-shift-id"
                );
                editShift(shiftId);
              });

            tableBody.appendChild(row);
          });
        } else {
          const emptyRow = document.createElement("tr");
          emptyRow.innerHTML = `<td colspan="10">No shifts found.</td>`;
          tableBody.appendChild(emptyRow);
        }

        shiftsTable.appendChild(tableBody);

        const deleteButton = document.createElement("button");
        deleteButton.className = "delete-table-button";
        deleteButton.textContent = "Delete All";
        deleteButton.addEventListener("click", () => {
          deleteShiftsTable(date);
        });

        shiftsList.appendChild(shiftsTable);
        shiftsList.appendChild(deleteButton);
      });
    })
    .catch((error) => console.log(error));
}

function deleteShiftsTable(date){
  fetch(`/delete-shifts-date/${formatBackDate(date)}`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        console.log('All shifts deleted for this day successfully');
        location.reload();
        window.scrollTo(0, 0);
      } else {
        console.error('Error deleting all shifts for this day');
      }
    })
    .catch(error => {
      console.error('Error deleting all shifts for this day', error);
    });
}

fetchShifts();

var selected_dates = [];
function editShift(id_shift){
  const deleteButton = document.getElementById("delete-all-doctors-button");
  deleteButton.style.display="none";
  const shiftsTable = document.getElementById("shifts-list");
  shiftsTable.style.display="none";
  var form = document.getElementById("shift-form");
  form.style.display = "block";

  fetch(`/shift-info/${id_shift}`)
    .then((response) => response.json())
    .then((shift) => {
      var [hour, minute] = shift.startHour.split(':').slice(0, 2);
      var formattedHour = parseInt(hour, 10).toString();
      var timp_inceput = `${formattedHour}:${minute}`;
      document.getElementById("shift-start-hour").value = timp_inceput;

      [hour, minute] = shift.endHour.split(':').slice(0, 2);
      formattedHour = parseInt(hour, 10).toString();
      const timp_final = `${formattedHour}:${minute}`;
      document.getElementById("shift-end-hour").value = timp_final;

      var parts = shift.zi.split('-');
      var day = parseInt(parts[2], 10);
      var month = (parseInt(parts[1], 10) - 1);
      var year = parseInt(parts[0]);
      var data = new Date(year, month, day);
      cal.selectDate(data);
      selected_dates.push(data);

      const saveButton = document.getElementById("save-shift-btn");
      saveButton.addEventListener("click", function() {
        saveShift(shift);
      });
      const backButton = document.getElementById("back-button");
      backButton.addEventListener("click", function() {
        goBackFromShiftEdit();
      });

      const deleteButton = document.getElementById("delete-button");
      deleteButton.addEventListener("click", function() {
        deleteShift(shift);
      });
    })
    .catch((error) => console.log(error));

}

function saveShift(shift){
  const form = document.getElementById("add-shift-form");
  var formData = new FormData(form);

  var dates_to_str = selected_dates.reduce((acc, current_date) => {
		acc.push([current_date.getDate(),current_date.getMonth()+1,current_date.getFullYear()].join('-'));
		return acc;
	},[]);

  formData.append("dates",dates_to_str);
  formData.append("id_doctor",shift.id_doctor);

  fetch(`/update-shift/${shift.id}`, {
    method: "POST",
    body: formData,
  })
    .then((response) => response.text())
    .then((result) => {
      console.log(result);
      location.reload();
      window.scrollTo(0, 0);
    })
    .catch((error) => {
      console.error(error);
    });
}

function goBackFromShiftEdit(){
  const deleteButton = document.getElementById("delete-all-doctors-button");
  deleteButton.style.display="block";
  const shiftsTable = document.getElementById("shifts-list");
  const formular = document.getElementById("shift-form");
  formular.style.display = "none";
  shiftsTable.style.display = "block";
  document.getElementById("shift-start-hour").value = "-1";
  document.getElementById("shift-end-hour").value = "-1";
  console.log(selected_dates[0]);
  cal.unselectDate(selected_dates[0]);
  selected_dates = [];
  window.scrollTo(0, 0);
}

function deleteShift(shift){
  fetch(`/delete-shift/${shift.id}`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        console.log('Shift deleted successfully');
        location.reload();
        window.scrollTo(0, 0);
      } else {
        console.error('Error deleting shift');
      }
    })
    .catch(error => {
      console.error('Error deleting shift', error);
    });
}

var cal = new calendar(document.getElementById('calendar'), {
  selectDate: function (date) {
    if (!!selected_dates.find(d => d.getTime() === date.getTime())) {
      cal.unselectDate(date);
      selected_dates = selected_dates.filter(d => d.getTime() !== date.getTime());
    } else {
      cal.selectDate(date);
      selected_dates.push(date);
    }
    console.log(selected_dates);
  },
	onDayClick: function (date, evts) {
		if (!!selected_dates.find(d => d.getTime() === date.getTime())) {
			cal.unselectDate(date);
			selected_dates = selected_dates.filter(d => d.getTime() !== date.getTime());
		} else {
      console.log(date);
			cal.selectDate(date);
			selected_dates.push(date);
		}
		console.log(selected_dates);
	},
	onMonthChanged: function () {
		for (let i = 0; i < selected_dates.length; i++) {
			cal.selectDate(selected_dates[i]);
		}
	}
});

function deleteAllData(){
  fetch(`/delete-all-data`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        console.log('All data deleted successfully');
        location.reload();
        window.scrollTo(0, 0);
      } else {
        console.error('Error deleting all data');
      }
    })
    .catch(error => {
      console.error('Error deleting all data', error);
    });
}

function deleteAllShifts(){
  fetch(`/delete-all-shifts`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        console.log('All shifts deleted successfully');
        location.reload();
        window.scrollTo(0, 0);
      } else {
        console.error('Error deleting all shifts');
      }
    })
    .catch(error => {
      console.error('Error deleting all shifts', error);
    });
}