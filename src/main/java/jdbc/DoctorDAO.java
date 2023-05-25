package jdbc;
import javax.print.Doc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    public void create(String nume, String prenume, String nr_telefon, String email, String image) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "insert into doctori (id,nume,prenume,nr_telefon,email,image) values (?,?,?,?,?,?)")) {
            pstmt.setInt(1,this.idMax() + 1);
            pstmt.setString(2, nume);
            pstmt.setString(3, prenume);
            pstmt.setString(4, nr_telefon);
            pstmt.setString(5, email);
            pstmt.setString(6, image);

            pstmt.executeUpdate();
        }
    }
    public int findByName(String name) throws SQLException {
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id from doctori where name='" + name + "'")) {
            return rs.next() ? rs.getInt(1) : null;
        }
    }

    public List<Doctor> findAll() throws SQLException {
        List<Doctor> rez = new ArrayList<>();
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, nume, prenume, email, nr_telefon, image from doctori")) {
            while(rs.next()) {
                int id = rs.getInt("id");
                String nume = rs.getString("nume");
                String prenume = rs.getString("prenume");
                String nr_telefon = rs.getString("nr_telefon");
                String email = rs.getString("email");
                String image = rs.getString("image");

                var specializare = new SpecializareDAO();
                var specializari = specializare.findSpecializariByDoctorId(id);

                var cabinet = new CabinetDAO();
                var cabinete = cabinet.findCabineteByDoctorId(id);

                rez.add(new Doctor(id,nume, prenume, nr_telefon, email, image, specializari, cabinete));
            }
        }
        return rez;
    }

    public Doctor findById(int id) throws SQLException {
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, nume, prenume, nr_telefon, email, image from doctori where id='" + id + "'")) {
            if (rs.next()) {
                String nume = rs.getString("nume");
                String prenume = rs.getString("prenume");
                String nr_telefon = rs.getString("nr_telefon");
                String email = rs.getString("email");
                String image = rs.getString("image");

                var specializare = new SpecializareDAO();
                var specializari = specializare.findSpecializariByDoctorId(id);

                var cabinet = new CabinetDAO();
                var cabinete = cabinet.findCabineteByDoctorId(id);

                return new Doctor(id,nume, prenume, nr_telefon, email,image,specializari,cabinete);
            } else {
                return null;
            }
        }
    }
    public int findByEmail(String email) throws SQLException {
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id from doctori where email='" + email + "'")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int idMax() throws SQLException {
        Connection con = Database.getConnection();
        Statement stmt = con.createStatement();

        int var4;
        try {
            ResultSet rs = stmt.executeQuery("select max(id) from doctori");

            try {
                var4 = rs.next() ? rs.getInt(1) : null;
            } catch (Throwable var8) {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                    }
                }

                throw var8;
            }

            if (rs != null) {
                rs.close();
            }
        } catch (Throwable var9) {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Throwable var6) {
                    var9.addSuppressed(var6);
                }
            }

            throw var9;
        }

        if (stmt != null) {
            stmt.close();
        }

        return var4;
    }
}
