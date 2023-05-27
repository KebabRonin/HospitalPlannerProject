package jdbc;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ProgramDoctorDAO {
    public void create(Integer id_doctor/*, Integer id_cabinet*/, Date zi, Time ora_inceput, Time ora_final) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "insert into program_doctori (id_doctor, zi, ora_inceput, ora_final) values (?,?,?,?)")) {
            pstmt.setInt(1,id_doctor);
            pstmt.setDate(2,zi);
            pstmt.setTime(3,ora_inceput);
            pstmt.setTime(4,ora_final);

            pstmt.executeUpdate();
        }
    }

    public List<Program> getOnDay(Date date) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "select id, id_doctor, zi, ora_inceput, ora_final from program_doctori where zi=(?)")) {
            pstmt.setDate(1,date);
            ResultSet rs =  pstmt.executeQuery();
            return getResultList(rs);
        }
    }

    public List<Program> getOfDoctor(int id_doctor) throws SQLException {
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, id_doctor, zi, ora_inceput, ora_final from program_doctori where id_doctor=" + id_doctor)) {
            return getResultList(rs);
        }
    }


    public List<Program> getOfDoctorOnDay(int id_doctor, Date date) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "select id, id_doctor, zi, ora_inceput, ora_final from program_doctori where zi=(?) AND id_doctor=(?)")) {
            pstmt.setDate(1,date);
            pstmt.setInt(2,id_doctor);
            ResultSet rs =  pstmt.executeQuery();
            return getResultList(rs);
        }
    }
    private List<Program> getResultList(ResultSet rs) throws SQLException {
        List<Program> intervale = new LinkedList<>();
        while(rs.next()) {
            intervale.add(new Program(rs.getInt("id"), rs.getInt("id_doctor"), rs.getDate("zi"),
                    rs.getTime("ora_inceput"), rs.getTime("ora_final")));
            System.out.println();
        }
        return intervale;
    }

}
