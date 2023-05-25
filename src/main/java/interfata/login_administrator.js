var loginButton = document.getElementById("login-submit-btn");
loginButton.disabled = true;

function updateErrorMessage(inputElement, errorMessageElement, value, errorMessage) {
  if (!value) {
    inputElement.classList.add("kYUBna");
    errorMessageElement.textContent = errorMessage;
    errorMessageElement.style.color = "rgb(226, 27, 60)";
  } else {
    errorMessageElement.textContent = "";
    inputElement.classList.remove("kYUBna");
  }
}

function checkFormCompletion() {
  var email = document.getElementById("email").value;
  var password = document.getElementById("password").value;

  loginButton.disabled = !(email && password);
}

document.getElementById("email").addEventListener("input", checkFormCompletion);
document.getElementById("password").addEventListener("input", checkFormCompletion);

loginButton.addEventListener("click", function(event) {
  event.preventDefault();

  var emailInput = document.getElementById("email");
  var passwordInput = document.getElementById("password");

  var email = document.getElementById("email").value;
  var password = document.getElementById("password").value;

  var loginErrorMessage = document.getElementById("login-error-msg");

  var xhr = new XMLHttpRequest();
  xhr.open("POST", "/login-administrator", true);
  xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 && xhr.status === 200) {
      var response = xhr.responseText;
      console.log(response);

      if (response === "Log in successful!") {
        window.location.href = "appointments.html";
      }
      else if(response === "Incorrect."){
        updateErrorMessage(passwordInput, loginErrorMessage, "", 'Invalid data.');
        updateErrorMessage(emailInput, loginErrorMessage, "", 'Invalid data.');
      }
    }
  };
  xhr.send(
    "email=" + encodeURIComponent(email) +
    "&password=" + encodeURIComponent(password)
  );
});
