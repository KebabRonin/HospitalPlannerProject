package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CabinetDAO {
    public void create(String denumire, String image, Integer etaj) throws SQLException {
        try (Connection con = Database.getConnection();
        PreparedStatement pstmt = con.prepareStatement(
                "insert into cabinete (denumire, etaj, image) values (?,?,?)")) {
            pstmt.setString(1, denumire);
            pstmt.setInt(2, etaj);
            pstmt.setString(3, image);
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
        try (Connection con = Database.getConnection();
             PreparedStatement pstmt = con.prepareStatement(
                     "select id from cabinete where denumire=(?)")) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : null;
        }
    }
    public Cabinet findById(int id) throws SQLException {

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
