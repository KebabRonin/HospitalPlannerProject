package jdbc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacientDAO{
    public void create(String prenume, String nume, String data_nastere, String adresa, String nr_telefon, String email, String parola) throws SQLException {
        try (Connection con = Database.getConnection();
        PreparedStatement pstmt = con.prepareStatement(
                "insert into pacienti (prenume,nume,data_nastere,adresa,nr_telefon,email,parola) values (?,?,?,?,?,?,?)")) {
            pstmt.setString(1, prenume);
            pstmt.setString(2, nume);
            pstmt.setString(3, data_nastere);
            pstmt.setString(4, adresa);
            pstmt.setString(5, nr_telefon);
            pstmt.setString(6, email);
            pstmt.setString(7, parola);

            pstmt.executeUpdate();
        }
    }

    public static void delete(int userId) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "DELETE FROM pacienti WHERE id = ?")) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public static void delete() throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "DELETE FROM pacienti")) {
            pstmt.executeUpdate();
        }
    }

    public int findByName(String name) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "select id from pacienti where name=(?)")) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : null;
        }
    }

    public static Pacient findById(int id) throws SQLException {
        try (Connection con = Database.getConnection();
        Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, nume, prenume, data_nastere, adresa, nr_telefon, email, parola from pacienti where id='" + id + "'")) {
            if (rs.next()) {
                String nume = rs.getString("nume");
                String prenume = rs.getString("prenume");
                String data_nastere = rs.getString("data_nastere");
                String adresa = rs.getString("adresa");
                String nr_telefon = rs.getString("nr_telefon");
                String email = rs.getString("email");
                String parola = rs.getString("parola");
                return new Pacient(id,nume, prenume, data_nastere, adresa, nr_telefon, email, parola );
            } else {
                return null;
            }
        }
    }

    public int findByEmail(String email) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement("select id from pacienti where email=(?)");
                     ) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public String findPasswordById(int id) throws SQLException {
        try (Connection con = Database.getConnection();
        Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select parola from pacienti where id='" + id + "'")) {
            return rs.next() ? rs.getString(1) : null;
        }
    }

    public static List<Pacient> findAll() throws SQLException{
        List<Pacient> rez = new ArrayList<>();
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, nume, prenume, data_nastere, adresa, nr_telefon, email, parola from pacienti order by id")) {
            while(rs.next()) {
                int id = rs.getInt("id");
                String nume = rs.getString("nume");
                String prenume = rs.getString("prenume");
                String data_nastere = rs.getString("data_nastere");
                String adresa = rs.getString("adresa");
                String nr_telefon = rs.getString("nr_telefon");
                String email = rs.getString("email");
                String parola = rs.getString("parola");

                rez.add(new Pacient(id,nume, prenume, data_nastere, adresa, nr_telefon, email, parola));
            }
        }
        return rez;
    }
}
