package jdbc;


import java.sql.Date;
import java.sql.Time;
import java.util.AbstractMap;

public class Program {
    private int id;
    private int id_doctor;
    private Date zi;
    private Time startHour;
    private Time endHour;

    public Program(int id, int id_doctor, Date zi, Time startHour, Time endHour) {
        this.id = id;
        this.id_doctor = id_doctor;
        this.zi = zi;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_doctor() {
        return id_doctor;
    }

    public void setId_doctor(int id_doctor) {
        this.id_doctor = id_doctor;
    }

    public Date getZi() {
        return zi;
    }

    public void setZi(Date zi) {
        this.zi = zi;
    }

    public Time getStartHour() {
        return startHour;
    }

    public void setStartHour(Time startHour) {
        this.startHour = startHour;
    }

    public Time getEndHour() {
        return endHour;
    }

    public void setEndHour(Time endHour) {
        this.endHour = endHour;
    }
}
