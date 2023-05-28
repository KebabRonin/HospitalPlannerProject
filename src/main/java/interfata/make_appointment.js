var selected_dates = [];
var selected_doctor = -1;
var selected_hour = -1;
var selected_specializare = -1;


var doctorSelect = document.getElementById("doctor");
var specSelect = document.getElementById("specializare");
var hourSelect = document.getElementById("hour");


function make_option(root, value, text) {
	var concrete_option = document.createElement("option");
	concrete_option.textContent = text;
	concrete_option.setAttribute("value", value);
	root.appendChild(concrete_option);
	return concrete_option;
}
var update_funcs = {
	"doctor": function () {
	var prevoius_selection = doctorSelect.value;
	doctorSelect.textContent = '';


	fetch("/doctors-info")
		.then((response) => response.json())
		.then((data) => {
			if (data.length > 0) {
				if(data.length > 1) {
					make_option(doctorSelect, -10, "Any doctor");
				}

				let index = 0;
				data.forEach((doctor) => {
					//de ce merge??
					let current = make_option(doctorSelect, doctor.id, "Dr. "+doctor.nume+" "+doctor.prenume);
					if(current.value == prevoius_selection) {
						current.selected = true;
					}
					index++;
				});
			} else {
				make_option(doctorSelect, -1, "No doctors available");
			}
		})
		.catch((error) => {
			console.log(error);
			doctorSelect.innerHTML = '';
			make_option(doctorSelect, -1, "No doctors available");
		});
		
		// for(let i = 0; i < doctorSelect.options.length; i++) {
		// 	if (doctorSelect.options[i].value == prevoius_selection) {
		// 		doctorSelect.options.selectedIndex = i;
		// 		doctorSelect.options[i].selected = true;
		// 		break;
		// 	}
		// }
		doctorSelect.selectedIndex = 0
		console.log(doctorSelect.selectedIndex)
		console.log('Ran ' + 'doctor')
	},
	"specializare": function () {
	var prevoius_selection = specSelect.value;
	specSelect.textContent = '';

	fetch("/specializari-info")
		.then((response) => response.json())
		.then((data) => {

			if (data.length > 0) {
				if(data.length > 1) {
					make_option(specSelect, -10, "Any specialisation");
				}

				let index = 0;
				data.forEach((specializare) => {
					//de ce merge??
					let current = make_option(specSelect, specializare.id, specializare.denumire);
					if(current.value == prevoius_selection) {
						current.selected = index;
					}
					index++;
				}
				);
			} else {
				make_option(specSelect, -1, "No specialisations available");
			}
		})
		.catch((error) => {
			console.log(error);
			specSelect.innerHTML = '';
			make_option(specSelect, -1, "No specialisations available");
		});
		specSelect.selectedIndex = 0;
		console.log('Ran ' + 'specializare')
	},
	"dates": function () {

		console.log('Ran ' + 'dates')
	},
	"hour": function () {
	var prevoius_selection = hourSelect.value;
	hourSelect.textContent = '';
	
	var qstr = "?";
	
	if(selected_doctor!=-1) qstr.concat(selected_doctor);
	if(selected_dates.length > 0) qstr.concat("&dates=").concat(selected_dates.map(d => [d.getDate(), d.getMonth(), d.getFullYear()].join("-")).join(","));
	make_option(hourSelect, "any", "Any hour");
	for (let i = 7; i <= 20; i++) {
		let current = make_option(hourSelect, i+":00", i+":00");
		if (prevoius_selection == current.value) {
			current.selected = true;
		}
		make_option(hourSelect, i+":30", i+":30");
		if (prevoius_selection == current.value) {
			current.selected = true;
		}
	}

	/*fetch("/program-info" + qstr)
		.then((response) => response.json())
		.then((data) => {
			
			hourSelect.textContent = '';

			if (data.length > 0) {
				make_option(hourSelect, 0, "Any hour");

				//data.forEach((specializare) => make_option(hourSelect, , ));
				for (let i = 7; i <= 20; i++) {
					make_option(hourSelect, i+":00", i+":00");
					make_option(hourSelect, i+":30", i+":30");
				}
			} else {
				make_option(hourSelect, -1, "No hours available");
			}
		})
		.catch((error) => {
			console.log(error);
			make_option(hourSelect, -1, "No hours available");
		});

		for(let i = 0; i < hourSelect.options.length; i++) {
			if (hourSelect.options[i].value == prevoius_selection) {
				hourSelect.options.selectedIndex = i;
				hourSelect.options[i].selected = true;
				break;
			}
		}*/
		hourSelect.selectedIndex = 0;
		console.log('Ran ' + 'hour')
	}
}


