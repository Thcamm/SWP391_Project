/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao.appointment;

import java.time.LocalDateTime;
import model.appointment.Appointment;

/**
 *
 * @author ADMIN
 */
public interface AppointmentRepository {
    
    void save(Appointment a); 
    boolean existsSameTimeForVehicle (int vehicleId, LocalDateTime datetime); 
    
}
