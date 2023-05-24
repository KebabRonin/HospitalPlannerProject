package server;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jdbc.Cabinet;
import jdbc.CabinetDAO;
import jdbc.PacientDAO;
import jdbc.Pacient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.MediaType;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebController {
    String administrator_username = "admin";
    String administrator_password = "admin";
    private static Map<Integer, Pacient> connectedUsers = new ConcurrentHashMap<>();
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

    @GetMapping(value = "/cabinete-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Cabinet> getCabinetsInfo() throws SQLException {
        var cabinet = new CabinetDAO();
        return cabinet.getAllCabinets();
    }

    @GetMapping(value = "/user-info/{uid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Pacient getUserInfo(@PathVariable("uid") String uid) {
        int userId = Integer.parseInt(uid);
        return connectedUsers.get(userId);
    }

    @PostMapping("/add-cabinet")
    @ResponseBody
    public String addCabinet(@RequestParam("cabinet-name") String cabinetName,
                             @RequestParam("floor") Integer floor,
                             @RequestParam("cabinet-picture") MultipartFile cabinetPicture) {
        try {
            var cabinet = new CabinetDAO();
            if (cabinet.findByName(cabinetName) != 0) {
                return "Cabinet already exists.";
            }
            String folderPath = "C:\\Users\\Alex\\Documents\\GitHub\\HospitalPlannerProject\\src\\main\\java\\interfata\\cabinet_pictures\\";
            String fileName = cabinetPicture.getOriginalFilename();
            String filePath = folderPath + fileName;

            cabinetPicture.transferTo(new File(filePath));

            cabinet.create(cabinetName, filePath, floor);
            return "Cabinet added successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload the file.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to add the cabinet.";
        }
    }



    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam("email") String email_login,
                         @RequestParam("password") String password, HttpServletResponse response) {
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
            connectedUsers.put(id, pacient.findById(id));
            userId = id;
            Cookie cookie = new Cookie("userId", Integer.toString(id));
            response.addCookie(cookie);
            return "Log in successful!";
        }  catch (SQLException e) {
            e.printStackTrace();
            return "Failed to log in.";
        }
    }

    @PostMapping("/login-administrator")
    @ResponseBody
    public String loginAdministrator(@RequestParam("email") String email_administrator,
                        @RequestParam("password") String password) {
//        if(connectedUsers.get(0) != null){
//            return "Admin already connected.";
//        }
        if(!email_administrator.equals(administrator_username)){
            return "Incorrect.";
        }
        if(!password.equals(administrator_password)){
            return "Incorrect.";
        }
        return "Log in successful!";
    }

    @PostMapping("/signup")
    @ResponseBody
    public String signUp(@RequestParam("firstName") String firstName,
                         @RequestParam("lastName") String lastName,
                         @RequestParam("date") String date,
                         @RequestParam("address") String address,
                         @RequestParam("phone") String phone,
                         @RequestParam("email") String email,
                         @RequestParam("password") String password, HttpServletResponse response) {
        try {
            var pacient = new PacientDAO();
            int id = pacient.findByEmail(email);
            if(id != 0){
                return "Failed to sign up.";
            }
            pacient.create(firstName, lastName,date,address,phone,email,password);
            userId = pacient.findByEmail(email);
            connectedUsers.put(userId, pacient.findById(userId));
            Cookie cookie = new Cookie("userId", Integer.toString(userId));
            response.addCookie(cookie);
            return "Sign up successful!";
        }  catch (SQLException e) {
            e.printStackTrace();
            return "Failed to sign up.";
        }
    }

}