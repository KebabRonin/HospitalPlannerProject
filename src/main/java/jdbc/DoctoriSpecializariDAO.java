package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoctoriSpecializariDAO {
    public static void create(Integer id_doctor, Integer id_specializare) throws SQLException {

        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                "insert into doctori_specializari (id_doctor, id_specializare) values (?,?)")) {
            pstmt.setInt(1,id_doctor);
            pstmt.setInt(2,id_specializare);

            pstmt.executeUpdate();
        }
    }

    public static int findIdSpecializareByIdDoctor(Integer id_doctor) throws SQLException{
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "select id_specializare from doctori_specializari where id_doctor=?")) {
            pstmt.setInt(1,id_doctor);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_specializare");
            } else {
                return 0;
            }

        }
    }

    public static void update(Integer id_doctor, Integer id_specializare) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "UPDATE doctori_specializari SET id_specializare=? WHERE id_doctor=?")) {
            pstmt.setInt(1, id_specializare);
            pstmt.setInt(2, id_doctor);

            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void delete(int id_doctor) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "DELETE FROM doctori_specializari WHERE id_doctor = ?")) {
            pstmt.setInt(1, id_doctor);
            pstmt.executeUpdate();
        }
    }

}
