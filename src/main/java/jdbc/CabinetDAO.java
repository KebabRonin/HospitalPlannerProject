package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CabinetDAO {
    public void create(String denumire, String image, Integer etaj) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "insert into cabinete (id, denumire, etaj, image) values (?,?,?,?)")) {
            pstmt.setInt(1,this.idMax() + 1);
            pstmt.setString(2, denumire);
            pstmt.setInt(3, etaj);
            pstmt.setString(4, image);
            pstmt.executeUpdate();
        }
    }

    public List<String> findCabineteByDoctorId(int id_doctor) throws SQLException{
        List<String> cabinete = new ArrayList<>();
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id_cabinet from program_doctori where id_doctor='" + id_doctor + "'")) {
            while (rs.next()) {
                int id_cabinet = rs.getInt("id_cabinet");

                try(Statement statement = con.createStatement();
                    ResultSet resultSet = statement.executeQuery(
                            "select denumire from cabinete where id='" + id_cabinet + "'")){
                    while(resultSet.next()){
                        String denumire = resultSet.getString("denumire");

                        cabinete.add(denumire);
                    }
                }

            }
            return cabinete;
        }
    }

    public List<Cabinet> getAllCabinets() throws SQLException {
        Connection con = Database.getConnection();
        List<Cabinet> cabinetList = new ArrayList<>();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, denumire, etaj, image FROM cabinete")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String denumire = rs.getString("denumire");
                int etaj = rs.getInt("etaj");
                String image = rs.getString("image");
                Cabinet cabinet = new Cabinet(id, denumire, etaj, image);
                cabinetList.add(cabinet);
            }
        }
        return cabinetList;
    }

    public int findByName(String name) throws SQLException {
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id from cabinete where denumire='" + name + "'")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    public Cabinet findById(int id) throws SQLException {
        Connection con = Database.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select id, denumire, etaj, image from cabinete where id='" + id + "'")) {
            if (rs.next()) {
                String denumire = rs.getString("denumire");
                Integer etaj = rs.getInt("etaj");
                String image = rs.getString("image");
                return new Cabinet(id,denumire,etaj,image);
            } else {
                return null;
            }
        }
    }

    public int idMax() throws SQLException {
        Connection con = Database.getConnection();
        Statement stmt = con.createStatement();

        int var4;
        try {
            ResultSet rs = stmt.executeQuery("select max(id) from cabinete");

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
