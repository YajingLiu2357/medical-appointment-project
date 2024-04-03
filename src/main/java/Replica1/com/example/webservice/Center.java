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
}


