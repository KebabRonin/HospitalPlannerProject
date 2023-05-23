package jdbc;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        var pacient = new PacientDAO();
        var pacientul = pacient.findById(7);
        System.out.println(pacientul.getNume() + " " + pacientul.getPrenume() + " " + pacientul.getEmail());
    }
}
