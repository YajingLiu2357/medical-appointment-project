package com.example.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style= SOAPBinding.Style.RPC)
public interface Hospital {
    String sayHello(String name);
    @WebMethod
    boolean AddAppointment(String appointmentID, short appointmentType,
                           short capacity);

    @WebMethod
    boolean RemoveAppointment(String appointmentID, short appointmentType);

    @WebMethod
    String ListAppointmentAvailability(short AppointmentType);

    @WebMethod
    String ListAppointmentAvailabilityCompatibility(short AppointmentType);

    @WebMethod
    short BookAppointment(String patientID, String appointmentID,
                            short appointmentType);

    @WebMethod
    String GetAppointmentSchedule(String patientID);

    @WebMethod
    String GetAppointmentScheduleCompatibility(String patientID);

    @WebMethod
    String GetAppointmentScheduleLocal(String patientID);

    @WebMethod
    boolean CancelAppointment(String patientID, String appointmentID);

    @WebMethod
    boolean CheckUser(String userID);

    @WebMethod
    String RegisterUser(short cityType, short userType);

    @WebMethod
    short SwapAppointment(String patientID, String oldAppointmentID, short oldAppointmentType, String newAppointmentID, short newAppointmentType);
}
