package jdbc;

import java.sql.Date;

public class Programare {
    private int id;
    private int id_pacient;
    private int id_doctor;
    private int id_cabinet;
    private Date data_programare;

    public Programare(){}
    public Programare(int id, int id_pacient, int id_doctor, int id_cabinet, Date data_programare) {
        this.id = id;
        this.id_pacient = id_pacient;
        this.id_doctor = id_doctor;
        this.id_cabinet = id_cabinet;
        this.data_programare = data_programare;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_cabinet() {
        return id_cabinet;
    }

    public void setId_cabinet(int id_cabinet) {
        this.id_cabinet = id_cabinet;
    }

    public int getId_doctor() {
        return id_doctor;
    }

    public void setId_doctor(int id_doctor) {
        this.id_doctor = id_doctor;
    }

    public int getId_pacient() {
        return id_pacient;
    }

    public void setId_pacient(int id_pacient) {
        this.id_pacient = id_pacient;
    }

    public Date getData_programare() {
        return data_programare;
    }

    public void setData_programare(Date data_programare) {
        this.data_programare = data_programare;
    }
}