//Not used yet
var update_matrix = {
	//what to update
	"doctor": {
		//what just updated
		"specializare": function() {
			
		},
		"dates": function() {
			var myBody = {doctor:getSelectedDoctor()};
			if (myBody.doctor == -1) myBody = null;
			console.log(myBody);
			fetch("/program-info?month="+cal.getCurrentMonth(), {method:"GET", body:myBody}).then(response => response.json()).then(data => {
				
			}).catch(error => console.log(error));
		},
		"hour": function() {
			
		},
	},
	"specializare": {
		//what just updated
		"doctor": function() {
			
		},
		"dates": function() {
			
		},
		"hour": function() {
			
		}
	},
	"dates": {
		//what just updated
		"doctor": function() {
			
		},
		"specializare": function() {
			
		},
		"hour": function() {
			
		}
	},
	"hour": {
		//what just updated
		"doctor": function() {
			
		},
		"specializare": function() {
			
		},
		"dates": function() {
			
		}
	}
}

function update_event(func) {
	var e = document.getElementById(func);
	if(func in update_funcs) {
		eval('selected_'+func+' = e.options[e.selectedIndex].value;');
		console.log('selected_'+func+' '+eval('selected_'+func))
	}

	console.log(func + " chosen");
	for( i in update_funcs) {
		if (i != func) {
			update_funcs[i]();
			console.log('Ran ' + i)
		}
	}
	//update_funcs[func]();
}


var cal = new calendar(document.getElementById('calendar'), {
	onDayClick: function (date, evts) {
		if (!!selected_dates.find(d => d.getTime() === date.getTime())) {
			cal.unselectDate(date);
			selected_dates = selected_dates.filter(d => d.getTime() !== date.getTime());
		} else {
			cal.selectDate(date);
			selected_dates.push(date);
		}
		console.log(selected_dates);
		update_event('calendar')
	},
	onMonthChanged: function () {
		for (let i = 0; i < selected_dates.length; i++) {
			cal.selectDate(selected_dates[i]);
		}
	}
});

//WATCHES selected_dates, selected_doctors, selected_hours, selected_section
for( i in update_funcs) {
	update_funcs[i]();
}

function submit_form() {
	var e = document.getElementById("appointment-form");

	//confirm_dialog();

	var myCookie = document.cookie.split('userId=')
	if (myCookie.length === 2) myCookie = myCookie.pop().split(';').shift();
	else {
		window.href='/';
	}


	var dates_to_str = selected_dates.reduce((acc, current_date) => {
		acc.push([current_date.getDate(),current_date.getMonth()+1,current_date.getFullYear()].join('-'));
		return acc;
	},[]);
	// var request;
	// if(dates_to_str.length > 0) request["dates"] = dates_to_str;
	// if(selected_specializare != -1) request["specializare"] = selected_specializare;
	// if(selected_doctor != -1) request["doctor"] = selected_doctor;
	// if(selected_hour != -1) request["hour"] = selected_hour;
	//body:JSON.stringify(request)
	
	var request = {"dates":dates_to_str, "specializare": selected_specializare,"doctor":selected_doctor, "hour":selected_hour};
	
	fetch('/appointments/'+myCookie, {method:"POST", headers: new Headers({'content-type': 'application/json'}),body:JSON.stringify(request)})
		.then((response) => {
			console.log(JSON.stringify(response));
			if(response.status != 200) {
				throw new Error('promise chain cancelled');
			}
			response.json();
		})
		.then((data) => {
			alert("success");
			window.location.href = '/your_appointments.html';
		})
		.catch((error) => {
			console.log(error);
			alert("failure");
		});
}