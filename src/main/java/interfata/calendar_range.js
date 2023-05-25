var startinput = document.getElementById('date-range-picker-start');
var endinput = document.getElementById('date-range-picker-end');
var div = document.getElementById('cal2');
var start_date, end_date;
var cal = new calendar(div, {
	onDayClick: function(date){
		if(start_date && end_date){ 
			cal.clearSelection(date);
			start_date = end_date = null;
		}else if(start_date){
			cal.clearSelection(date);
			cal.selectDateRange(start_date, date);
			end_date = date;
			endinput.value = date.toLocaleDateString();
		}else{
			cal.selectDate(date);
			start_date = date;
			startinput.value = date.toLocaleDateString();
		}
	}
});