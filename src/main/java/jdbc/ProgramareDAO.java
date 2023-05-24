package jdbc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramareDAO {
    public void create(int id, int id_pacient, int id_doctor, int id_cabinet, Date data_programare) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "insert into programari (id, id_pacient, id_doctor, id_cabinet, data_programare) values (?,?,?,?,?)")) {
            pstmt.setInt(1,this.idMax() + 1);
            pstmt.setInt(2,id_pacient);
            pstmt.setInt(3,id_doctor);
            pstmt.setInt(4,id_cabinet);
            pstmt.setDate(5,data_programare);

            pstmt.executeUpdate();
        }
    }
    public List<Programare> findAllOfPatientId(int id_pacient) throws SQLException {
        List<Programare> rez = new ArrayList<>();
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, id_pacient, id_doctor, id_cabinet, data_programare from programari where id_pacient='" + id_pacient + "'")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int id_doctor = rs.getInt("id_doctor");
                int id_cabinet = rs.getInt("id_cabinet");
                Date data_programare = rs.getDate("data_programare");
                rez.add(new Programare(id,id_pacient, id_doctor, id_cabinet, data_programare));
            }
            return rez;
        }
    }

    public int idMax() throws SQLException {
        Connection con = Database.getConnection();
        Statement stmt = con.createStatement();

        int var4;
        try {
            ResultSet rs = stmt.executeQuery("select max(id) from programari");

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
