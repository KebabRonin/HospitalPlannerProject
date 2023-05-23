package jdbc;

public class Pacient {
    private int id;
    private String nume;
    private String prenume;
    private String data_nastere;
    private String adresa;

    public int getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getData_nastere() {
        return data_nastere;
    }

    public String getAdresa() {
        return adresa;
    }

    public String getNr_telefon() {
        return nr_telefon;
    }

    public String getEmail() {
        return email;
    }

    public String getParola() {
        return parola;
    }

    private String nr_telefon;
    private String email;
    private String parola;

    public Pacient(int id, String nume, String prenume, String data_nastere, String adresa, String nr_telefon, String email, String parola) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.data_nastere = data_nastere;
        this.adresa = adresa;
        this.nr_telefon = nr_telefon;
        this.email = email;
        this.parola = parola;
    }
}
