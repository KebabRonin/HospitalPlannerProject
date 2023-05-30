package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecializareDAO {
    public void create(String denumire) throws SQLException {
        try (Connection con = Database.getConnection();
        PreparedStatement pstmt = con.prepareStatement(
                "insert into specializari (denumire) values (?)")) {
            pstmt.setString(1,denumire);

            pstmt.executeUpdate();
        }
    }

    public List<Specializare> findSpecializariByDoctorId(int id_doctor) throws SQLException{
        List<Specializare> specializari = new ArrayList<>();
        try (Connection con = Database.getConnection();
        Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id_specializare from doctori_specializari where id_doctor='" + id_doctor + "'")) {
            while (rs.next()) {
                int id_specializare = rs.getInt("id_specializare");

                try(Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(
                        "select denumire from specializari where id='" + id_specializare + "'")){
                    while(resultSet.next()){
                        String denumire = resultSet.getString("denumire");

                        specializari.add(new Specializare(id_specializare,denumire));
                    }
                }

            }
            return specializari;
        }
    }

    public static Specializare findById(int id_specializare) throws SQLException{
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select denumire from specializari where id='" + id_specializare + "'")) {
            while (rs.next()) {
                String denumire = rs.getString("denumire");
                return new Specializare(id_specializare,denumire);
            }
            return null;
        }
    }

    public List<Specializare> getAllSpecializari() throws SQLException {
        List<Specializare> specializari = new ArrayList<>();
        try (Connection con = Database.getConnection();
        Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id,denumire from specializari")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String denumire = rs.getString("denumire");
                specializari.add(new Specializare(id, denumire));
            }
            return specializari;
        }
    }

//    public Specializare findById(int id) throws SQLException {
//        List<Specializare> specializari = new ArrayList<>();
//        try (Connection con = Database.getConnection();
//             PreparedStatement pstmt = con.prepareStatement("select id,denumire from specializari where id = (?)");
//             ) {
//            pstmt.setInt(1,id);
//            ResultSet rs = pstmt.executeQuery();
//            return rs.next() ? new Specializare(rs.getInt(1), rs.getString(2)) : null;
//        }
//    }
}
