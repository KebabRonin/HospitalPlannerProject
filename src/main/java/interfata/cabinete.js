function getLastTwoPartsOfPath(filePath) {
  try {
    const parts = filePath.split("\\");
    return parts.slice(-2).join("\\");
  } catch (error) {
    return null;
  }
}

function getCabinetImage(imagePath) {
  if (imagePath) {
    return getLastTwoPartsOfPath(imagePath);
  }
  return "cabinet_pictures/no_cabinet_image.png";
}

function extractFileName(filePath) {
  const lastIndex = filePath.lastIndexOf("\\") || filePath.lastIndexOf("/");
  const fileName = filePath.substring(lastIndex + 1);
  return fileName;
}

/*EDIT CABINET*/
function editCabinet(id_cabinet) {
  showCabinetForm();
  fetch(`/cabinet-info/${id_cabinet}`)
    .then((response) => response.json())
    .then((cabinet) => {
      editCabinetForm(cabinet);
    })
    .catch((error) => console.log(error));
}

function editCabinetForm(cabinet){
  document.getElementById("form-title").textContent = "Edit Cabinet";
  document.getElementById("cabinet-name").value = cabinet.denumire;
  document.getElementById("cabinet-floor").value = cabinet.etaj;

  const pictureInput = document.getElementById("cabinet-picture");
  const fileLabel = document.querySelector(".file-input-label");
  const fileName = document.getElementById("file-name");

  if (cabinet.image) {
    fileName.textContent = extractFileName(cabinet.image);
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
    saveCabinet(cabinet.id);
  });

  const specificFormGroup = document.getElementById("specific-form-group");

  const deleteButton = document.createElement("button");
  deleteButton.classList.add("delete-btn");
  deleteButton.textContent = "Delete";
  deleteButton.setAttribute("onclick", "deleteCabinet()");
  deleteButton.setAttribute("type", "button");
  deleteButton.onclick = function() {
    deleteCabinet(cabinet.id);
  };
  specificFormGroup.appendChild(deleteButton);
}

function resetForm(){
  document.getElementById("form-title").textContent = "Add a Cabinet";
  document.getElementById("cabinet-name").value = "";
  document.getElementById("cabinet-floor").value = "";

  const pictureInput = document.getElementById("cabinet-picture");
  const fileLabel = document.querySelector(".file-input-label");
  const fileName = document.getElementById("file-name");
  fileName.textContent = "";
  fileLabel.textContent = "Choose File";
  pictureInput.removeEventListener("change", handleFileInputChange);
  pictureInput.addEventListener("change", function() {
    handleFileInputChange(this);
  });

  if(document.getElementById("save-btn") != null){
    const addButton = document.getElementById("save-btn");
    addButton.id = "add-button";
    addButton.textContent = "Add";
    addButton.removeEventListener("click", saveCabinet);
  }
  
  const specificFormGroup = document.getElementById("specific-form-group");
  if(specificFormGroup.querySelector(".delete-btn") != null){
    const deleteButton = specificFormGroup.querySelector(".delete-btn");
    specificFormGroup.removeChild(deleteButton);
  }
}

function deleteCabinet(id_cabinet){
  var cabinetTitle = document.getElementById("cabinets-title");
  var cabinets = document.getElementById("cabinet-list");
  var button = document.getElementById("add-cabinet-button");
  const form = document.getElementById("cabinet-form");
  
  fetch(`/delete-cabinet/${id_cabinet}`, {
    method: 'DELETE'
  })
    .then(response => {
      if (response.ok) {
        console.log('Cabinet deleted successfully');
        resetForm();
        fetchCabinete();
        form.style.display = "none";
        cabinetTitle.style.display = "block";
        cabinets.style.display = "flex";
        button.style.display = "block";
      } else {
        console.error('Error deleting cabinet');
      }
    })
    .catch(error => {
      console.error('Error deleting cabinet', error);
    });
}

