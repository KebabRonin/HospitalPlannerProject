package server;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class RequestProgramare {
    private int doctor;
    private Time hour;
    private List<Date> dates;
    private int specializare;

    @Override
    public String toString() {
        return "RequestProgramare{" +
                "id_doctor=" + doctor +
                ", hour='" + hour + '\'' +
                ", dates=" + dates +
                ", specializare=" + specializare +
                '}';
    }

    public int getDoctor() {
        return doctor;
    }

    public int getSpecializare() {
        return specializare;
    }

    public List<Date> getDates() {
        return dates;
    }

    public Time getHour() {
        return hour;
    }

    public void setDoctor(String id_doctor) {
        this.doctor = Integer.parseInt(id_doctor);
    }

    public void setDates(List<String> dates) throws ParseException {
        this.dates = new LinkedList<>();
        SimpleDateFormat d = new SimpleDateFormat("dd-M-yyyy");
        for (String str : dates) {
            this.dates.add(new java.sql.Date(d.parse(str).getTime()));
        }
    }

    public void setHour(String hour) throws ParseException {
        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm");
        try {
            this.hour = new Time(dtf.parse(hour).getTime());
        } catch (ParseException e) {
            if (hour.equals("-1")) {
                this.hour = null;
            }
            else {
                throw e;
            }
        }
    }

    public void setSpecializare(String specializare) {
        this.specializare = Integer.parseInt(specializare);
    }
}
