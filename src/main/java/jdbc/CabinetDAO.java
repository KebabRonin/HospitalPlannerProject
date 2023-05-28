package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CabinetDAO {
    public static void create(String denumire, Integer etaj, String image) throws SQLException {
        try (Connection con = Database.getConnection();
        PreparedStatement pstmt = con.prepareStatement(
                "insert into cabinete (denumire, etaj, image) values (?,?,?)")) {
            pstmt.setString(1, denumire);
            pstmt.setInt(2, etaj);
            pstmt.setString(3, image);
            pstmt.executeUpdate();
        }
    }

    public static void create(String denumire, Integer etaj) throws SQLException {
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "insert into cabinete (denumire, etaj) values (?,?)")) {
            pstmt.setString(1, denumire);
            pstmt.setInt(2, etaj);
            pstmt.executeUpdate();
        }
    }

    public static void update(int id, String cabinetName, int floor, String filePath) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "UPDATE cabinete SET denumire=?, etaj=?, image=? WHERE id=?")) {
            pstmt.setString(1, cabinetName);
            pstmt.setInt(2, floor);
            pstmt.setString(3, filePath);
            pstmt.setInt(4, id);

            pstmt.executeUpdate();
        }
    }

    public static void update(int id, String cabinetName, int floor) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "UPDATE cabinete SET denumire=?, etaj=? WHERE id=?")) {
            pstmt.setString(1, cabinetName);
            pstmt.setInt(2, floor);
            pstmt.setInt(3, id);

            pstmt.executeUpdate();
        }
    }

    public static void delete(int id_cabinet) throws SQLException {
        Connection con = Database.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(
                "DELETE FROM cabinete WHERE id = ?")) {
            pstmt.setInt(1, id_cabinet);
            pstmt.executeUpdate();
        }
    }

    public List<String> findCabineteByDoctorId(int id_doctor) throws SQLException{
        List<String> cabinete = new ArrayList<>();
        try (Connection con = Database.getConnection();
        Statement stmt = con.createStatement();
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
        List<Cabinet> cabinetList = new ArrayList<>();
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, denumire, etaj, image FROM cabinete order by id")) {
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
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "select id from cabinete where denumire=(?)")) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    public static Cabinet findById(int id) throws SQLException {

        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement();
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
}