function saveCabinet(id_cabinet){
  const formular = document.getElementById("cabinet-form");
  var button = document.getElementById("add-cabinet-button");
  const cabinetListDiv = document.getElementById("cabinet-list");
  const cabinetsTitle = document.getElementById("cabinets-title");

  const form = document.getElementById("add-cabinet-form");
  const formData = new FormData(form);

  formData.append("id",id_cabinet);
  fetch("/update-cabinet", {
    method: "POST",
    body: formData,
  })
    .then((response) => response.text())
    .then((result) => {
      console.log(result);
      formular.style.display = "none";
      button.style.display = "flex";

      fetchCabinete();
      resetForm();
      cabinetListDiv.style.display = "flex";
      cabinetsTitle.style.display = "block";
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

function showCabinetForm() {
  var cabinetTitle = document.getElementById("cabinets-title");
  var cabinets = document.getElementById("cabinet-list");
  var button = document.getElementById("add-cabinet-button");
  var form = document.getElementById("cabinet-form");

  cabinetTitle.style.display = "none";
  cabinets.style.display = "none";
  button.style.display = "none";
  form.style.display = "block";
}

function addCabinet() {
  var cabinetTitle = document.getElementById("cabinets-title");
  var formDiv = document.getElementById("cabinet-form");
  var form = document.getElementById("add-cabinet-form");
  var cabinets = document.getElementById("cabinet-list");
  var button = document.getElementById("add-cabinet-button");
  var formData = new FormData(form);

  fetch("/add-cabinet", {
    method: "POST",
    body: formData,
  })
    .then((response) => response.text())
    .then((result) => {
      console.log(result);
      formDiv.style.display = "none";
      cabinetTitle.style.display = "block";
      cabinets.style.display = "flex";
      button.style.display = "flex";

      fetchCabinete();
    })
    .catch((error) => {
      console.error(error);
    });
}

function handleFileInputChange(input) {
  var fileLabel = document.getElementById("file-name");
  fileLabel.textContent = input.files[0].name;
}

function goBack() {
  var cabinetTitle = document.getElementById("cabinets-title");
  var cabinets = document.getElementById("cabinet-list");
  cabinetTitle.style.display = "block";
  cabinets.style.display = "flex";
  document.getElementById("cabinet-form").style.display = "none";
  document.getElementById("add-cabinet-button").style.display = "flex";

  resetForm();
}

function getDoctorsByCabinet(id_cabinet) {
  return fetch(`/cabinet-doctor/${id_cabinet}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error('Network response was not OK');
      }
      return response.text();
    })
    .then((data) => {
      try {
        const doctor = JSON.parse(data);
        if (!doctor || Object.keys(doctor).length === 0) {
          return "No Doctor";
        } else {
          const { nume, prenume } = doctor;
          return { nume, prenume };
        }
      } catch (error) {
        return "No Doctor";
      }
    })
    .catch((error) => console.log(error));
}

function fetchCabinete() {
  fetch("/cabinete-info")
    .then((response) => response.json())
    .then((data) => {
      const cabinetListDiv = document.getElementById("cabinet-list");
      cabinetListDiv.innerHTML=``;

      var cabinetTitle = document.getElementById("cabinets-title");
      const cabinetListTable = document.createElement("table");
      cabinetListTable.id = "cabinet-list-table";

      const tableHeader = document.createElement("thead");
      tableHeader.innerHTML = `
        <tr>
          <th>Picture</th>
          <th>ID</th>
          <th>Cabinet Name</th>
          <th>Floor</th>
          <th>Doctor</th>
          <th>Options</th>
        </tr>
      `;
      cabinetListTable.appendChild(tableHeader);

      const tableBody = document.createElement("tbody");

      if (data.length > 0) {
        data.forEach((cabinet) => {
          const row = document.createElement("tr");
          row.innerHTML = `
            <td><img src="${getCabinetImage(cabinet.image)}" alt="Cabinet Image"></td>
            <td>${cabinet.id}</td>
            <td>${cabinet.denumire}</td>
            <td>${cabinet.etaj}</td>
            <td id="doctor-name-${cabinet.id}"></td>
            <td>
              <button class="edit-cabinet" data-cabinet-id="${cabinet.id}">Edit</button>
            </td>
          `;

          getDoctorsByCabinet(cabinet.id)
          .then((doctor) => {
            const doctorNameCell = row.querySelector(`#doctor-name-${cabinet.id}`);
            if (typeof doctor.nume === "undefined") {
              doctorNameCell.textContent = "Unassigned";
            } else {
              doctorNameCell.textContent = `${doctor.nume} ${doctor.prenume}`;
            }
          })
          .catch((error) => console.log(error));

          row
            .querySelector(".edit-cabinet")
            .addEventListener("click", (event) => {
              const cabinetId = event.target.getAttribute("data-cabinet-id");
              editCabinet(cabinetId);
            });

          tableBody.appendChild(row);
        });

        cabinetListTable.appendChild(tableBody);
        cabinetListDiv.appendChild(cabinetListTable);
        cabinetTitle.style.display = "block";
      } else {
        const noCabinetsRow = document.createElement("tr");
        noCabinetsRow.innerHTML = `
          <td colspan="5">No cabinets found.</td>
        `;
        tableBody.appendChild(noCabinetsRow);

        cabinetListTable.appendChild(tableBody);
        const cabinetListDiv = document.getElementById("cabinet-list");
        cabinetListDiv.appendChild(cabinetListTable);
        cabinetTitle.style.display = "block";
      }
    })
    .catch((error) => console.log(error));
}

fetchCabinete();
