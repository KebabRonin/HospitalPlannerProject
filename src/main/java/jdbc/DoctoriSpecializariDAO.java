package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DoctoriSpecializariDAO {
    public void create(Integer id_doctor, Integer id_specializare) throws SQLException {

        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                "insert into doctori_specializari (id_doctor, id_specializare) values (?,?)")) {
            pstmt.setInt(1,id_doctor);
            pstmt.setInt(2,id_specializare);

            pstmt.executeUpdate();
        }
    }

    public void update(Integer id_doctor, Integer id_specializare) throws SQLException {
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

    public void delete(int id_doctor) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "DELETE FROM doctori_specializari WHERE id_doctor = ?")) {
            pstmt.setInt(1, id_doctor);
            pstmt.executeUpdate();
        }
    }

}
