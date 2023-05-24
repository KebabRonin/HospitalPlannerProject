function getLastTwoPartsOfPath(filePath) {
  const parts = filePath.split('\\');
  return parts.slice(-2).join('\\');
}

fetch('/cabinete-info')
  .then(response => response.json())
  .then(data => {
    const cabinetListDiv = document.getElementById('cabinet-list');
    const noCabinetsMessage = document.getElementById('text-and-button');

    if (data.length > 0) {
      noCabinetsMessage.style.display = 'none';

      data.forEach(cabinet => {
        const cabinetElement = document.createElement('div');
        cabinetElement.classList.add('cabinet-box');
        const imageSource = getLastTwoPartsOfPath(cabinet.image) || 'cabinet_pictures/no_cabinet_image.png'; 
        
        cabinetElement.innerHTML = `
          <img src="${imageSource}" alt="Cabinet Image">
          <div class="cabinet-details">
            <p class="cabinet-name">Cabinet Name: ${cabinet.denumire}</p>
            <p class="cabinet-floor">Floor: ${cabinet.etaj}</p>
          </div>
        `;
        cabinetListDiv.appendChild(cabinetElement);
      });
    } else {
      noCabinetsMessage.style.display = 'flex';
    }
  })
  .catch(error => console.log(error));


function showCabinetForm() {
  var cabinets = document.getElementById('cabinet-list');
  var text = document.getElementById('text-and-button');
  var button = document.getElementById('add-cabinet-button');
  var form = document.getElementById('cabinet-form');

  cabinets.style.display = 'none';
  text.style.display = 'none';
  button.style.display = 'none';
  form.style.display = 'block';
}

function addCabinet() {
  var formDiv = document.getElementById('cabinet-form');
  var form = document.getElementById('add-cabinet-form');
  var cabinets = document.getElementById('cabinet-list');
  var button = document.getElementById('add-cabinet-button');
  var formData = new FormData(form);

  fetch('/add-cabinet', {
    method: 'POST',
    body: formData
  })
  .then(response => response.text())
  .then(result => {
    console.log(result);
    formDiv.style.display = 'none';
    cabinets.style.display = 'flex';
    button.style.display = 'flex';

    fetch('/cabinete-info')
  .then(response => response.json())
  .then(data => {
    const cabinetListDiv = document.getElementById('cabinet-list');
    const noCabinetsMessage = document.getElementById('text-and-button');

    if (data.length > 0) {
      noCabinetsMessage.style.display = 'none';
      cabinetListDiv.innerHTML = '';

      data.forEach(cabinet => {
        const cabinetElement = document.createElement('div');
        cabinetElement.classList.add('cabinet-box');
        const imageSource = getLastTwoPartsOfPath(cabinet.image) || 'cabinet_pictures/no_cabinet_image.png'; 
        
        cabinetElement.innerHTML = `
          <img src="${imageSource}" alt="Cabinet Image">
          <div class="cabinet-details">
            <p class="cabinet-name">Cabinet Name: ${cabinet.denumire}</p>
            <p class="cabinet-floor">Floor: ${cabinet.etaj}</p>
          </div>
        `;
        cabinetListDiv.appendChild(cabinetElement);
      });
    } else {
      noCabinetsMessage.style.display = 'flex';
    }
  })
  .catch(error => console.log(error));
  })
  .catch(error => {
    console.error(error);
  });
}

function handleFileInputChange(input) {
  var fileLabel = document.getElementById('file-name');
  fileLabel.textContent = input.files[0].name;
}
