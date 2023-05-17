package jpa;

import javax.persistence.*;

@Entity
@Table(name = "doctori")
@NamedQueries({
        @NamedQuery(name = "Doctor.findAll",
                query = "select e from Doctor e order by e.nume"),
        @NamedQuery(name = "Doctor.findByName",
                query = "SELECT p FROM Doctor p WHERE p.nume LIKE :nume")

})
public class Doctor extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "id")
    @Column(name = "id")
    private int id;
    @Column(name = "nume")
    private String nume;
    @Column(name = "prenume")
    private String prenume;
    @Column(name = "nr_telefon")
    private Integer nr_telefon;
    @Column(name = "email")
    private String email;

    public Doctor(){}
    public Doctor(String nume, String prenume) {
        this.nume = nume;
        this.prenume = prenume;
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

    public Integer getNr_telefon() {
        return nr_telefon;
    }

    public void setNr_telefon(Integer nr_telefon) {
        this.nr_telefon = nr_telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}

