package jpa;

import javax.persistence.*;

@Entity
@Table(name = "cabinete")
@NamedQueries({
        @NamedQuery(name = "Cabinet.findAll",
                query = "select e from Cabinet e order by e.denumire"),
        @NamedQuery(name = "Cabinet.findByName",
                query = "SELECT p FROM Cabinet p WHERE p.denumire LIKE :denumire")

})
public class Cabinet extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "id")
    @Column(name = "id")
    private int id;
    @Column(name = "denumire")
    private String denumire;
    @Column(name = "etaj")
    private String etaj;

    public Cabinet(){}
    public Cabinet(String denumire) {
        this.denumire = denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDenumire() {
        return denumire;
    }


    public String getEtaj() {
        return etaj;
    }

    public void setEtaj(String etaj) {
        this.etaj = etaj;
    }
}

