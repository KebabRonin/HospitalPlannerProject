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
  
function getPacientInfo(id_pacient) {
return fetch(`/pacient-info/${id_pacient}`)
    .then((response) => response.json())
    .then((pacient) => {
    const { nume, prenume } = pacient;
    return { nume, prenume };
    })
    .catch((error) => console.log(error));
}

function fetchAppointments() {
  const appointmentsList = document.getElementById("appointments-list");
  appointmentsList.innerHTML = "";
  appointmentsList.style.display = "block";

  const appointmentsTitle = document.createElement("h1");
  appointmentsTitle.textContent = `Appointments`;
  appointmentsList.appendChild(appointmentsTitle);

  fetch(`/all-appointments`)
    .then((response) => response.json())
    .then((data) => {
      const appointmentsByDate = {};

      data.forEach((appointment) => {
        const date = formatDate(appointment.data_programare);
        if (!appointmentsByDate[date]) {
          appointmentsByDate[date] = [appointment];
        } else {
          appointmentsByDate[date].push(appointment);
        }
      });

      Object.keys(appointmentsByDate).forEach((date) => {
        const appointmentsTable = document.createElement("table");
        appointmentsTable.id = `appointments-table-${date}`;

        const tableTitle = document.createElement("caption");
        tableTitle.textContent = date;
        appointmentsTable.appendChild(tableTitle);

        const tableHeader = document.createElement("thead");
        tableHeader.innerHTML = `
          <tr>
            <th>ID</th>
            <th>Patient</th>
            <th>Doctor</th>
            <th>Cabinet</th>
            <th>Date</th>
            <th>Hour</th>
            <th>Options</th>
          </tr>
        `;
        appointmentsTable.appendChild(tableHeader);

        const tableBody = document.createElement("tbody");
        if (appointmentsByDate[date].length > 0) {
          appointmentsByDate[date].forEach((appointment) => {
            const row = document.createElement("tr");
            row.innerHTML = `
              <td>${appointment.id}</td>
              <td id="pacient-name-${appointment.id_pacient}"></td>
              <td id="doctor-name-${appointment.id_doctor}"></td>
              <td id="cabinet-name-${appointment.id_doctor}"></td>
              <td>${appointment.data_programare}</td>
              <td>${appointment.ora_programare}</td>
              <td>
                <button class="edit-appointment" data-appointment-id="${appointment.id}">Edit</button>
              </td>
            `;

            getPacientInfo(appointment.id_pacient)
              .then((pacient) => {
                const pacientNameCell = row.querySelector(
                  `#pacient-name-${appointment.id_pacient}`
                );
                pacientNameCell.textContent = `${pacient.nume} ${pacient.prenume}`;
              })
              .catch((error) => console.log(error));

            getDoctorInfo(appointment.id_doctor)
              .then((doctor) => {
                const doctorNameCell = row.querySelector(`#doctor-name-${appointment.id_doctor}`);
                const cabinetNameCell = row.querySelector(`#cabinet-name-${appointment.id_doctor}`);
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
              .querySelector(".edit-appointment")
              .addEventListener("click", (event) => {
                const appointmentId = event.target.getAttribute("data-appointment-id");
                editAppointment(appointmentId);
              });

            tableBody.appendChild(row);
          });
        } else {
          const emptyRow = document.createElement("tr");
          emptyRow.innerHTML = `<td colspan="10">No appointments found.</td>`;
          tableBody.appendChild(emptyRow);
        }

        appointmentsTable.appendChild(tableBody);
        appointmentsList.appendChild(appointmentsTable);

        const deleteButton = document.createElement("button");
        deleteButton.classList.add("delete-table-button");
        deleteButton.textContent = "Delete All";
        deleteButton.addEventListener("click", () => {
          deleteAppointmentsTable(date);
        });

        appointmentsList.appendChild(deleteButton);
      });
    })
    .catch((error) => console.log(error));
}

function deleteAppointmentsTable(date){
  fetch(`/delete-appointments-date/${formatBackDate(date)}`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        console.log('All appointments deleted for this day successfully');
        location.reload();
        window.scrollTo(0, 0);
      } else {
        console.error('Error deleting all appointments for this day');
      }
    })
    .catch(error => {
      console.error('Error deleting all appointments for this day', error);
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
        window.scrollTo(0, 0);
      } else {
        console.error('Error deleting all data');
      }
    })
    .catch(error => {
      console.error('Error deleting all data', error);
    });
}

function deleteAllAppointments(){
  fetch(`/delete-all-appointments`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        console.log('All appointments deleted successfully');
        location.reload();
        window.scrollTo(0, 0);
      } else {
        console.error('Error deleting all appointments');
      }
    })
    .catch(error => {
      console.error('Error deleting all appointments', error);
    });
}

fetchAppointments();

