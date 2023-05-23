package jpa;

import javax.persistence.*;

@Entity
@Table(name = "pacienti")
@NamedQueries({
        @NamedQuery(name = "Pacient.findAll",
                query = "select e from Pacient e order by e.nume"),
        @NamedQuery(name = "Pacient.findByName",
                query = "SELECT p FROM Pacient p WHERE p.nume LIKE :nume")

})
public class Pacient extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "id")
    @Column(name = "id")
    private int id;
    @Column(name = "nume")
    private String nume;
    @Column(name = "prenume")
    private String prenume;
    @Column(name = "data_nastere")
    private String data_nastere;
    @Column(name = "nr_telefon")
    private String nr_telefon;
    @Column(name = "adresa")
    private String adresa;
    @Column(name = "email")
    private String email;
    @Column(name = "parola")
    private String parola;

    public Pacient(){}
    public Pacient(String nume, String prenume){
        this.nume = nume;
        this.prenume = prenume;
    }

    public Pacient(String nume,String prenume,String data_nastere, String nr_telefon, String adresa, String email, String parola) {
        this.nume = nume;
        this.prenume = prenume;
        this.data_nastere = data_nastere;
        this.nr_telefon = nr_telefon;
        this.adresa = adresa;
        this.email = email;
        this.parola = parola;
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

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}

