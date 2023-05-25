function getLastTwoPartsOfPath(filePath) {
  const parts = filePath.split("\\");
  return parts.slice(-2).join("\\");
}

fetch("/doctors-info")
  .then((response) => response.json())
  .then((data) => {
    var doctorsTitle = document.getElementById("doctors-title");
    const doctorListDiv = document.getElementById("doctor-list");
    const noDoctorsMessage = document.getElementById("text-and-button");

    if (data.length > 0) {
      noDoctorsMessage.style.display = "none";
      
      data.forEach((doctor) => {
        const doctorElement = document.createElement("div");
        doctorElement.classList.add("doctor-card");
        const imageSource =
          getLastTwoPartsOfPath(doctor.image) ||
          "doctor_pictures/no_doctor_picture.png";

        let specializariText = "No Specialization";
        if (doctor.specializari && doctor.specializari.length > 0) {
          specializariText = doctor.specializari
            .map((specializare) => specializare.denumire)
            .join(", ");
        }

        let cabineteText = "No Cabinets";
        if (doctor.cabinete && doctor.cabinete.length > 0) {
          cabineteText = doctor.cabinete
            .map((cabinet) => cabinet)
            .join(", ");
        }

        doctorElement.innerHTML = `
          <img src="${imageSource}" alt="Doctor Image">
          <div class="doctor-details">
            <p class="doctor-id">${doctor.id}</p>
            <p class="doctor-name">${doctor.nume} ${doctor.prenume}</p>
            <p class="doctor-email">${doctor.email}</p>
            <p class="doctor-phone">${doctor.nr_telefon}</p>
            <p class="doctor-specializari">${specializariText}</p>
            <p class="doctor-cabinete">${cabineteText}</p>
          </div>
          <button id="view-details">View Details</button>
          <button id="edit">Edit</button>
        `;
        doctorListDiv.appendChild(doctorElement);
      });
    } else {
      doctorsTitle.style.display = "none";
      noDoctorsMessage.style.display = "block";
    }
  })
  .catch((error) => console.log(error));

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

//Trebuie facut fetch si aici
function goBack() {
  var doctorsTitle = document.getElementById("doctors-title");
  var doctorList = document.getElementById("doctor-list");
  doctorsTitle.style.display = "block";
  doctorList.style.display = "block";
  document.getElementById("doctor-form").style.display = "none";
  document.getElementById("add-doctor-button").style.display = "flex";
}

function addDoctor() {
  var textAndButton = document.getElementById("text-and-button");
  var form = document.getElementById("doctor-form");
  var button = document.getElementById("add-doctor-button");

  var formular = document.getElementById("add-doctor-form");

  var formData = new FormData(formular);
  fetch("/add-doctor", {
    method: "POST",
    body: formData,
  })
    .then((response) => response.text())
    .then((result) => {
      console.log(result);
      textAndButton.style.display = "block";
      form.style.display = "none";
      button.style.display = "flex";

      fetch("/doctors-info")
        .then((response) => response.json())
        .then((data) => {
          var doctorsTitle = document.getElementById("doctors-title");
          const doctorListDiv = document.getElementById("doctor-list");
          doctorListDiv.style.display = "block";
          doctorListDiv.innerHTML= ``
          doctorsTitle.style.display = "block";
          const noDoctorsMessage = document.getElementById("text-and-button");

          if (data.length > 0) {
            noDoctorsMessage.style.display = "none";

            data.forEach((doctor) => {
              const doctorElement = document.createElement("div");
              doctorElement.classList.add("doctor-card");
              const imageSource =
                getLastTwoPartsOfPath(doctor.image) ||
                "doctor_pictures/no_doctor_picture.png";

              let specializariText = "No Specialization";
              if (doctor.specializari && doctor.specializari.length > 0) {
                specializariText = doctor.specializari
                  .map((specializare) => specializare.denumire)
                  .join(", ");
              }

              let cabineteText = "No Cabinets";
              if (doctor.cabinete && doctor.cabinete.length > 0) {
                cabineteText = doctor.cabinete
                  .map((cabinet) => cabinet)
                  .join(", ");
              }

              doctorElement.innerHTML = `
          <img src="${imageSource}" alt="Doctor Image">
          <div class="doctor-details">
            <p class="doctor-id">${doctor.id}</p>
            <p class="doctor-name">${doctor.nume} ${doctor.prenume}</p>
            <p class="doctor-email">${doctor.email}</p>
            <p class="doctor-phone">${doctor.nr_telefon}</p>
            <p class="doctor-specializari">${specializariText}</p>
            <p class="doctor-specializari">${cabineteText}</p>
          </div>
          <button id="view-details">View Details</button>
          <button id="edit">Edit</button>
        `;
              doctorListDiv.appendChild(doctorElement);
            });
          } else {
            doctorsTitle.style.display = "none";
            noDoctorsMessage.style.display = "block";
          }
        })
        .catch((error) => console.log(error));
    })
    .catch((error) => {
      console.error(error);
    });
}

function handleFileInputChange(input) {
  var fileLabel = document.getElementById("file-name");
  fileLabel.textContent = input.files[0].name;
}
