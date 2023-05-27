function getLastTwoPartsOfPath(filePath) {
  try {
    const parts = filePath.split("\\");
    return parts.slice(-2).join("\\");
  } catch (error) {
    return null;
  }
}

function extractFileName(filePath) {
  const lastIndex = filePath.lastIndexOf("\\") || filePath.lastIndexOf("/");
  const fileName = filePath.substring(lastIndex + 1);
  return fileName;
}

function showDoctorForm() {
  var doctorsTitle = document.getElementById("doctors-title");
  var doctorList = document.getElementById("doctor-list");
  var textAndButton = document.getElementById("text-and-button");
  var button = document.getElementById("add-doctor-button");
  var form = document.getElementById("doctor-form");

  doctorsTitle.style.display = "none";
  doctorList.style.display = "none";
  button.style.display = "none";
  textAndButton.style.display = "none";
  form.style.display = "block";

  fetchCabineteSpecializari();
}

function showShiftForm(id_doctor) {
  var doctorsTitle = document.getElementById("doctors-title");
  var doctorList = document.getElementById("doctor-list");
  var textAndButton = document.getElementById("text-and-button");
  var button = document.getElementById("add-doctor-button");
  var form = document.getElementById("shift-form");

  doctorsTitle.style.display = "none";
  doctorList.style.display = "none";
  button.style.display = "none";
  textAndButton.style.display = "none";
  form.style.display = "block";
}

function editAppointment(appointmentId){

}

