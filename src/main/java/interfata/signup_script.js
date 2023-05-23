var submitButton = document.getElementById("signup-submit-btn");
submitButton.disabled = true;

function updateErrorMessage(inputElement, errorMessageElement, value, errorMessage) {
  if (!value) {
    inputElement.classList.add("kYUBna");
    errorMessageElement.textContent = errorMessage;
    errorMessageElement.style.color = "rgb(226, 27, 60)";
  } else {
    errorMessageElement.textContent = "";
    //inputElement.classList.remove("kYUBna");
  }
}

function checkFormCompletion() {
  var firstName = document.getElementById("username").value;
  var lastName = document.getElementsByName("lastName")[0].value;
  var dateOfBirth = document.getElementById("date").value;
  var address = document.getElementsByName("address")[0].value;
  var phoneNumber = document.getElementById("phone").value;
  var email = document.getElementById("email").value;
  var password = document.getElementById("password").value;

  submitButton.disabled = !(firstName && lastName && dateOfBirth && address && phoneNumber && email && password);
}

document.getElementById("username").addEventListener("input", checkFormCompletion);
document.getElementsByName("lastName")[0].addEventListener("input", checkFormCompletion);
document.getElementById("date").addEventListener("input", checkFormCompletion);
document.getElementsByName("address")[0].addEventListener("input", checkFormCompletion);
document.getElementById("phone").addEventListener("input", checkFormCompletion);
document.getElementById("email").addEventListener("input", checkFormCompletion);
document.getElementById("password").addEventListener("input", checkFormCompletion);

submitButton.addEventListener("click", function(event) {
  event.preventDefault();

  var firstNameInput = document.getElementById("username");
  var lastNameInput = document.getElementsByName("lastName");
  var dateOfBirthInput = document.getElementById("date");
  var addressInput = document.getElementsByName("address");
  var phoneNumberInput = document.getElementById("phone");
  var emailInput = document.getElementById("email");
  var passwordInput = document.getElementById("password");

  var firstName = document.getElementById("username").value;
  var lastName = document.getElementsByName("lastName")[0].value;
  var dateOfBirth = document.getElementById("date").value;
  var address = document.getElementsByName("address")[0].value;
  var phoneNumber = document.getElementById("phone").value;
  var email = document.getElementById("email").value;
  var password = document.getElementById("password").value;

  var firstNameErrorMessage = document.getElementById("firstName-error-message");
  var lastNameErrorMessage = document.getElementById("lastName-error-message");
  var dateErrorMessage = document.getElementById("date-error-message");
  var addressErrorMessage = document.getElementById("address-error-message");
  var phoneErrorMessage = document.getElementById("phone-error-message");
  var emailErrorMessage = document.getElementById("email-error-message");
  var passwordErrorMessage = document.getElementById("password-error-message");


    if (password.length < 8) {
      updateErrorMessage(passwordInput, passwordErrorMessage, "", "Password should have at least 8 characters.");
    } else {
      updateErrorMessage(passwordInput, passwordErrorMessage, password, "");
    }

    var firstNameRegex = /^[A-Za-z]+$/;
    if (!firstName.match(firstNameRegex)) {
    updateErrorMessage(firstNameInput, firstNameErrorMessage, "", "First name must contain only letters.");
    } else {
    updateErrorMessage(firstNameInput, firstNameErrorMessage, firstName, "");
    }

    var lastNameRegex = /^[A-Za-z]+$/;
    if (!lastName.match(lastNameRegex)) {
    updateErrorMessage(lastNameInput, lastNameErrorMessage, "", "Last name must contain only letters.");
    } else {
    updateErrorMessage(lastNameInput, lastNameErrorMessage, lastName, "");
    }

    var addressRegex = /^[A-Za-z, ]+$/;
    if (!address.match(addressRegex)) {
    updateErrorMessage(addressInput, addressErrorMessage, "", "Address must contain only letters, spaces, and commas.");
    } else {
    updateErrorMessage(addressInput, addressErrorMessage, address, "");
    }

    var phoneRegex = /^\d{10}$/;
    if (!phoneNumber.match(phoneRegex)) {
    updateErrorMessage(phoneNumberInput, phoneErrorMessage, "", "Phone number must be exactly 10 digits.");
    } else {
    updateErrorMessage(phoneNumberInput, phoneErrorMessage, phoneNumber, "");
    }

    var currentDate = new Date();
    var enteredDate = new Date(dateOfBirth);
    var minDate = new Date();
    minDate.setFullYear(currentDate.getFullYear() - 18);
    if (enteredDate > minDate) {
    updateErrorMessage(dateOfBirthInput, dateErrorMessage, "", "You must be at least 18 years old.");
    } else {
    updateErrorMessage(dateOfBirthInput, dateErrorMessage, dateOfBirth, "");
    }

    var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email.match(emailRegex)) {
        updateErrorMessage(emailInput, emailErrorMessage, "", 'Invalid email address.');
        } else {
        updateErrorMessage(emailInput, emailErrorMessage, email, "");
        }

    console.log(firstNameErrorMessage.textContent + " " + lastNameErrorMessage.textContent + " " + dateErrorMessage.textContent + " " + addressErrorMessage.textContent + " " + phoneErrorMessage.textContent + " " + emailErrorMessage.textContent + " " + passwordErrorMessage.textContent)
    if(firstNameErrorMessage.textContent != "" || lastNameErrorMessage.textContent != "" || dateErrorMessage.textContent != "" || addressErrorMessage.textContent != "" || phoneErrorMessage.textContent != "" || emailErrorMessage.textContent != "" || passwordErrorMessage.textContent != "")
    {
        return;
    }

       var xhr = new XMLHttpRequest();
       xhr.open("POST", "/signup", true);
       xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
       xhr.onreadystatechange = function() {
         if (xhr.readyState === 4 && xhr.status === 200) {
           var response = xhr.responseText;
           console.log(response);

           if (response === "Sign up successful!") {
             window.location.href = "/admin.html";
           }
           else if (response === "Failed to sign up."){
            updateErrorMessage(emailInput, emailErrorMessage, "", 'Email address already exists.');
           }
         }
       };
       xhr.send(
         "firstName=" + encodeURIComponent(firstName) +
         "&lastName=" + encodeURIComponent(lastName) +
         "&date=" + encodeURIComponent(dateOfBirth) +
         "&address=" + encodeURIComponent(address) +
         "&phone=" + encodeURIComponent(phoneNumber) +
         "&email=" + encodeURIComponent(email) +
         "&password=" + encodeURIComponent(password)
       );
});
