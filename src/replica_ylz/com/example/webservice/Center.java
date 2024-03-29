package com.example.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;


@WebService
@SOAPBinding(style= SOAPBinding.Style.RPC)
public interface Center {
    String sayHello(String name);

    //compatibility interface
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


    //original interface
    @WebMethod
    String RegisterUser(short cityType, short userType);

    @WebMethod
    boolean CheckUser(short city, String userID);

    @WebMethod
    boolean BookAppointment(short city, String userID, String appointmentID, short appointmentType);

    @WebMethod
    boolean CancelAppointment(short city, String userID, String appointmentID);

    @WebMethod
    String GetAppointmentSchedule(short city, String userID);

    @WebMethod
    short SwapAppointment(short CityType, String patientID, String oldAppointmentID, short oldAppointmentType, String newAppointmentID, short newAppointmentType);

    @WebMethod
    boolean AddAppointment(short cityType, String appointmentID, short appointmentType, short capacity);

    @WebMethod
    boolean RemoveAppointment(short cityType, String appointmentID, short appointmentType);

    @WebMethod
    String ListAppointmentAvailability(short cityType, short appointmentType);
}


