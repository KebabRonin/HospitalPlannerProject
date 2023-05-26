var selected_dates = [];
var div = document.getElementById('calendar');
var cal = new calendar(div, {
	onDayClick: function(date, evts){
		if(!!selected_dates.find(d => d.getTime() === date.getTime())){ 
			cal.unselectDate(date);
			selected_dates = selected_dates.filter(d => d.getTime() !== date.getTime());
		}else {
			cal.selectDate(date);
			selected_dates.push(date);
		}
		console.log(selected_dates);
	},
	onMonthChanged: function() {
		for (let i = 0; i < selected_dates.length; i++) {
			cal.selectDate(selected_dates[i]);
		}
	}
});


fetch("/doctors-info")
  .then((response) => response.json())
  .then((data) => {
    var doctorSelect = document.getElementById("doctor");
	doctorSelect.textContent = '';

	var concrete_option;

    if (data.length > 0) {
        concrete_option = document.createElement("option");
		concrete_option.textContent = "Any Doctor";
		concrete_option.setAttribute("value", "any");
		doctorSelect.appendChild(concrete_option);
      
      data.forEach((doctor) => {
        concrete_option = document.createElement("option");
		let d_name = doctor.nume + " " + doctor.prenume;
		concrete_option.textContent = "Dr. " + d_name;
		concrete_option.setAttribute("value", d_name);
		doctorSelect.appendChild(concrete_option);
      });
    } else {
		concrete_option = document.createElement("option");
		concrete_option.textContent = "No doctors available";
		concrete_option.setAttribute("value", "none");
	  	doctorSelect.appendChild(concrete_option);
    }
  })
  .catch((error) => console.log(error));

  fetch("/doctors-info")
  .then((response) => response.json())
  .then((data) => {
    var hourSelect = document.getElementById("hours");
	hourSelect.textContent = '';

	var concrete_option;

    if (data.length > 0) {
		for(let i = 7; i <= 18; i++) {
			concrete_option = document.createElement("option");
			concrete_option.textContent = i + ":00";
			concrete_option.setAttribute("value", concrete_option.textContent);
			hourSelect.appendChild(concrete_option);
			concrete_option.textContent = i + ":30";
			concrete_option.setAttribute("value", concrete_option.textContent);
			hourSelect.appendChild(concrete_option);
			
		}
      //data.forEach((doctor) => {});
    } else {
		concrete_option = document.createElement("option");
		concrete_option.textContent = "No hours";
		concrete_option.setAttribute("value", "none");
		hourSelect.appendChild(concrete_option);
    }
  })
  .catch((error) => console.log(error));