package jdbc;

import java.sql.*;

public class ProgramDoctorDAO {
    public void create(Integer id_doctor, Integer id_cabinet, String program) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "insert into program_doctori (id, id_doctor, id_cabinet, program) values (?,?,?,?)")) {
            pstmt.setInt(1,idMax() + 1);
            pstmt.setInt(2,id_doctor);
            pstmt.setInt(3,id_cabinet);
            pstmt.setString(4,program);

            pstmt.executeUpdate();
        }
    }
    public int idMax() throws SQLException {
        Connection con = Database.getConnection();
        Statement stmt = con.createStatement();

        int var4;
        try {
            ResultSet rs = stmt.executeQuery("select max(id) from program_doctori");

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
