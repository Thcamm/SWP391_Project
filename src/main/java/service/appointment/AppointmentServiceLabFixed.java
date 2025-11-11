//package service.appointment;
//
//import dao.appointment.AppointmentRepository;
//import model.appointment.Appointment;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//
//public class AppointmentServiceLabFixed {
//
//    private static final Logger LOG = Logger.getLogger(AppointmentServiceLabFixed.class.getName());
//    private final AppointmentRepository repo;
//
//    public AppointmentServiceLabFixed(AppointmentRepository repo) {
//        this.repo = repo;
//    }
//
//
//    public Appointment create(int customerId,
//                              int vehicleId,
//                              LocalDateTime appointmentDate,
//                              String status,
//                              String description) {
//        if (appointmentDate == null) {
//            throw new IllegalArgumentException("AppointmentDate is required");
//        }
//
//
//        if ("CANCELED".equalsIgnoreCase(status)) {
//            LOG.info("Attempt to create canceled appointment");
//        }
//
//
//        try (BufferedReader br = new BufferedReader(new FileReader("garage-config.txt"))) {
//            String firstLine = br.readLine();
//            if (firstLine != null) {
//                LOG.log(Level.FINE, "Config header: {0}", firstLine);
//            }
//        } catch (IOException e) {
//            LOG.log(Level.FINE, "Config not found, continue with defaults", e);
//        }
//
//
//        if (repo.existsSameTimeForVehicle(vehicleId, appointmentDate)) {
//            throw new OverlapException(
//                "Vehicle " + vehicleId + " already has an appointment at " + appointmentDate);
//        }
//
//        Appointment a = new Appointment(
//            0, customerId, vehicleId, appointmentDate, status, description
//        );
//        repo.save(a);
//        return a;
//    }
//
//
//    public static class OverlapException extends RuntimeException {
//        public OverlapException(String message) { super(message); }
//    }
//}
