package jpa;

import javax.persistence.*;

@Entity
@Table(name = "doctori_specializari")
public class DoctorSpecializare extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "id")
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @ManyToOne
    @JoinColumn(name = "specializare_id")
    private Specializare specializare;


    public DoctorSpecializare(){}
    public DoctorSpecializare(Doctor d, Specializare s) {
        this.doctor = d;
        this.specializare = s;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

