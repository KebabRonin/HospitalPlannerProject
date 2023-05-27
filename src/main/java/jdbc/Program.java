package jdbc;


import java.sql.Date;
import java.sql.Time;
import java.util.AbstractMap;

public class Program {
    private int id;
    private int id_doctor;
    private Date zi;
    private AbstractMap.SimpleEntry<Time, Time> perioada;

    public Program(int id, int id_doctor, Date zi, Time ora_inceput, Time ora_final) {
        this.id = id;
        this.id_doctor = id_doctor;
        this.zi = zi;
        this.perioada = new AbstractMap.SimpleEntry<>(ora_inceput, ora_final);
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

    public void setOra_final(Time ora_final) {
        this.perioada.setValue(ora_final);
    }

    public Time getOra_final() {
        return perioada.getValue();
    }

    public void setOra_inceput(Time ora_inceput) {
        this.perioada = new AbstractMap.SimpleEntry<>(ora_inceput, this.perioada.getValue());
    }

    public Time getOra_inceput() {
        return perioada.getKey();
    }

    public void setZi(Date zi) {
        this.zi = zi;
    }
}
