package jdbc;

public class Cabinet {
    private int id;
    private String denumire;
    private String image;
    private int etaj;

    public Cabinet(Integer id, String denumire, int etaj, String image) {
        this.id = id;
        this.denumire = denumire;
        this.etaj = etaj;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getDenumire() {
        return denumire;
    }

    public int getEtaj() {
        return etaj;
    }

    public String getImage() {
        return image;
    }

}

