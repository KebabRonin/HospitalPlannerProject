package jdbc;
import java.sql.*;

public class PacientDAO{
    public void create(String nume, String prenume, String data_nastere, String adresa, String nr_telefon, String email, String parola) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "insert into pacienti (id,nume,prenume,data_nastere,adresa,nr_telefon,email,parola) values (?,?,?,?,?,?,?,?)")) {
            pstmt.setInt(1,this.idMax() + 1);
            pstmt.setString(2, nume);
            pstmt.setString(3, prenume);
            pstmt.setString(4, data_nastere);
            pstmt.setString(5, adresa);
            pstmt.setString(6, nr_telefon);
            pstmt.setString(7, email);
            pstmt.setString(8, parola);

            pstmt.executeUpdate();
        }
    }
    public int findByName(String name) throws SQLException {
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id from pacienti where name='" + name + "'")) {
            return rs.next() ? rs.getInt(1) : null;
        }
    }
    public Pacient findById(int id) throws SQLException {
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
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
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id from pacienti where email='" + email + "'")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public String findPasswordById(int id) throws SQLException {
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select parola from pacienti where id='" + id + "'")) {
            return rs.next() ? rs.getString(1) : null;
        }
    }

    public int idMax() throws SQLException {
        Connection con = Database.getConnection();
        Statement stmt = con.createStatement();

        int var4;
        try {
            ResultSet rs = stmt.executeQuery("select max(id) from pacienti");

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
