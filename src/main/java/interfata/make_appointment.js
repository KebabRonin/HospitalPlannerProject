var selected_dates = [];
var selected_doctor = -1;
var selected_hour = -1;
var selected_specializare = -1;
var cal;


var specializareForm = document.getElementById("specializare-form");
var calendarForm = document.getElementById("calendar-form");
var doctorForm = document.getElementById("doctor-form");
var hourForm = document.getElementById("hour-form");

var specializareSelect = document.getElementById("specializare");
var calendarSelect = document.getElementById("calendar");
var doctorSelect = document.getElementById("doctor");
var hourSelect = document.getElementById("hour");

function make_option(root, value, text) {
	var concrete_option = document.createElement("option");
	concrete_option.textContent = text;
	concrete_option.setAttribute("value", value);
	root.appendChild(concrete_option);
	return concrete_option;
}

function load_specializare_form() {
	calendarForm.style["display"] = "none";
	doctorForm.style["display"] = "none";
	hourForm.style["display"] = "none";

	var prevoius_selection = specializareSelect.value;
	specializareSelect.textContent = '';

	fetch("/specializari-info/existing-doctors")
		.then((response) => response.json())
		.then((data) => {

			if (data.length > 0) {
				make_option(specializareSelect, -1, "No specialisation selected");
				let index = 0;
				data.forEach((specializare) => {
					let current = make_option(specializareSelect, specializare.id, specializare.denumire);
					if(current.value == prevoius_selection) {
						current.selected = index;
					}
					index++;
				}
				);
			} else {
				make_option(specializareSelect, -1, "No specialisations available");
			}
		})
		.catch((error) => {
			console.log(error);
			specSelect.textContent = '';
			make_option(specializareSelect, -1, "No specialisations available");
	});

	specializareForm.style["display"] = "block";
	console.log('Ran ' + 'specializare')
}

load_specializare_form();

function load_doctor_form() {

	if(specializareSelect.value == -1) {
		alert("Must choose specialisation!");
		return;
	}

	specializareForm.style["display"] = "none";
	calendarForm.style["display"] = "none";
	hourForm.style["display"] = "none";

	var prevoius_selection = doctorSelect.value;
	doctorSelect.textContent = '';

	var qstring;
	if(specializareSelect.value > 0) {
		qstring = '?specialisation='+specializareSelect.value;
	}
	else {
		qstring = '';
	}

	fetch("/doctors-info" + qstring)
		.then((response) => response.json())
		.then((data) => {
			if (data.length > 0) {
				if(data.length > 1) {
					make_option(doctorSelect, -1, "Any doctor");
				}

				let index = 0;
				data.forEach((doctor) => {
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
	
	doctorSelect.selectedIndex = 0
	console.log(doctorSelect.selectedIndex)
		
	doctorForm.style["display"] = "block";
	console.log('Ran ' + 'doctor')
}

function load_calendar_form() {
	specializareForm.style["display"] = "none";
	doctorForm.style["display"] = "none";
	hourForm.style["display"] = "none";

	selected_dates = [];
	calendarSelect.textContent = '';

	cal = new calendar(document.getElementById('calendar'), {
		onDayClick: function (date, evts) {
			if (selected_dates.find(d => d.getTime() === date.getTime())) {
				cal.unselectDate(date);
				selected_dates = selected_dates.filter(d => d.getTime() !== date.getTime());
			} else {
				cal.selectDate(date);
				selected_dates.push(date);
			}
			console.log(selected_dates);
		},
		onMonthChanged: function () {
			loadOfMonth(cal);
			for (let i in selected_dates) {
				cal.selectDate(selected_dates[i]);
			}
			cal.drawCalendar();
		}
	});

	loadOfMonth(cal);

	calendarForm.style["display"] = "block";
	console.log('Ran ' + 'calendar')
}

async function loadOfMonth(cal) {
	var qstring = '';
	if(doctorSelect.value != -1) {
		qstring = '&doctor='+doctorSelect.value;
	}
	else if(specializareSelect.value != -1) {
		qstring = qstring.concat('&specialisation='+specializareSelect.value);
	}

	await fetch("/program-info/days?month="+cal.getCurrentMonth().month+"&year="+cal.year+qstring).then(response => response.json()).then(data => {
		for(let i = 1; i < 32; i++) {
			let ok = false;
			for(let j in data) {
				if(data[j] == i) {
					ok = true;
					break;
				}
			}
			if(ok == false) {
				cal.disableDate(new Date(cal.year, cal.getCurrentMonth().month-1, i));
			}
		}
	}).catch(error => console.log(error));
	await cal.drawCalendar();
}

async function load_hour_form() {
	if(selected_dates.length < 1) {
		alert("Must choose at least a date!");
		return;
	}


	specializareForm.style["display"] = "none";
	calendarForm.style["display"] = "none";
	doctorForm.style["display"] = "none";

	hourSelect.textContent = '';

	var qstring = '';
	if(doctorSelect.value > -1) {
		qstring = '&doctor='+doctorSelect.value;
	}
	else if(specializareSelect.value > -1) {
		qstring = qstring.concat('&specialisation='+specializareSelect.value);
	}

	var flag = false;

	selected_dates = selected_dates.sort();

	for(let i in selected_dates) {
		console.log(selected_dates[i]);
		await fetch("/program-info/hours?day="+selected_dates[i].getDate()+"&month="+cal.getCurrentMonth().month+"&year="+cal.year+qstring)
		.then((response) => response.json())
		.then((data) => {
			console.log(data);
			if (data.length > 0 && !(data.length == 1 && data[0] === "")) {
				if(data.length > 1 && flag == false) {
					make_option(hourSelect, -2, "Any time");
					flag = true;
				}
				console.log("Data: "+data)
				for(let j in data) {
					make_option(hourSelect, selected_dates[i].getDate()+"-"+cal.getCurrentMonth().month+"-"+cal.year+" "+data[j], selected_dates[i].getDate()+"-"+cal.getCurrentMonth().month+"-"+cal.year+" "+data[j]);
				}
			}
		})
		.catch((error) => {
			console.log(error);
			hourSelect.innerHTML = '';
			make_option(hourSelect, -1, "No hours available");
			flag = true;
		});		
	}

	if(flag == false){
		hourSelect.innerHTML = '';
		make_option(hourSelect, -1, "No hours available");
	}
	
	hourForm.style["display"] = "block";
	console.log('Ran ' + 'hour')
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
	var request = {};
	if(dates_to_str.length > 0) request.dates = dates_to_str;
	if(specializareSelect.value != -1) request.specializare = specializareSelect.value;
	if(doctorSelect.value != -1) request.doctor = doctorSelect.value;
	if(hourSelect.value != -1) {
		if(hourSelect.value == -2) {
			selected_hour = hourSelect.options[1].value;
		} else {
			selected_hour = hourSelect.value;
		}
		request.hour = selected_hour.split(" ")[1];
		request.dates = [selected_hour.split(" ")[0]];
	}
	
	console.log(request);
	
	//var request = {dates:dates_to_str, specializare: selected_specializare,doctor:selected_doctor, hour:selected_hour};
	
	fetch('/appointments/'+myCookie, {method:"POST", headers: new Headers({'content-type': 'application/json'}),body:JSON.stringify(request)})
		.then((response) => {
			console.log(JSON.stringify(response));
			if(response.status != 200) {
				throw new Error('promise chain cancelled');
			}
			response.json();
		})
		.then((data) => {
			window.location.href = '/your_appointments.html';
		})
		.catch((error) => {
			console.log(error);
			alert("failure");
		});
}