function getDoctorInfo(id_doctor) {
  return new Promise((resolve, reject) => {
    fetch(`/doctors-info/${id_doctor}`)
      .then((response) => response.json())
      .then((doctor) => {
        const { nume, prenume } = doctor;
        resolve({ nume, prenume });
      })
      .catch((error) => reject(error));
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

function showAppointmentsOfDoctor(id_doctor) {
  var doctorsTitle = document.getElementById("doctors-title");
  var doctorList = document.getElementById("doctor-list");
  var button = document.getElementById("add-doctor-button");

  doctorsTitle.style.display = "none";
  doctorList.style.display = "none";
  button.style.display = "none";

  const appointmentsList = document.getElementById("appointments-list");
  appointmentsList.innerHTML = "";
  appointmentsList.style.display = "block";

  getDoctorInfo(id_doctor)
    .then((doctor) => {
      const appointmentsTitle = document.createElement("h1");
      appointmentsTitle.textContent = `Appointments of Dr. ${doctor.nume} ${doctor.prenume}`;
      appointmentsList.appendChild(appointmentsTitle);

      fetch(`/doctor-appointments/${id_doctor}`)
        .then((response) => response.json())
        .then((data) => {
          const appointmentsTable = document.createElement("table");
          appointmentsTable.id = "appointments-table";

          const tableHeader = document.createElement("thead");
          tableHeader.innerHTML = `
            <tr>
              <th>ID</th>
              <th>Patient</th>
              <th>Date</th>
              <th>Hour</th>
              <th>Options</th>
            </tr>
          `;
          appointmentsTable.appendChild(tableHeader);

          const tableBody = document.createElement("tbody");
          if (data.length > 0) {
            console.log(data);
            data.forEach((appointment) => {
              const row = document.createElement("tr");
              row.innerHTML = `
                <td>${appointment.id}</td>
                <td id="pacient-name-${appointment.id_pacient}"></td>
                <td>${appointment.data_programare}</td>
                <td>${appointment.ora_programare}</td>
                <td>
                  <button class="edit-appointment" data-appointment-id="${appointment.id}">Edit</button>
                </td>
              `;
              getPacientInfo(appointment.id_pacient)
                .then((pacient) => {
                  const pacientNameCell = row.querySelector(`#pacient-name-${appointment.id_pacient}`);
                  pacientNameCell.textContent = `${pacient.nume} ${pacient.prenume}`;
                })
                .catch((error) => console.log(error));

              row.querySelector(".edit-appointment").addEventListener("click", (event) => {
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

          const backButtonDiv = document.createElement("div");
          backButtonDiv.id = "back-button-appointments";
          const backButton = document.createElement("button");
          backButton.textContent = "Back";
          backButton.addEventListener("click", function() {
            appointmentsList.style.display = "none";
            goBack();
          });
          backButtonDiv.appendChild(backButton);

          appointmentsList.appendChild(backButtonDiv);
        })
        .catch((error) => console.log(error));
    })
    .catch((error) => console.log(error));
}

/*EDIT DOCTOR*/ 
function editDoctor(id_doctor){
  var doctorsTitle = document.getElementById("doctors-title");
  var doctorList = document.getElementById("doctor-list");
  var textAndButton = document.getElementById("text-and-button");
  var button = document.getElementById("add-doctor-button");
  var form = document.getElementById("doctor-form");

  doctorsTitle.style.display = "none";
  doctorList.style.display = "none";
  button.style.display = "none";
  textAndButton.style.display = "none";
  form.style.display = "block";

  fetchCabineteSpecializari();
  fetch(`/doctors-info/${id_doctor}`)
    .then((response) => response.json())
    .then((doctor) => {
      editDoctorForm(doctor);
    })
    .catch((error) => console.log(error));
}

function editDoctorForm(doctor) {
  document.getElementById("form-title").textContent = "Edit Doctor";
  document.getElementById("doctor-first-name").value = doctor.nume;
  document.getElementById("doctor-last-name").value = doctor.prenume;
  document.getElementById("doctor-phone").value = doctor.nr_telefon;
  document.getElementById("doctor-email").value = doctor.email;

  document.getElementById("doctor-cabinet").value = doctor.id_cabinet;
  document.getElementById("doctor-specialization").value = doctor.specializari[0].id;

  const pictureInput = document.getElementById("doctor-picture");
  const fileLabel = document.querySelector(".file-input-label");
  const fileName = document.getElementById("file-name");

  if (doctor.image) {
    fileName.textContent = extractFileName(doctor.image);
    fileLabel.textContent = "Change File";
  } else {
    fileName.textContent = "";
    fileLabel.textContent = "Choose File";
  }
  
  pictureInput.removeEventListener("change", handleFileInputChange);
  pictureInput.addEventListener("change", function() {
    handleFileInputChange(this);
  });

  const addButton = document.getElementById("add-button");
  addButton.id = "save-btn";
  addButton.textContent = "Save";
  addButton.removeAttribute("onclick");
  addButton.addEventListener("click", function() {
    saveDoctor(doctor.id);
  });
  
  const backButton = document.querySelector(".back-btn");
  backButton.setAttribute("onclick", "goBackFromEdit()");

  const specificFormGroup = document.getElementById("specific-form-group");

  const deleteButton = document.createElement("button");
  deleteButton.classList.add("delete-btn");
  deleteButton.textContent = "Delete";
  deleteButton.setAttribute("onclick", "deleteDoctor()");
  deleteButton.setAttribute("type", "button");
  deleteButton.onclick = function() {
    deleteDoctor(doctor.id);
  };
  specificFormGroup.appendChild(deleteButton);
}

function deleteDoctor(id_doctor){
  const doctorListDiv = document.getElementById("doctor-list");
  const doctorsTitle = document.getElementById("doctors-title");
  fetch(`/delete-doctor/${id_doctor}`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        console.log('Doctor deleted successfully');
        const form = document.getElementById("doctor-form");
        const addButton = document.getElementById("add-doctor-button");
        addButton.style.display = "flex";
        form.style.display = "none";
        resetForm();
        fetchDoctorsInfo();
        doctorListDiv.style.display = "flex";
        doctorsTitle.style.display = "block";
      } else {
        console.error('Error deleting doctor');
      }
    })
    .catch(error => {
      console.error('Error deleting doctor', error);
    });
}

function resetForm() {
  document.getElementById("form-title").textContent = "Add Doctor";
  document.getElementById("doctor-first-name").value = "";
  document.getElementById("doctor-last-name").value = "";
  document.getElementById("doctor-phone").value = "";
  document.getElementById("doctor-email").value = "";

  document.getElementById("doctor-cabinet").value = "";
  document.getElementById("doctor-specialization").value = "";

  const pictureInput = document.getElementById("doctor-picture");
  const fileLabel = document.querySelector(".file-input-label");
  const fileName = document.getElementById("file-name");
  fileName.textContent = "";
  fileLabel.textContent = "Choose File";
  pictureInput.removeEventListener("change", handleFileInputChange);
  pictureInput.addEventListener("change", function() {
    handleFileInputChange(this);
  });

  const addButton = document.getElementById("save-btn");
  addButton.id = "add-button";
  addButton.textContent = "Add";
  addButton.removeEventListener("click", saveDoctor);

  const backButton = document.querySelector(".back-btn");
  backButton.setAttribute("onclick", "goBack()");

  const specificFormGroup = document.getElementById("specific-form-group");
  const deleteButton = specificFormGroup.querySelector(".delete-btn");
  specificFormGroup.removeChild(deleteButton);
}

function goBackFromEdit(){
  var doctorsTitle = document.getElementById("doctors-title");
  var doctorList = document.getElementById("doctor-list");
  doctorsTitle.style.display = "block";
  doctorList.style.display = "block";
  document.getElementById("doctor-form").style.display = "none";
  document.getElementById("add-doctor-button").style.display = "flex";
  resetForm();
  fetchDoctorsInfo();
}

function saveDoctor(id_doctor) {
  const form = document.getElementById("add-doctor-form");
  const formular = document.getElementById("doctor-form");
  var button = document.getElementById("add-doctor-button");
  const doctorListDiv = document.getElementById("doctor-list");
  const doctorsTitle = document.getElementById("doctors-title");
  const formData = new FormData(form);

  formData.append("id",id_doctor);
  fetch("/update-doctor", {
    method: "POST",
    body: formData,
  })
    .then((response) => response.text())
    .then((result) => {
      console.log(result);
      formular.style.display = "none";
      button.style.display = "flex";

      fetchDoctorsInfo();
      doctorListDiv.style.display = "flex";
      doctorsTitle.style.display = "block";
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}
/**/

function goBack() {
  var doctorsTitle = document.getElementById("doctors-title");
  var doctorList = document.getElementById("doctor-list");
  doctorsTitle.style.display = "block";
  doctorList.style.display = "block";
  document.getElementById("doctor-form").style.display = "none";
  document.getElementById("add-doctor-button").style.display = "flex";

  fetchDoctorsInfo();
}

function goBackFromShift() {
  var doctorsTitle = document.getElementById("doctors-title");
  var doctorList = document.getElementById("doctor-list");
  doctorsTitle.style.display = "block";
  doctorList.style.display = "block";
  document.getElementById("shift-form").style.display = "none";
  document.getElementById("add-doctor-button").style.display = "flex";
}

function addDoctor() {
  var textAndButton = document.getElementById("text-and-button");
  var form = document.getElementById("doctor-form");
  var button = document.getElementById("add-doctor-button");
  const doctorListDiv = document.getElementById("doctor-list");
  const doctorsTitle = document.getElementById("doctors-title");

  var formular = document.getElementById("add-doctor-form");

  var formData = new FormData(formular);
  fetch("/add-doctor", {
    method: "POST",
    body: formData,
  })
    .then((response) => response.text())
    .then((result) => {
      console.log(result);
      form.style.display = "none";
      button.style.display = "flex";

      fetchDoctorsInfo();
      doctorListDiv.style.display = "flex";
      doctorsTitle.style.display = "block";
    })
    .catch((error) => {
      console.error(error);
    });
}

function handleFileInputChange(input) {
  var fileLabel = document.getElementById("file-name");
  fileLabel.textContent = input.files[0].name;
}

function fetchCabineteSpecializari(){
  fetch("/cabinete-info")
    .then((response) => response.json())
    .then((data) => {
      const cabinetListSelect = document.getElementById("doctor-cabinet");
      cabinetListSelect.innerHTML =
        '<option value="">Select a cabinet</option>';

      if (data.length > 0) {
        data.forEach((cabinet) => {
          const cabinetElement = document.createElement("option");
          cabinetElement.value = cabinet.id;
          cabinetElement.innerHTML = `
          <div class="cabinet-details">
            <p class="cabinet-name">cabinet: ${cabinet.denumire}, Floor: ${cabinet.etaj}</p>
          </div>
        `;

          cabinetListSelect.appendChild(cabinetElement);
        });
      } else {
        const emptyOption = document.createElement("option");
        emptyOption.disabled = true;
        emptyOption.selected = true;
        emptyOption.textContent = "No cabinets available";
        cabinetListSelect.innerHTML = "";
        cabinetListSelect.appendChild(emptyOption);
      }
    })
    .catch((error) => console.log(error));

  fetch("/specializari-info")
    .then((response) => response.json())
    .then((data) => {
      const specializariListSelect = document.getElementById(
        "doctor-specialization"
      );
      specializariListSelect.innerHTML =
        '<option value="">Select a Specialization</option>';

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

function getSpecializariText(specializari) {
  if (specializari && specializari.length > 0) {
    return specializari.map((specializare) => specializare.denumire).join(", ");
  }
  return "No Specialization";
}

function getDoctorImage(imagePath) {
  if (imagePath) {
    return getLastTwoPartsOfPath(imagePath);
  }
  return "doctor_pictures/no_doctor_picture.png";
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

function fetchDoctorsInfo(){
  fetch("/doctors-info")
  .then((response) => response.json())
  .then((data) => {
    const doctorListDiv = document.getElementById("doctor-list");
    doctorListDiv.innerHTML=``;

    const doctorsTable = document.createElement("table");
    doctorsTable.id = "doctors-table";

    const tableHeader = document.createElement("thead");
    tableHeader.innerHTML = `
      <tr>
        <th>Picture</th>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
        <th>Phone</th>
        <th>Specializations</th>
        <th>Cabinet</th>
        <th>Options</th>
      </tr>
    `;
    doctorsTable.appendChild(tableHeader);

    const tableBody = document.createElement("tbody");

    if (data.length > 0) {
      data.forEach((doctor) => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td><img src="${getDoctorImage(doctor.image)}" alt="Doctor Image"></td>
          <td>${doctor.id}</td>
          <td>${doctor.nume} ${doctor.prenume}</td>
          <td>${doctor.email}</td>
          <td>${doctor.nr_telefon}</td>
          <td>${getSpecializariText(doctor.specializari)}</td>
          <td id="cabinet-name-${doctor.id_cabinet}"></td>
          <td>
            <button class="add-shift" data-doctor-id="${doctor.id}">Add Shift</button>
            <button class="view-appointments" data-doctor-id="${doctor.id}">Appointments</button>
            <button class="edit-doctor" data-doctor-id="${doctor.id}">Edit</button>
          </td>
        `;

        getCabinetInfo(doctor.id_cabinet)
        .then((cabinet) => {
          const cabinetNameCell = row.querySelector(`#cabinet-name-${doctor.id_cabinet}`);
          cabinetNameCell.textContent = cabinet.denumire;
        })
        .catch((error) => console.log(error));

        row.querySelector(".add-shift").addEventListener("click", (event) => {
          const doctorId = event.target.getAttribute("data-doctor-id");
          showShiftForm(doctorId);
        });

        row.querySelector(".view-appointments").addEventListener("click", (event) => {
          const doctorId = event.target.getAttribute("data-doctor-id");
          showAppointmentsOfDoctor(doctorId);
        });

        row.querySelector(".edit-doctor").addEventListener("click", (event) => {
          const doctorId = event.target.getAttribute("data-doctor-id");
          editDoctor(doctorId);
        });

        tableBody.appendChild(row);
      });
    } else {
      const emptyRow = document.createElement("tr");
      emptyRow.innerHTML = `<td colspan="10">No doctors found.</td>`;
      tableBody.appendChild(emptyRow);
    }

    doctorsTable.appendChild(tableBody);
    doctorListDiv.appendChild(doctorsTable);
  })
  .catch((error) => console.log(error));
}

fetchDoctorsInfo();



