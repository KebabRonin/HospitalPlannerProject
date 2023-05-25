var selected_dates = [];
var div = document.getElementById('calendar');
var start_date, end_date;
var cal = new calendar(div, {
	onDayClick: function(date){
		console.log();
		if(!!selected_dates.find(d => d.getTime() === date.getTime())){ 
			cal.unselectDate(date);
			//selected_dates = selected_dates.filter(value => value.getTime() === date.getTime());
			console.log('Hi');
		}else {
			cal.selectDate(date);
			//selected_dates.push(date);
			console.log('Ho');
		}
		selected_dates = cal.getSelection();
		console.log(selected_dates);
	}
});