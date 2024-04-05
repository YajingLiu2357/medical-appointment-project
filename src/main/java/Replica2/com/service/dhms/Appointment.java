package Replica2.com.service.dhms;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface Appointment {
    @WebMethod
    String addAppointment(String appointmentID, String appointmentType, int capacity);
    @WebMethod
    String removeAppointment(String appointmentID, String appointmentType);
    @WebMethod
    String listAppointmentAvailability (String appointmentType);
    @WebMethod
    String bookAppointment(String patientID, String appointmentID, String appointmentType);
    @WebMethod
    String getAppointmentSchedule(String patientID);
    @WebMethod
    String cancelAppointment(String patientID, String appointmentID);
    @WebMethod
    String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType);
}
