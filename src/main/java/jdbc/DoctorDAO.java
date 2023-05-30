package jdbc;
import javax.print.Doc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorDAO {
    public void create(String nume, String prenume, String nr_telefon, String email, String image, Integer id_cabinet) throws SQLException {
        try (Connection con = Database.getConnection();
        PreparedStatement pstmt = con.prepareStatement(
                "insert into doctori (nume,prenume,nr_telefon,email,image,id_cabinet) values (?,?,?,?,?,?)")) {
            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setString(3, nr_telefon);
            pstmt.setString(4, email);
            pstmt.setString(5, image);
            pstmt.setInt(6,id_cabinet);

            pstmt.executeUpdate();
        }
    }

    public void create(String nume, String prenume, String nr_telefon, String email, Integer id_cabinet) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "insert into doctori (nume,prenume,nr_telefon,email,id_cabinet) values (?,?,?,?,?)")) {
            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setString(3, nr_telefon);
            pstmt.setString(4, email);
            pstmt.setInt(5,id_cabinet);

            pstmt.executeUpdate();
        }
    }

    public static void delete(int doctorId) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "DELETE FROM doctori WHERE id = ?")) {
            pstmt.setInt(1, doctorId);
            pstmt.executeUpdate();
        }
    }

    public static void delete() throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "DELETE FROM doctori")) {
            pstmt.executeUpdate();
        }
    }

    public void update(Integer id, String nume, String prenume, String nr_telefon, String email, String image, Integer id_cabinet) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "UPDATE doctori SET nume=?, prenume=?, nr_telefon=?, email=?, image=?, id_cabinet=? WHERE id=?")) {
            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setString(3, nr_telefon);
            pstmt.setString(4, email);
            pstmt.setString(5, image);
            pstmt.setInt(6, id_cabinet);
            pstmt.setInt(7, id);

            pstmt.executeUpdate();
        }
    }

    public void update(Integer id, String nume, String prenume, String nr_telefon, String email, Integer id_cabinet) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "UPDATE doctori SET nume=?, prenume=?, nr_telefon=?, email=?, id_cabinet=? WHERE id=?")) {
            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setString(3, nr_telefon);
            pstmt.setString(4, email);
            pstmt.setInt(5, id_cabinet);
            pstmt.setInt(6, id);

            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void update(Integer id_cabinet) throws SQLException {
        Connection con = Database.getConnection();
        try {
            List<Integer> doctorIds = new ArrayList<>();
            try (PreparedStatement fetchStmt = con.prepareStatement(
                    "SELECT id FROM doctori WHERE id_cabinet=?")) {
                fetchStmt.setInt(1, id_cabinet);
                ResultSet resultSet = fetchStmt.executeQuery();
                while (resultSet.next()) {
                    int doctorId = resultSet.getInt("id");
                    doctorIds.add(doctorId);
                }
            }

            try (PreparedStatement updateStmt = con.prepareStatement(
                    "UPDATE doctori SET id_cabinet=0 WHERE id=?")) {
                for (int doctorId : doctorIds) {
                    updateStmt.setInt(1, doctorId);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
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
                     "select id, nume, prenume, email, nr_telefon, image, id_cabinet from doctori order by id")) {
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

    public static Doctor findById(int id) throws SQLException {
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

    public static int findId(String nume, String prenume, String nr_telefon, String email, int id_cabinet) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement stmt = con.prepareStatement("select id from doctori where nume=? and prenume=? and nr_telefon=? and email=? and id_cabinet=?"))
        {
            stmt.setString(1,nume);
            stmt.setString(2,prenume);
            stmt.setString(3,nr_telefon);
            stmt.setString(4,email);
            stmt.setInt(5,id_cabinet);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");

                return id;
            } else {
                return 0;
            }
        }
    }

    public static Doctor findByCabinetId(int id_cabinet) throws SQLException {
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, nume, prenume, nr_telefon, email, image from doctori where id_cabinet='" + id_cabinet + "'")) {
            if (rs.next()) {
                int id = rs.getInt("id");
                String nume = rs.getString("nume");
                String prenume = rs.getString("prenume");
                String nr_telefon = rs.getString("nr_telefon");
                String email = rs.getString("email");
                String image = rs.getString("image");

                var specializare = new SpecializareDAO();
                var specializari = specializare.findSpecializariByDoctorId(id);

                return new Doctor(id,nume, prenume, nr_telefon, email,image,specializari,id_cabinet);
            } else {
                return null;
            }
        }
    }

    public static int findByEmail(String email) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement("select id from doctori where email=(?)");
        ) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public String findEmailById(Integer id_doctor) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement("select email from doctori where id=(?)");
        ) {
            pstmt.setInt(1, id_doctor);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getString(1) : null;
        }
    }

    public List<Doctor> findBySpecialisation(int specializare) throws SQLException {
        return this.findAll().stream().filter((d) -> d.getSpecializari().stream().anyMatch((spec) -> spec.getId() == specializare)).collect(Collectors.toList());
    }
}
