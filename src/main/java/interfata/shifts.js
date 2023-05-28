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
        const shiftsTable = document.createElement("table");
        shiftsTable.id = "shifts-table";
  
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
        if (data.length > 0) {
          data.forEach((shift) => {
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
        shiftsList.appendChild(shiftsTable);
      })
      .catch((error) => console.log(error));
}

fetchShifts();
    