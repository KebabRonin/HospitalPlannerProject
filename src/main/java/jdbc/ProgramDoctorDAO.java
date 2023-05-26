package jdbc;

import java.sql.*;

public class ProgramDoctorDAO {
    public void create(Integer id_doctor, Integer id_cabinet, String program) throws SQLException {
        try (Connection con = Database.getConnection();
        PreparedStatement pstmt = con.prepareStatement(
                "insert into program_doctori (id_doctor, id_cabinet, program) values (?,?,?)")) {
            pstmt.setInt(1,id_doctor);
            pstmt.setInt(2,id_cabinet);
            pstmt.setString(3,program);

            pstmt.executeUpdate();
        }
    }
}