/*EDIT APPOINTMENTS*/
var selected_dates = [];
var cal_appointments = new calendar(document.getElementById('calendar-appointments'), {
  selectDate: function (date) {
    if (!!selected_dates.find(d => d.getTime() === date.getTime())) {
      cal_appointments.unselectDate(date);
      selected_dates = selected_dates.filter(d => d.getTime() !== date.getTime());
    } else {
      cal_appointments.selectDate(date);
      selected_dates.push(date);
    }
    console.log(selected_dates);
  },
	onDayClick: function (date, evts) {
		if (!!selected_dates.find(d => d.getTime() === date.getTime())) {
			cal_appointments.unselectDate(date);
			selected_dates = selected_dates.filter(d => d.getTime() !== date.getTime());
		} else {
      console.log(date);
			cal_appointments.selectDate(date);
			selected_dates.push(date);
		}
		console.log(selected_dates);
	},
	onMonthChanged: function () {
		for (let i = 0; i < selected_dates.length; i++) {
			cal_appointments.selectDate(selected_dates[i]);
		}
	}
});
function fetchSpecializariAppointments(){
  fetch("/specializari-info")
    .then((response) => response.json())
    .then((data) => {
      const specializariListSelect = document.getElementById(
        "specializare"
      );
      specializariListSelect.innerHTML =
        '<option value="">Any Specialization</option>';

      if (data.length > 0) {
        data.forEach((specializare) => {
          const specializareElement = document.createElement("option");
          specializareElement.value = specializare.id;
          specializareElement.innerHTML = `
          <div class="specializare-details">
            <p class="specializare-name">${specializare.denumire}</p>
          </div>
        `;
          specializariListSelect.appendChild(specializareElement);
        });
      } else {
        const emptyOption = document.createElement("option");
        emptyOption.disabled = true;
        emptyOption.selected = true;
        emptyOption.textContent = "No specializations available";
        specializariListSelect.innerHTML = "";
        specializariListSelect.appendChild(emptyOption);
      }
    })
    .catch((error) => console.log(error));
}

function fetchDoctoriAppointments(){
  fetch("/doctors-info")
    .then((response) => response.json())
    .then((data) => {
      const doctoriListSelect = document.getElementById(
        "doctor"
      );
      doctoriListSelect.innerHTML =
        '<option value="">Any Doctor</option>';

      if (data.length > 0) {
        data.forEach((doctor) => {
          const doctorElement = document.createElement("option");
          doctorElement.value = doctor.id;
          doctorElement.innerHTML = `
          <div class="doctor-details">
            <p class="doctor-name">${doctor.nume} ${doctor.prenume}</p>
          </div>
        `;
          doctoriListSelect.appendChild(doctorElement);
        });
      } else {
        const emptyOption = document.createElement("option");
        emptyOption.disabled = true;
        emptyOption.selected = true;
        emptyOption.textContent = "No doctors available";
        doctoriListSelect.innerHTML = "";
        doctoriListSelect.appendChild(emptyOption);
      }
    })
    .catch((error) => console.log(error));
}

function editAppointment(appointmentId) {
  const deleteAllButton = document.getElementById("delete-all-doctors-button");
  deleteAllButton.style.display="none";
  const appointmentsList = document.getElementById("appointments-list");
  appointmentsList.style.display = "none";
  const appointmentsForm = document.getElementById("container");
  appointmentsForm.style.display = "block";

  const saveButton = document.getElementById("add-appointment-btn");
  const deleteButton = document.getElementById("delete-appointment-btn");

  Promise.all([fetchSpecializariAppointments(), fetchDoctoriAppointments()])
    .then(() => {
      fetch(`/appointment-info/${appointmentId}`)
        .then((response) => response.json())
        .then((appointment) => {

          console.log(appointment);
          saveButton.onclick = function(){
            saveAppointment(appointment.id,appointment.id_doctor,appointment.id_pacient);
          }
          deleteButton.onclick = function(){
            deleteAppointment(appointment.id,appointment.id_doctor);
          }

          var [hour, minute] = appointment.ora_programare.split(':').slice(0, 2);
          var formattedHour = parseInt(hour, 10).toString();
          var appointmentHour = `${formattedHour}:${minute}`;
          document.getElementById("hour").value = appointmentHour;

          var parts = appointment.data_programare.split('-');
          var day = parseInt(parts[2], 10);
          var month = (parseInt(parts[1], 10) - 1);
          var year = parseInt(parts[0]);
          var data = new Date(year, month, day);
          cal_appointments.selectDate(data);
          selected_dates.push(data);

          fetch(`/doctors-info/${appointment.id_doctor}`)
          .then((response) => response.json())
          .then((doctor) => {
            document.getElementById("doctor").value = doctor.id;
          })
          .catch((error) => console.log(error));

          fetch(`/specializare-info/${appointment.id_doctor}`)
            .then((response) => response.json())
            .then((specializare) => {
              document.getElementById("specializare").value = specializare.id;
            })
            .catch((error) => console.log(error));
        })
        .catch((error) => console.log(error));
    });
}

function goBackFromAppointmentsEdit(){
  const deleteAllButton = document.getElementById("delete-all-doctors-button");
  deleteAllButton.style.display="block";
  const appointmentsList = document.getElementById("appointments-list");
  appointmentsList.style.display="block";
  const appointmentsForm = document.getElementById("container");
  appointmentsForm.style.display="none";
  document.getElementById("hour").value = "";
  document.getElementById("doctor").value = "";
  document.getElementById("specializare").value = "";
  cal_appointments.unselectDate(selected_dates[0]);
  selected_dates = [];
  window.scrollTo(0, 0);
}

function saveAppointment(appointmentId,id_doctor,id_pacient){
  const form = document.getElementById("appointment-form");
  const formData = new FormData(form);

  var dates_to_str = selected_dates.reduce((acc, current_date) => {
		acc.push([current_date.getDate(),current_date.getMonth()+1,current_date.getFullYear()].join('-'));
		return acc;
	},[]);

  formData.append("id_pacient",id_pacient);
  formData.append("dates",dates_to_str);
  fetch(`/update-appointment/${appointmentId}`, {
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
      console.error("Error:", error);
    });
}

function deleteAppointment(appointmentId,id_doctor){
  fetch(`/delete-appointment/${appointmentId}`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        console.log('Appointment deleted successfully');
        location.reload();
        window.scrollTo(0, 0);
      } else {
        console.error('Error deleting appointment');
      }
    })
    .catch(error => {
      console.error('Error deleting appointment', error);
    });
}


  