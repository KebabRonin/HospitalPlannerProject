package jdbc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramareDAO {
    public void create(int id_pacient, int id_doctor, Date data_programare, Time ora_programare) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "insert into programari (id_pacient, id_doctor, data_programare, ora_programare) values (?,?,?,?)")) {
            pstmt.setInt(1,id_pacient);
            pstmt.setInt(2,id_doctor);
            pstmt.setDate(3,data_programare);
            pstmt.setTime(4,ora_programare);

            pstmt.executeUpdate();
        }
    }
    public List<Programare> findAllOfPatientId(int id_pacient) throws SQLException {
        List<Programare> rez = new ArrayList<>();
        try (Connection con = Database.getConnection();
        Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, id_doctor, data_programare, ora_programare from programari where id_pacient='" + id_pacient + "' order by data_programare asc, ora_programare asc")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int id_doctor = rs.getInt("id_doctor");
                Date data_programare = rs.getDate("data_programare");
                String ora_programare = rs.getString("ora_programare");
                rez.add(new Programare(id,id_pacient, id_doctor, data_programare,ora_programare));
            }
            return rez;
        }
    }

    public static List<Programare> findAllOfDoctorId(int id_doctor) throws SQLException {
        List<Programare> rez = new ArrayList<>();
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, id_pacient, data_programare, ora_programare from programari where id_doctor='" + id_doctor + "' order by data_programare asc, ora_programare asc")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int id_pacient = rs.getInt("id_pacient");
                Date data_programare = rs.getDate("data_programare");
                String ora_programare = rs.getString("ora_programare");
                rez.add(new Programare(id,id_doctor, id_pacient, data_programare, ora_programare));
            }
            return rez;
        }
    }

    public static List<Programare> findAll() throws SQLException {
        List<Programare> rez = new ArrayList<>();
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, id_doctor, id_pacient, data_programare, ora_programare from programari order by data_programare asc, ora_programare asc")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int id_doctor = rs.getInt("id_doctor");
                int id_pacient = rs.getInt("id_pacient");
                Date data_programare = rs.getDate("data_programare");
                String ora_programare = rs.getString("ora_programare");
                rez.add(new Programare(id,id_doctor, id_pacient, data_programare, ora_programare));
            }
            return rez;
        }
    }
}
