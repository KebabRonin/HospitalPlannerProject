package jdbc;

import java.util.List;

public class Doctor {
    private int id;
    private String nume;
    private String prenume;
    private String nr_telefon;
    private String email;
    private String image;
    private List<Specializare> specializari;
    private List<String> cabinete;

    public Doctor(){}
    public Doctor(String nume, String prenume) {
        this.nume = nume;
        this.prenume = prenume;
    }

    public Doctor(int id, String nume, String prenume, String nr_telefon, String email, String image, List<Specializare> specializari, List<String> cabinete) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.nr_telefon = nr_telefon;
        this.email = email;
        this.image = image;
        this.specializari = specializari;
        this.cabinete = cabinete;
    }

    public void setName(String name) {
        this.nume = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getNr_telefon() {
        return nr_telefon;
    }

    public void setNr_telefon(String nr_telefon) {
        this.nr_telefon = nr_telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Specializare> getSpecializari() {
        return specializari;
    }

    public void setSpecializari(List<Specializare> specializari) {
        this.specializari = specializari;
    }

    public List<String> getCabinete() {
        return cabinete;
    }

    public void setCabinete(List<String> cabinete) {
        this.cabinete = cabinete;
    }
}

