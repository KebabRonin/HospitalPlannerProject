package jpa;

import javax.persistence.*;

@Entity
@Table(name = "specializari")
@NamedQueries({
        @NamedQuery(name = "Specializare.findAll",
                query = "select e from Specializare e order by e.denumire"),
        @NamedQuery(name = "Specializare.findByName",
                query = "SELECT p FROM Specializare p WHERE p.denumire LIKE :denumire")

})
public class Specializare extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "id")
    @Column(name = "id")
    private int id;
    @Column(name = "denumire")
    private String denumire;


    public Specializare(){}
    public Specializare(String denumire) {
        this.denumire = denumire;
    }
    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }
    public String getDenumire(){
        return denumire;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

