package Replica3.montreal_server.src.ws;

import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style=Style.RPC)
public interface DhmsMontreal {
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
