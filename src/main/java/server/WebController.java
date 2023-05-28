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
import java.sql.Date;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @GetMapping(value = "/pacient-info/{id_pacient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Pacient getPacientInfo(@PathVariable("id_pacient") Integer id_pacient) throws SQLException {
        Pacient pacient = PacientDAO.findById(id_pacient);
        return pacient;
    }

    @GetMapping(value = "/cabinet-info/{id_cabinet}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Cabinet getCabinetInfo(@PathVariable("id_cabinet") Integer id_cabinet) throws SQLException {
        Cabinet cabinet = CabinetDAO.findById(id_cabinet);
        return cabinet;
    }

    @GetMapping(value = "/doctors-info/{id_doctor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Doctor getDoctorInfo(@PathVariable("id_doctor") Integer id_doctor) throws SQLException {
        return DoctorDAO.findById(id_doctor);
    }

    @GetMapping(value = "/doctor-shifts/{id_doctor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Program> getShiftsOfDoctor(@PathVariable("id_doctor") Integer id_doctor) throws SQLException {
        return ProgramDoctorDAO.getOfDoctor(id_doctor);
    }

    @GetMapping(value = "/doctor-appointments/{id_doctor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Programare> getDoctorAppointmentsInfo(@PathVariable("id_doctor") Integer id_doctor) throws SQLException {
        return ProgramareDAO.findAllOfDoctorId(id_doctor);
    }

    @GetMapping(value = "/cabinet-doctor/{id_cabinet}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Doctor getDoctorsByCabinet(@PathVariable("id_cabinet") Integer id_cabinet) throws SQLException {
        return DoctorDAO.findByCabinetId(id_cabinet);
    }

    @GetMapping(value = "/all-appointments", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Programare> getAllAppointments() throws SQLException {
        return ProgramareDAO.findAll();
    }

    @GetMapping(value = "/all-shifts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Program> getAllShifts() throws SQLException {
        return ProgramDoctorDAO.getAllShifts();
    }

    @PostMapping("/add-shift")
    @ResponseBody
    public String addShift(@RequestParam("id_doctor") Integer id_doctor,
                             @RequestParam("dates") String datesString,
                             @RequestParam("start-hour") String start_hour,
                             @RequestParam("end-hour") String end_hour) {
        try {
            List<Date> dates = new LinkedList<>();
            SimpleDateFormat d = new SimpleDateFormat("dd-M-yyyy");
            String[] sub = datesString.split(",");
            for (String str : sub) {
                dates.add(new java.sql.Date(d.parse(str).getTime()));
            }

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            java.util.Date parsedDate = format.parse(start_hour);
            Time startHour = new Time(parsedDate.getTime());
            parsedDate = format.parse(end_hour);
            Time endHour = new Time(parsedDate.getTime());

            for(Date date : dates){
                ProgramDoctorDAO.create(id_doctor,date,startHour,endHour);
            }
            if(dates.size()>1){
                return "Shifts added successfully!";
            }
            return "Shift added successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to add the shift.";
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/update-shift/{id_shift}")
    @ResponseBody
    public String updateShift(@PathVariable("id_shift") Integer id_shift,
                              @RequestParam("id_doctor") Integer id_doctor,
                              @RequestParam("dates") String dateString,
                              @RequestParam("start-hour") String start_hour,
                              @RequestParam("end-hour") String end_hour) {
        try {
            SimpleDateFormat d = new SimpleDateFormat("dd-M-yyyy");
            Date date = new java.sql.Date(d.parse(dateString).getTime());

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            java.util.Date parsedDate = format.parse(start_hour);
            Time startHour = new Time(parsedDate.getTime());
            parsedDate = format.parse(end_hour);
            Time endHour = new Time(parsedDate.getTime());

            ProgramDoctorDAO.update(id_shift,id_doctor,date,startHour,endHour);
            return "Shift edited successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to edit the shift.";
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping(value = "/delete-shift/{id_shift}")
    @ResponseBody
    public String deleteShift(@PathVariable("id_shift") Integer id_shift) {
        try {
            ProgramDoctorDAO.delete(id_shift);
            return "Shift deleted successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete the shift.";
        }
    }

    @GetMapping(value = "/shift-info/{id_shift}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Program getShiftInfo(@PathVariable("id_shift") Integer id_shift) throws SQLException {
        return ProgramDoctorDAO.findById(id_shift);
    }

    @PostMapping("/add-cabinet")
    @ResponseBody
    public String addCabinet(@RequestParam("cabinet-name") String cabinetName,
                             @RequestParam("floor") Integer floor,
                             @RequestParam("cabinet-picture") MultipartFile cabinetPicture) {
        try {
            String fileName = cabinetPicture.getOriginalFilename();
            if(!Objects.equals(fileName, "")){
                String folderPath = "C:\\Users\\Alex\\Documents\\GitHub\\HospitalPlannerProject\\src\\main\\java\\interfata\\doctor_pictures\\";
                String filePath = folderPath + fileName;
                cabinetPicture.transferTo(new File(filePath));

                CabinetDAO.create(cabinetName,floor,filePath);
            }
            else{
                CabinetDAO.create(cabinetName,floor);
            }

            return "Cabinet added successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload the file.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to add the cabinet.";
        }
    }

    @PostMapping("/update-cabinet")
    @ResponseBody
    public String updateCabinet(@RequestParam("id") Integer id,
                                @RequestParam("cabinet-name") String cabinetName,
                                @RequestParam("floor") Integer floor,
                                @RequestParam("cabinet-picture") MultipartFile cabinetPicture) {
        try {
            String fileName = cabinetPicture.getOriginalFilename();
            if(!Objects.equals(fileName, "")){
                String folderPath = "C:\\Users\\Alex\\Documents\\GitHub\\HospitalPlannerProject\\src\\main\\java\\interfata\\doctor_pictures\\";
                String filePath = folderPath + fileName;
                cabinetPicture.transferTo(new File(filePath));

                CabinetDAO.update(id,cabinetName,floor,filePath);
            }
            else{
                CabinetDAO.update(id,cabinetName,floor);
            }

            return "Cabinet edited successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload the file.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to edit the cabinet.";
        }
    }

    @PostMapping("/add-doctor")
    @ResponseBody
    public String addDoctor(@RequestParam("first-name") String firstName,
                            @RequestParam("last-name") String lastName,
                            @RequestParam("specialization") Integer specialization,
                            @RequestParam("cabinet") Integer cabinet,
                            @RequestParam("phone") String phone,
                            @RequestParam("email") String email,
                            @RequestParam("image") MultipartFile image) {
        try {
            System.out.println("SPECIALIZARE: " + specialization + " CABINET: " + cabinet);
            String fileName = image.getOriginalFilename();
            if(!Objects.equals(fileName, "")){
                String folderPath = "C:\\Users\\Alex\\Documents\\GitHub\\HospitalPlannerProject\\src\\main\\java\\interfata\\doctor_pictures\\";
                String filePath = folderPath + fileName;
                image.transferTo(new File(filePath));

                var doctor = new DoctorDAO();
                doctor.create(firstName,lastName,phone,email,filePath,cabinet);
            }
            else{
                var doctor = new DoctorDAO();
                doctor.create(firstName,lastName,phone,email,cabinet);
            }
            int id_doctor = DoctorDAO.findId(firstName,lastName,phone,email,cabinet);

            var doctorSpecializare = new DoctoriSpecializariDAO();
            doctorSpecializare.create(id_doctor, specialization);

            return "Doctor added successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload the file.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to add the doctor.";
        }
    }

    @PostMapping("/update-doctor")
    @ResponseBody
    public String updateDoctor(@RequestParam("id") Integer id,
                               @RequestParam("first-name") String firstName,
                               @RequestParam("last-name") String lastName,
                               @RequestParam("specialization") Integer specialization,
                               @RequestParam("cabinet") Integer cabinet,
                               @RequestParam("phone") String phone,
                               @RequestParam("email") String email,
                               @RequestParam("image") MultipartFile image) {
        try {
            System.out.println("SPECIALIZARE: " + specialization + "CABINET: " + cabinet);
            String fileName = image.getOriginalFilename();
            if(!Objects.equals(fileName, "")){
                String folderPath = "C:\\Users\\Alex\\Documents\\GitHub\\HospitalPlannerProject\\src\\main\\java\\interfata\\doctor_pictures\\";
                String filePath = folderPath + fileName;
                image.transferTo(new File(filePath));

                var doctor = new DoctorDAO();
                doctor.update(id,firstName,lastName,phone,email,filePath,cabinet);
            }
            else{
                var doctor = new DoctorDAO();
                doctor.update(id,firstName,lastName,phone,email,cabinet);
            }

            var doctorSpecializare = new DoctoriSpecializariDAO();
            doctorSpecializare.update(id, specialization);

            return "Doctor edited successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed upload the file.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed edit the doctor.";
        }
    }

    @DeleteMapping("/delete-doctor/{id}")
    @ResponseBody
    public String deleteDoctor(@PathVariable("id") Integer id_doctor) {
        try {
            var doctor = new DoctorDAO();
            doctor.delete(id_doctor);

            var doctorSpecializare = new DoctoriSpecializariDAO();
            doctorSpecializare.delete(id_doctor);

            return "Doctor deleted successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete the doctor.";
        }
    }

    @DeleteMapping("/delete-cabinet/{id_cabinet}")
    @ResponseBody
    public String deleteCabinet(@PathVariable("id_cabinet") Integer id_cabinet) {
        try {
            CabinetDAO.delete(id_cabinet);
            DoctorDAO.update(id_cabinet);

            return "Cabinet deleted successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete the cabinet.";
        }
    }

    @GetMapping(value="/program-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getProgram(@RequestParam(value = "dates", required = false)String datesLStr, @RequestParam(value = "doctors", required = false) String docsLStr) throws SQLException, java.text.ParseException {
        List<Date> dates = new LinkedList<>();
        SimpleDateFormat d = new SimpleDateFormat("dd-M-yyyy");
        String[] sub = datesLStr.split(",");
        for (String str : sub) {
            dates.add(new java.sql.Date(d.parse(str).getTime()));
        }

        int[] doctor_ids;
        sub = docsLStr.split(",");
        doctor_ids = Arrays.stream(sub).mapToInt(Integer::parseInt).toArray();

        System.out.println(dates.size());
        dates.stream().forEach((date) -> System.out.println(date));

        System.out.println(doctor_ids.length);
        Arrays.stream(doctor_ids).forEach((id) -> System.out.println(id));
        return "";
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

    @PostMapping(value = "/appointments", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void makeAppointment(@CookieValue("userId") int userId, @RequestBody RequestProgramare request) throws ParseException, SQLException {
        System.out.println(request);
        ProgramareDAO prog = new ProgramareDAO();

        if(request.getHour() == null) {
            request.setHour("01:01");
        }

        prog.create(userId, request.getDoctor(), request.getDates().get(0), request.getHour());
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