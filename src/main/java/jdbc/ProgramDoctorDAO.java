package jdbc;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ProgramDoctorDAO {
    public static void create(Integer id_doctor, Date zi, Time ora_inceput, Time ora_final) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "insert into program_doctori (id_doctor, zi, timp_inceput, timp_final) values (?,?,?,?)")) {
            pstmt.setInt(1,id_doctor);
            pstmt.setDate(2,zi);
            pstmt.setTime(3,ora_inceput);
            pstmt.setTime(4,ora_final);

            pstmt.executeUpdate();
        }
    }

    public static void update(Integer id, Integer id_doctor, Date zi, Time ora_inceput, Time ora_final) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "UPDATE program_doctori SET id_doctor=?, zi=?, timp_inceput=?, timp_final=? WHERE id=?")) {
            pstmt.setInt(1, id_doctor);
            pstmt.setDate(2, zi);
            pstmt.setTime(3, ora_inceput);
            pstmt.setTime(4, ora_final);
            pstmt.setInt(5, id);

            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void delete(Integer id) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "DELETE from program_doctori WHERE id=?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void delete(Date zi) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "DELETE from program_doctori WHERE zi=?")) {
            pstmt.setDate(1, zi);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void delete(Date zi, int doctorId) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "DELETE from program_doctori WHERE zi=? and id_doctor=?")) {
            pstmt.setDate(1, zi);
            pstmt.setInt(2, doctorId);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void delete() throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "DELETE from program_doctori")) {
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static List<Program> getOnDay(Date date) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "select id, id_doctor, zi, timp_inceput, timp_final from program_doctori where zi=(?)")) {
            pstmt.setDate(1,date);
            ResultSet rs =  pstmt.executeQuery();
            return getResultList(rs);
        }
    }

    public static Program findById(Integer id) throws SQLException {
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id_doctor, zi, timp_inceput, timp_final from program_doctori where id='" + id + "'")) {
            if (rs.next()) {
                Integer id_doctor = rs.getInt("id_doctor");
                Date zi = rs.getDate("zi");
                Time timp_inceput = rs.getTime("timp_inceput");
                Time timp_final = rs.getTime("timp_final");

                return new Program(id,id_doctor,zi,timp_inceput,timp_final);
            } else {
                return null;
            }
        }
    }

    public static List<Program> getOfDoctor(int id_doctor) throws SQLException {
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, id_doctor, zi, timp_inceput, timp_final from program_doctori where id_doctor=" + id_doctor + " order by zi asc, timp_inceput asc")) {
            return getResultList(rs);
        }
    }

    public static List<Program> getAllShifts() throws SQLException{
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, id_doctor, zi, timp_inceput, timp_final from program_doctori order by zi asc, timp_inceput asc")) {
            return getResultList(rs);
        }
    }

    public static List<Program> getOfDoctorOnDay(int id_doctor, Date date) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "select id, id_doctor, zi, timp_inceput, timp_final from program_doctori where zi=(?) AND id_doctor=(?)")) {
            pstmt.setDate(1,date);
            pstmt.setInt(2,id_doctor);
            ResultSet rs =  pstmt.executeQuery();
            return getResultList(rs);
        }
    }

    private static List<Program> getResultList(ResultSet rs) throws SQLException {
        List<Program> intervale = new LinkedList<>();
        while(rs.next()) {
            intervale.add(new Program(rs.getInt("id"), rs.getInt("id_doctor"), rs.getDate("zi"),
                    rs.getTime("timp_inceput"), rs.getTime("timp_final")));
            System.out.println();
        }
        return intervale;
    }

}
