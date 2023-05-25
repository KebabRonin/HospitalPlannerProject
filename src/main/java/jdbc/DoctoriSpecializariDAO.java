package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DoctoriSpecializariDAO {
    public void create(Integer id_doctor, Integer id_specializare) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "insert into doctori_specializari (id_doctor, id_specializare) values (?,?)")) {
            pstmt.setInt(1,id_doctor);
            pstmt.setInt(2,id_specializare);

            pstmt.executeUpdate();
        }
    }
}
