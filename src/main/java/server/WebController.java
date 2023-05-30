package server;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jdbc.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.sql.*;
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
    public byte[] index(@CookieValue(value="userId", required=false) Integer userId) {
        if(userId == null || connectedUsers.get(userId) == null) {
            return WebController.load_file(filesPath + "landing.html");
        }
        else {
            return WebController.load_file(filesPath + "admin.html");
        }
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

    @GetMapping("/admin")
    @ResponseBody
    public byte[] profile(@CookieValue(value="userId", required = false) Integer userId) {
        if(userId == null || connectedUsers.get(userId) == null) {
            return WebController.load_file(filesPath + "landing.html");
        }
        else {
            return WebController.load_file(filesPath + "admin.html");
        }
    }

    @GetMapping("/your_appointments")
    @ResponseBody
    public byte[] your_appointments(@CookieValue(value="userId", required = false) Integer userId) {
        if(userId == null || connectedUsers.get(userId) == null) {
            return WebController.load_file(filesPath + "landing.html");
        }
        else {
            return WebController.load_file(filesPath + "your_appointments.html");
        }
    }

    @GetMapping("/make_appointment")
    @ResponseBody
    public byte[] make_appointment(@CookieValue(value="userId", required = false) Integer userId) {
        if(userId == null || connectedUsers.get(userId) == null) {
            return WebController.load_file(filesPath + "landing.html");
        }
        else {
            return WebController.load_file(filesPath + "make_appointment.html");
        }
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

    @GetMapping(value = "/specializari-info/existing-doctors", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Specializare> getSpecializariInfoExisting() throws SQLException {
        var specializare = new SpecializareDAO();
        return specializare.getAllExistingSpecializari();
    }

    @GetMapping(value = "/specializari-info/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Specializare getSpecializareInfo(@PathVariable("id") int id) throws SQLException {
        var specializare = new SpecializareDAO();
        return specializare.findById(id);
    }

    @GetMapping(value = "/specializare-info/{id_doctor}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Specializare getSpecializareByDoctorId(@PathVariable("id_doctor") Integer id_doctor) throws SQLException {
        int id_specializare = DoctoriSpecializariDAO.findIdSpecializareByIdDoctor(id_doctor);
        return SpecializareDAO.findById(id_specializare);

    }

    @GetMapping(value = "/doctors-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Doctor> getDoctorsInfo(@RequestParam(value = "specialisation", required = false) Integer id_specialisation) throws SQLException {
        var doctor = new DoctorDAO();
        List<Doctor> doctors;
        if(id_specialisation != null) {
            doctors = doctor.findBySpecialisation(id_specialisation);
        }
        else {
            doctors = doctor.findAll();
        }
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

    @GetMapping(value = "/users-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Pacient> getUsersInfo() throws SQLException {
        return PacientDAO.findAll();
    }

    @DeleteMapping(value = "/delete-user/{userId}")
    @ResponseBody
    public String deleteUser(@PathVariable("userId") Integer userId) {
        try {
            PacientDAO.delete(userId);
            return "User deleted successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete the user.";
        }
    }

    @DeleteMapping(value = "/delete-shifts-date/{date}")
    @ResponseBody
    public String deleteShiftsOnDate(@PathVariable("date") String date) {
        try {
            SimpleDateFormat d = new SimpleDateFormat("dd-M-yyyy");
            ProgramDoctorDAO.delete(new java.sql.Date(d.parse(date).getTime()));
            return "Shifts deleted successfully!";
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return "Failed to delete the shifts.";
        }
    }

    @DeleteMapping(value = "/delete-shifts-date-doctor/{date}")
    @ResponseBody
    public String deleteShiftsOnDateForDoctor(@PathVariable("date") String date,
                                              @RequestParam("doctor") Integer doctorId) {
        try {
            SimpleDateFormat d = new SimpleDateFormat("yyyy-M-dd");
            ProgramDoctorDAO.delete(new java.sql.Date(d.parse(date).getTime()),doctorId);
            return "Shifts deleted successfully!";
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return "Failed to delete the shifts.";
        }
    }

    @DeleteMapping(value = "/delete-appointments-date/{date}")
    @ResponseBody
    public String deleteAppointmentsOnDate(@PathVariable("date") String date) {
        try {
            SimpleDateFormat d = new SimpleDateFormat("dd-M-yyyy");
            ProgramareDAO.delete(new java.sql.Date(d.parse(date).getTime()));
            return "Appointments deleted successfully!";
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return "Failed to delete appointments.";
        }
    }

    @DeleteMapping(value = "/delete-appointments-date-doctor/{date}")
    @ResponseBody
    public String deleteAppointmentsOnDateForDoctor(@PathVariable("date") String date,
                                                    @RequestParam("doctor") Integer doctorId) {
        try {
            SimpleDateFormat d = new SimpleDateFormat("dd-M-yyyy");
            ProgramareDAO.delete(new java.sql.Date(d.parse(date).getTime()),doctorId);
            return "Appointments deleted successfully!";
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return "Failed to delete the appointments.";
        }
    }

    @DeleteMapping(value = "/delete-all-users")
    @ResponseBody
    public String deleteAllUsers() {
        try {
            PacientDAO.delete();
            return "User deleted successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete the user.";
        }
    }

    @DeleteMapping(value = "/delete-all-doctors")
    @ResponseBody
    public String deleteAllDoctors() {
        try {
            DoctorDAO.delete();
            return "Doctor deleted successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete the doctor.";
        }
    }

    @DeleteMapping(value = "/delete-all-cabinete")
    @ResponseBody
    public String deleteAllCabinete() {
        try {
            CabinetDAO.delete();
            return "Cabinet deleted successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete the cabinet.";
        }
    }

    @DeleteMapping(value = "/delete-all-appointments")
    @ResponseBody
    public String deleteAllAppointments() {
        try {
            ProgramareDAO.delete();
            return "Appointment deleted successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete the appointment.";
        }
    }

    @DeleteMapping(value = "/delete-all-shifts")
    @ResponseBody
    public String deleteAllShifts() {
        try {
            ProgramDoctorDAO.delete();
            return "Shift deleted successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete the shift.";
        }
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

    @DeleteMapping(value = "/delete-all-data")
    @ResponseBody
    public String deleteAllData() {
        try (Connection connection = Database.getConnection()) {
            String procedureCall = "CALL delete_all_data()";
            try (CallableStatement statement = connection.prepareCall(procedureCall)) {
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete all data";
        }
        return "Successfully deleted all data";
    }

    @DeleteMapping(value = "/delete-appointment/{appointmentId}")
    @ResponseBody
    public String deleteAppointment(@PathVariable("appointmentId") Integer appointmentId) {
        try {
            ProgramareDAO.delete(appointmentId);
            return "Appointment deleted successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to delete the appointment.";
        }
    }

    @PostMapping(value = "/update-appointment/{appointmentId}")
    @ResponseBody
    public String updateAppointment(@PathVariable("appointmentId") Integer appointmentId,
                                    @RequestParam("id_pacient") Integer id_pacient,
                                      @RequestParam("doctor") Integer id_doctor,
                                      @RequestParam("dates") String dateString,
                                      @RequestParam("hour") String hour) {
        try {
            SimpleDateFormat d = new SimpleDateFormat("dd-M-yyyy");
            Date date = new java.sql.Date(d.parse(dateString).getTime());

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            java.util.Date parsedDate = format.parse(hour);
            Time ora_programare = new Time(parsedDate.getTime());
            ProgramareDAO.update(appointmentId,id_doctor,id_pacient,date,ora_programare);
            return "Appointment edited successfully!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to edit the appointment.";
        } catch (ParseException e) {
            throw new RuntimeException(e);
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
                Path p = Paths.get("./"+filesPath+"/cabinet_pictures/"+fileName);
                String filePath = p.toString();

                cabinetPicture.transferTo(p);

                CabinetDAO.create(cabinetName,floor,filePath);
            }
            else{
                CabinetDAO.create(cabinetName,floor);
            }

            //cabinetPicture.transferTo(new File(filePath));
            cabinetPicture.transferTo(Paths.get("./"+filesPath+"/cabinet_pictures/"+fileName));

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
                Path p = Paths.get("./"+filesPath+"/cabinet_pictures/"+fileName);
                String filePath = p.toString();
                cabinetPicture.transferTo(p);

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
    public List<Integer> getProgram(@RequestBody RequestProgramare request, @RequestParam("month") int month/*@RequestParam(value = "dates", required = false)String datesLStr, @RequestParam(value = "doctors", required = false) String docsLStr*/) throws Exception {
        System.out.println(request);
        List<Doctor> doctorList = null;
        DoctorDAO doctorDAO = new DoctorDAO();
        if (request.getDoctor() > 0) {
            Doctor d = doctorDAO.findById(request.getDoctor());
            if (d != null) {
                doctorList = new LinkedList<>();
                doctorList.add(d);
            }
            else {
                throw new Exception("Doctor does not exist");
            }
        }
        else if (request.getSpecializare() > 0) {
            doctorList = doctorDAO.findBySpecialisation(request.getSpecializare());
        }

        if(doctorList == null) {//any doctor
            try(Connection con = Database.getConnection();
                PreparedStatement pstmt = con.prepareStatement("select distinct to_char(zi,'DD')::integer from program_doctori where to_char(zi,'MM')::integer = (?)")) {
                pstmt.setInt(1, month);
                ResultSet rs = pstmt.executeQuery();
                List<Integer> available_days = new ArrayList<>(32);
                while(rs.next()) {
                    available_days.add(rs.getInt(1));
                }

                return available_days;
            }
        }
        else if(doctorList.size() > 0) {
            try(Connection con = Database.getConnection()) {
                StringBuilder sb = new StringBuilder("select distinct to_char(zi,'DD')::integer from program_doctori where to_char(zi,'MM')::integer = (?) and id_doctor in (");
                sb.append("?,".repeat(doctorList.size()));
                sb.deleteCharAt(sb.length()-1);
                sb.append(")");
                try (PreparedStatement pstmt = con.prepareStatement(sb.toString())) {
                    pstmt.setInt(1, month);
                    for (int index = 0; index < doctorList.size(); ++index) {
                        pstmt.setInt(1 + index, doctorList.get(index).getId());
                    }
                    ResultSet rs = pstmt.executeQuery();
                    List<Integer> available_days = new ArrayList<>(32);
                    while(rs.next()) {
                        available_days.add(rs.getInt(1));
                    }

                    return available_days;
                }

            }
        }
        return new ArrayList<>();
    }

    @GetMapping(value="/program-info/days", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Integer> getProgram(@RequestParam("month") int month,
                                    @RequestParam("year") int year,
                                    @RequestParam(value = "doctor", required = false) Integer doctor,
                                    @RequestParam(value = "specialisation", required = false) Integer specialisation) throws Exception {
        List<Doctor> doctorList = null;
        DoctorDAO doctorDAO = new DoctorDAO();
        if (doctor != null) {
            Doctor d = doctorDAO.findById(doctor);
            if (d != null) {
                doctorList = new LinkedList<>();
                doctorList.add(d);
            }
            else {
                throw new Exception("Doctor does not exist");
            }
        }
        else if (specialisation != null) {
            doctorList = doctorDAO.findBySpecialisation(specialisation);
        }
        else {
            throw new Exception("No specialisation selected");
        }

        if(doctorList != null && doctorList.size() > 0) {//any doctor
            try(Connection con = Database.getConnection()) {
                StringBuilder sb = new StringBuilder("select distinct to_char(zi,'DD')::integer from program_doctori where to_char(zi,'MM')::integer = (?) and to_char(zi,'YYYY')::integer = (?) and id_doctor in (");
                sb.append("?,".repeat(doctorList.size()));
                sb.deleteCharAt(sb.length()-1);
                sb.append(")");
                try (PreparedStatement pstmt = con.prepareStatement(sb.toString())) {
                    pstmt.setInt(1, month);
                    pstmt.setInt(2, year);
                    for (int index = 0; index < doctorList.size(); ++index) {
                        pstmt.setInt(3 + index, doctorList.get(index).getId());
                    }
                    ResultSet rs = pstmt.executeQuery();
                    List<Integer> available_days = new ArrayList<>(32);
                    while(rs.next()) {
                        available_days.add(rs.getInt(1));
                    }

                    System.out.println("Available dates:"+available_days);
                    return available_days;
                }

            }
        }
        return new ArrayList<>();
    }

    @GetMapping(value="/program-info/hours", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getHourProgram(@RequestParam("userId") int userId,
                                       @RequestParam("day") int day,
                                       @RequestParam("month") int month,
                                       @RequestParam("year") int year,
                                       @RequestParam(value = "doctor", required = false) Integer doctor,
                                       @RequestParam(value = "specialisation", required = false) Integer specialisation) throws Exception {
        List<Doctor> doctorList = null;
        DoctorDAO doctorDAO = new DoctorDAO();
        if (doctor != null) {
            Doctor d = doctorDAO.findById(doctor);
            if (d != null) {
                doctorList = new LinkedList<>();
                doctorList.add(d);
            }
            else {
                throw new Exception("Doctor does not exist");
            }
        }
        else if (specialisation != null) {
            doctorList = doctorDAO.findBySpecialisation(specialisation);
        }
        else {
            throw new Exception("No specialisation selected");
        }

        if(doctorList != null && doctorList.size() > 0) {//any doctor
            try(Connection con = Database.getConnection()) {
                //StringBuilder sb = new StringBuilder("select distinct to_char(zi,'DD')::integer from program_doctori where to_char(zi,'MM')::integer = (?) and to_char(zi,'YYYY')::integer = (?) and id_doctor in (");
                try (PreparedStatement pstmt = con.prepareStatement("select get_free_hours_of_pacient_of_doctor_on_date((?),(?), '"+day+"-"+month+"-"+year+"'::DATE)")) {
                    List<String> available_hours = new ArrayList<>(50);

                    for(Doctor i : doctorList) {
                        pstmt.setInt(1,userId);
                        pstmt.setInt(2,i.getId());
                        ResultSet rs = pstmt.executeQuery();
                        if(rs.next()) {
                            String result = rs.getString(1);
                            System.out.println(result);
                            available_hours.addAll(Arrays.stream(result.split(",")).filter((h) -> !available_hours.contains(h)).toList());
                        }

                    }
                    available_hours.sort((a,b) -> {
                        if (Integer.parseInt(a.split(":")[0]) > Integer.parseInt(b.split(":")[0])) {
                            return 1;
                        } else if(Integer.parseInt(a.split(":")[0]) == Integer.parseInt(b.split(":")[0])) {
                            return a.split(":")[1].charAt(0) - (b.split(":")[1].charAt(0));
                        } else{
                            return -1;
                        }
                    });
                    System.out.println("hours:" + available_hours);
                    return available_hours;
                }

            }
        }
        return new ArrayList<>();
    }

    @PostMapping("/add-doctor")
    @ResponseBody
    public ResponseEntity<String> addDoctor(@RequestParam("first-name") String firstName,
                                            @RequestParam("last-name") String lastName,
                                            @RequestParam("specialization") Integer specialization,
                                            @RequestParam("cabinet") Integer cabinet,
                                            @RequestParam("phone") String phone,
                                            @RequestParam("email") String email,
                                            @RequestParam("image") MultipartFile image) {
        try {
            List<Integer> ids = DoctorDAO.findIdsList(firstName,lastName,phone,email);
            for(Integer id_doctor : ids){
                int id_specializare = SpecializareDAO.findIdSpecializareByDoctorId(id_doctor);
                System.out.println("id specializare: " + id_specializare);
                if(id_specializare == specialization){
                    System.out.println("Ar trebui sa nu se adauge");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Doctor already exists.");
                }
            }

            String fileName = image.getOriginalFilename();
            if(Objects.equals(phone, "")){
                phone = "Not added";
            }
            if(Objects.equals(email, "")){
                email = "Not added";
            }
            if(!Objects.equals(fileName, "")){
                Path p = Paths.get("./"+filesPath+"/doctor_pictures/"+fileName);
                String filePath = p.toString();
                image.transferTo(p);
                var doctor = new DoctorDAO();
                doctor.create(firstName,lastName,phone,email,filePath,cabinet);
            }
            else{
                var doctor = new DoctorDAO();
                doctor.create(firstName,lastName,phone,email,cabinet);
            }

            int idDoctor = DoctorDAO.findMaxId();
            var doctorSpecializare = new DoctoriSpecializariDAO();
            doctorSpecializare.create(idDoctor, specialization);
            return ResponseEntity.ok("Doctor added successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the file.");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add the doctor.");
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
            List<Integer> ids = DoctorDAO.findIdsList(firstName,lastName,phone,email);
            for(Integer id_doctor : ids){
                int id_specializare = SpecializareDAO.findIdSpecializareByDoctorId(id_doctor);
                if(id_specializare == specialization){
                    return "Doctor already exists.";
                }
            }

            String fileName = image.getOriginalFilename();
            if(Objects.equals(phone, "")){
                phone = "Not added";
            }
            if(Objects.equals(email, "")){
                email = "Not added";
            }
            if(!Objects.equals(fileName, "")){
                Path p = Paths.get("./"+filesPath+"/doctor_pictures/"+fileName);
                String filePath = p.toString();
                image.transferTo(p);
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


    //@GetMapping(value = "/your_appointments", produces = MediaType.APPLICATION_JSON_VALUE)

    @GetMapping(value = "/appointments/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Programare> getUserAppointments(@PathVariable("userId") int userId) {
        try {
            return new ProgramareDAO().findAllOfPatientId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve doctor information.");
        }
    }

    @PostMapping(value = "/appointments/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void makeAppointment(@PathVariable("userId") int userId, @RequestBody RequestProgramare request) throws Exception {
        System.out.println(request);
        ProgramareDAO prog = new ProgramareDAO();

        List<Doctor> doctorList = null;
        DoctorDAO doctorDAO = new DoctorDAO();

        if(request.getSpecializare() <= 0 && request.getDoctor() <= 0) {
            throw new Exception("Not enough data");
        }
        if(request.getSpecializare() > 0 && request.getDoctor() > 0 &&
                DoctorDAO.findById(request.getDoctor()).getSpecializari().stream().noneMatch((x) -> x.getId() == request.getSpecializare())) {
            throw new Exception("Specialisation conflict with doctor");
        }
        if (request.getDoctor() > 0) {
            Doctor d = doctorDAO.findById(request.getDoctor());
            if (d != null) {
                doctorList = new LinkedList<>();
                doctorList.add(d);
            }
            else {
                throw new Exception("Doctor does not exist");
            }
        }
        else if (request.getSpecializare() > 0) {
            doctorList = doctorDAO.findBySpecialisation(request.getSpecializare());
        }
        else {
            throw new Exception("No specialisation selected");
        }

        if(request.getSpecializare() > 0 && request.getDoctor() <= 0) {
            for (Doctor i : new DoctorDAO().findBySpecialisation(request.getSpecializare())) {
                try {
                    prog.create(userId, i.getId(), request.getDates().get(0), request.getHour());
                } catch(SQLException e) {
                    System.out.println(e);
                }
            }
        }
        else {
            prog.create(userId, request.getDoctor(), request.getDates().get(0), request.getHour());
        }
    }

    @GetMapping(value = "/appointment-info/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Programare getAppointmentById(@PathVariable("appointmentId") Integer appointmentId) throws SQLException {
        var programare = ProgramareDAO.findById(appointmentId);
        return ProgramareDAO.findById(appointmentId);
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