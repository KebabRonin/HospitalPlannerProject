function showDoctorForm() {
  var textAndButton = document.getElementById('text-and-button');
  var form = document.getElementById('doctor-form');

  textAndButton.style.display = 'none';
  form.style.display = 'block';
}

function addDoctor() {
  // Handle form submission logic here
  
  var textAndButton = document.getElementById('text-and-button');
  var form = document.getElementById('doctor-form');

  textAndButton.style.display = 'block';
  form.style.display = 'none';
}

function handleFileInputChange(input) {
  var fileLabel = document.getElementById('file-name');
  fileLabel.textContent = input.files[0].name;
}
