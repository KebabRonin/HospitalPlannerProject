package server;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jdbc.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.MediaType;


import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @GetMapping(value = "/specializari-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Specializare> getSpecializariInfo() throws SQLException {
        var specializare = new SpecializareDAO();
        return specializare.getAllSpecializari();
    }

    @GetMapping(value = "/doctors-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Doctor> getDoctorsInfo() throws SQLException {
        var doctor = new DoctorDAO();
        var doctors = doctor.findAll();
        return doctors;
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
            String fileName = cabinetPicture.getOriginalFilename();
            String folderPath = "C:\\Users\\KebabWarrior\\Desktop\\HospitalPlannerProject\\src\\main\\java\\interfata\\cabinet_pictures\\";
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

    @PostMapping("/add-doctor")
    @ResponseBody
    public String addDoctor(@RequestParam("first-name") String firstName,
                            @RequestParam("last-name") String lastName,
                            @RequestParam("specialization") Integer specialization,
                            @RequestParam("cabinet") Integer cabinet,
                            @RequestParam("schedule") String schedule,
                            @RequestParam("phone") String phone,
                            @RequestParam("email") String email,
                            @RequestParam("image") MultipartFile image) {
        try {
            var doctor = new DoctorDAO();
            if (doctor.findByEmail(email) != 0) {
                return "Doctor already exists.";
            }
            String fileName = image.getOriginalFilename();
            String folderPath = "C:\\Users\\Alex\\Documents\\GitHub\\HospitalPlannerProject\\src\\main\\java\\interfata\\doctor_pictures\\";
            String filePath = folderPath + fileName;

            image.transferTo(new File(filePath));

            doctor.create(firstName,lastName,phone,email,filePath);
            int id_doctor = doctor.findByEmail(email);

            var doctorSpecializare = new DoctoriSpecializariDAO();
            doctorSpecializare.create(id_doctor, specialization);

            var programDoctor = new ProgramDoctorDAO();
            programDoctor.create(id_doctor,cabinet,schedule);

            return "Doctor added successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload the file.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to add the doctor.";
        }
    }


    @GetMapping(value = "/doctors", produces = MediaType.APPLICATION_JSON_VALUE)
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