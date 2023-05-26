package jdbc;
import javax.print.Doc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    public void create(String nume, String prenume, String nr_telefon, String email, String image) throws SQLException {
        try (Connection con = Database.getConnection();
        PreparedStatement pstmt = con.prepareStatement(
                "insert into doctori (nume,prenume,nr_telefon,email,image) values (?,?,?,?,?)")) {
            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setString(3, nr_telefon);
            pstmt.setString(4, email);
            pstmt.setString(5, image);

            pstmt.executeUpdate();
        }
    }
    public int findByName(String name) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "select id from doctori where name=(?)")) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : null;
        }
    }

    public List<Doctor> findAll() throws SQLException {
        List<Doctor> rez = new ArrayList<>();
        try (Connection con = Database.getConnection();
        Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, nume, prenume, email, nr_telefon, image, id_cabinet from doctori")) {
            while(rs.next()) {
                int id = rs.getInt("id");
                String nume = rs.getString("nume");
                String prenume = rs.getString("prenume");
                String nr_telefon = rs.getString("nr_telefon");
                String email = rs.getString("email");
                String image = rs.getString("image");
                int id_cabinet = rs.getInt("id_cabinet");

                var specializare = new SpecializareDAO();
                var specializari = specializare.findSpecializariByDoctorId(id);

                rez.add(new Doctor(id,nume, prenume, nr_telefon, email, image, specializari, id_cabinet));
            }
        }
        return rez;
    }

    public Doctor findById(int id) throws SQLException {
        try (Connection con = Database.getConnection();
        Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, nume, prenume, nr_telefon, email, image, id_cabinet from doctori where id='" + id + "'")) {
            if (rs.next()) {
                String nume = rs.getString("nume");
                String prenume = rs.getString("prenume");
                String nr_telefon = rs.getString("nr_telefon");
                String email = rs.getString("email");
                String image = rs.getString("image");
                int id_cabinet = rs.getInt("id_cabinet");

                var specializare = new SpecializareDAO();
                var specializari = specializare.findSpecializariByDoctorId(id);


                return new Doctor(id,nume, prenume, nr_telefon, email,image,specializari,id_cabinet);
            } else {
                return null;
            }
        }
    }
    public int findByEmail(String email) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement("select id from doctori where email=(?)");
        ) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
