function getDoctorInfo(id_doctor) {
    return fetch(`/doctors-info/${id_doctor}`)
      .then((response) => response.json())
      .then((doctor) => {
        const { nume, prenume, id_cabinet } = doctor;
  
        return fetch(`/cabinet-info/${id_cabinet}`)
          .then((response) => response.json())
          .then((cabinet) => {
            const { denumire } = cabinet;
  
            return { nume, prenume, denumire };
          });
      })
      .catch((error) => console.log(error));
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
        const appointmentsTable = document.createElement("table");
        appointmentsTable.id = "appointments-table";
  
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
        if (data.length > 0) {
          data.forEach((appointment) => {
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
            
                doctorNameCell.textContent = `${doctor.nume} ${doctor.prenume}`;
                cabinetNameCell.textContent = doctor.denumire;
              })
              .catch((error) => console.log(error));
  
            row
              .querySelector(".edit-appointment")
              .addEventListener("click", (event) => {
                const appointmentId = event.target.getAttribute(
                  "data-appointment-id"
                );
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
      })
      .catch((error) => console.log(error));
}

fetchAppointments();
  