package server;

import jdbc.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.MediaType;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

@Controller
public class WebController {
    boolean connected = false;
    int userId;
    private static String filesPath = "src/main/java/interfata/";
    @GetMapping("/")
    @ResponseBody
    public byte[] index() {
        return WebController.load_file(filesPath + "landing.html");
    }

    @GetMapping("/{*file_path}")
    @ResponseBody
    public byte[] get(@PathVariable(value="file_path") String filePath) {
        System.out.println(filePath);
        return WebController.load_file(filesPath + filePath);
    }

    @GetMapping("/resources/{*file_path}")
    @ResponseBody
    public byte[] get_resource(@PathVariable(value="file_path") String filePath) {
        System.out.println(filePath);
        return WebController.load_file(filesPath + filePath);
    }

    public static byte[] load_file(String filePath) {
        Path file = Paths.get(filePath);
        try {
            return Files.readAllBytes(file);
        } catch(IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
            //return null;
        }
    }

    @GetMapping(value = "/user-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Pacient getUserInfo() {
        try {
            var pacient = new PacientDAO();
            return pacient.findById(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve user information.");
        }
    }

    @GetMapping(value = "/api/doctors", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String get_doctors() {
        try {
            var doctori = new DoctorDAO();
            return doctori.findAll().toString();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve doctor information.");
        }
    }

    @GetMapping(value = "/your_appointments", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Programare> getUserAppointments(@CookieValue("userId") int userId) {
        try {
            return new ProgramareDAO().findAllOfPatientId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve doctor information.");
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam("email") String email_login,
                         @RequestParam("password") String password) {
        try {
            var pacient = new PacientDAO();
            int id = pacient.findByEmail(email_login);
            if(id == 0){
                return "Email doesn't exist.";
            }
            if(!password.equals(pacient.findPasswordById(id))){
                System.out.println(password + " != " + pacient.findPasswordById(id));
                return "Password incorrect.";
            }
            connected = true;
            userId = id;
            return "Log in successful!";
        }  catch (SQLException e) {
            e.printStackTrace();
            return "Failed to log in.";
        }
    }

    @PostMapping("/signup")
    @ResponseBody
    public String signUp(@RequestParam("firstName") String firstName,
                         @RequestParam("lastName") String lastName,
                         @RequestParam("date") String date,
                         @RequestParam("address") String address,
                         @RequestParam("phone") String phone,
                         @RequestParam("email") String email,
                         @RequestParam("password") String password) {
        try {
            var pacient = new PacientDAO();
            int id = pacient.findByEmail(email);
            if(id != 0){
                return "Failed to sign up.";
            }
            pacient.create(firstName, lastName,date,address,phone,email,password);
            connected = true;
            userId = pacient.findByEmail(email);
            return "Sign up successful!";
        }  catch (SQLException e) {
            e.printStackTrace();
            return "Failed to sign up.";
        }
    }

}