var dateFormat = function () {
	var	token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,
		timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
		timezoneClip = /[^-+\dA-Z]/g,
		pad = function (val, len) {
			val = String(val);
			len = len || 2;
			while (val.length < len) val = "0" + val;
			return val;
		};

	return function (date, mask, utc) {
		var dF = dateFormat;

		if (arguments.length == 1 && Object.prototype.toString.call(date) == "[object String]" && !/\d/.test(date)) {
			mask = date;
			date = undefined;
		}

		date = date ? new Date(date) : new Date;
		if (isNaN(date)) throw SyntaxError("invalid date");

		mask = String(dF.masks[mask] || mask || dF.masks["default"]);

		if (mask.slice(0, 4) == "UTC:") {
			mask = mask.slice(4);
			utc = true;
		}

		var	_ = utc ? "getUTC" : "get",
			d = date[_ + "Date"](),
			D = date[_ + "Day"](),
			m = date[_ + "Month"](),
			y = date[_ + "FullYear"](),
			H = date[_ + "Hours"](),
			M = date[_ + "Minutes"](),
			s = date[_ + "Seconds"](),
			L = date[_ + "Milliseconds"](),
			o = utc ? 0 : date.getTimezoneOffset(),
			flags = {
				d:    d,
				dd:   pad(d),
				ddd:  dF.i18n.dayNames[D],
				dddd: dF.i18n.dayNames[D + 7],
				m:    m + 1,
				mm:   pad(m + 1),
				mmm:  dF.i18n.monthNames[m],
				mmmm: dF.i18n.monthNames[m + 12],
				yy:   String(y).slice(2),
				yyyy: y,
				h:    H % 12 || 12,
				hh:   pad(H % 12 || 12),
				H:    H,
				HH:   pad(H),
				M:    M,
				MM:   pad(M),
				s:    s,
				ss:   pad(s),
				l:    pad(L, 3),
				L:    pad(L > 99 ? Math.round(L / 10) : L),
				t:    H < 12 ? "a"  : "p",
				tt:   H < 12 ? "am" : "pm",
				T:    H < 12 ? "A"  : "P",
				TT:   H < 12 ? "AM" : "PM",
				Z:    utc ? "UTC" : (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
				o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
				S:    ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]
			};

		return mask.replace(token, function ($0) {
			return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);
		});
	};
}();

dateFormat.masks = {
	"default":      "ddd mmm dd yyyy HH:MM:ss",
	shortDate:      "m/d/yy",
	mediumDate:     "mmm d, yyyy",
	longDate:       "mmmm d, yyyy",
	fullDate:       "dddd, mmmm d, yyyy",
	shortTime:      "h:MM TT",
	mediumTime:     "h:MM:ss TT",
	longTime:       "h:MM:ss TT Z",
	isoDate:        "yyyy-mm-dd",
	isoTime:        "HH:MM:ss",
	isoDateTime:    "yyyy-mm-dd'T'HH:MM:ss",
	isoUtcDateTime: "UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"
};

dateFormat.i18n = {
	dayNames: [
		"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
		"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
	],
	monthNames: [
		"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
		"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
	]
};

Date.prototype.format = function (mask, utc) {
	return dateFormat(this, mask, utc);
};

function appendHtml(el, str) {
  var div = document.createElement('div');
  div.innerHTML = str;
  el.appendChild(div);
}

function format_date(date) {
        var d = new Date(date);
        return d.format("dddd, mmmm d, yyyy");
}

function getDoctorImage(imagePath) {
	if (imagePath) {
	  return getLastTwoPartsOfPath(imagePath);
	}
	return "doctor_pictures/no_doctor_picture.png";
}

async function load_appointments() {
	var ainfoel = document.getElementById('appointment-info');
	ainfoel.innerHTML = ''
	var myCookie = document.cookie.split('userId=')
	if (myCookie.length === 2) myCookie = myCookie.pop().split(';').shift();
	else {
		window.locatoion.href='/';
	}
	fetch('/appointments/'+myCookie, {method:"GET"})
	.then(response => response.json())
	.then(async data => {
		console.log(data);
		var resp;
		for(el of data) {
			//(el.id_doctor, el.data_programare, el.ora_programare)
			resp = await fetch('/doctors-info/'+el.id_doctor, {method:"GET"});
			var data = await resp.json();
			let nume_doctor = "Dr. "+data.nume+" "+data.prenume;
			let image_doctor = data.image;
			//(nume_doctor, data_programare, ora_programare, data.id_cabinet)
			resp = await fetch('/cabinet-info/'+data.id_cabinet, {method:"GET"});
			var data2 = await resp.json();
			var ora_programare = el.ora_programare.split(':').slice(start=0, end=2).join(':');
			var appointmentId = el.id;
			appendHtml(ainfoel, `
			<div class="appointment" id="appointment-info">
				<div class="info">
					<img src="${getDoctorImage(image_doctor)}" alt="Doctor Image">
					<p><strong>Doctor:</strong> ${nume_doctor}</p>
					<p><strong>Date:</strong> ${format_date(el.data_programare)}</p>
					<p><strong>Time:</strong> ${ora_programare}</p>
					<p><strong>Cabinet:</strong> ${data2.denumire}</p>
					<p><strong>Floor:</strong> ${data2.etaj}</p>
					<button class="cancel-btn" appointment-id="${appointmentId}">Cancel Appointment</button>
				</div>
			</div>`);

			ainfoel.addEventListener("click", (event) => {
				if (event.target.classList.contains("cancel-btn")) {
				  const appointmentId = event.target.getAttribute("appointment-id");
				  deleteAppointment(appointmentId);
				}
			});
		}

		if (ainfoel.innerHTML === "") {
			document.getElementById("no-appointments").style.display="flex";
		} else {
			document.getElementById("no-appointments").style.display="none";
	}
	})
	.catch(error => {
		console.error('Failed to retrieve user information:', error);
	});

}

function deleteAppointment(appointmentId){
	fetch(`/delete-appointment/${appointmentId}`, {
		method: 'DELETE'
	  })
		.then(response => {
		  if (response.ok) {
			console.log('Appointment deleted successfully');
			location.reload();
			window.scrollTo(0,0);
		  } else {
			console.error('Error deleting appointment');
		  }
		})
		.catch(error => {
		  console.error('Error deleting appointment', error);
		});
}

load_appointments();