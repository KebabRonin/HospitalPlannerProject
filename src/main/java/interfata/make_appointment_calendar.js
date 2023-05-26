var selected_dates = [];
console.log(selected_dates)
var div = document.getElementById('calendar');
var start_date, end_date;
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