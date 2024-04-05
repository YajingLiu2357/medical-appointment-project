package com.example.webservice;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class RequestProcessor {

    public Center replica1Connection;

    //todo:add the other replicas

    private static RequestProcessor instance = null;

    public static RequestProcessor getInstance() {
        if(instance == null){
            instance = new RequestProcessor();
        }
        return instance;
    }

    private RequestProcessor() {
        try {
            URL url = new URL("http://localhost:8080/center?wsdl");
            QName qName = new QName("http://webservice.example.com/", "Replica2");
            Service service = Service.create(url, qName);
            replica1Connection = service.getPort(Center.class);
        }
        catch (Exception e) {
            System.out.println("Client exception: " + e);
            e.printStackTrace();
        }
    }

    public List<Boolean> IsValidUserName(String userID) {
        Type.UserEntity userEntity1 = new Type.UserEntity();
        userEntity1.DeserializeUser(userID);
        List<Boolean> results = new ArrayList<>();
        boolean res_replica1 = replica1Connection.CheckUser((short)userEntity1.city.ordinal(), userID);
        //todo: replica2, replica3, replica4
        results.add(res_replica1);
        return results;
    }

    public List<String> BookAppointment(String userid_, String city_, String time_, String date_, String month_, String year_, String type_) {
        String appointmentID = city_ + time_ + date_ + month_ + year_;
        String exchangedAvailableAppointments = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(type_));

        List<String> results = new ArrayList<>();
        String res_replica1 = replica1Connection.bookAppointment(userid_, appointmentID, exchangedAvailableAppointments);
        //todo: replica2, replica3, replica4
        results.add(res_replica1);
        return results;
    }

    public List<String> CancelAppointment(String userID, String appointmentID) {

        List<String> results = new ArrayList<>();
        String res_replica1 = replica1Connection.cancelAppointment(userID, appointmentID);
        results.add(res_replica1);
        //todo: replica2, replica3, replica4

        return results;
    }

    public List<String> ViewBookedAppointments(String userID) {
        List<String> results = new ArrayList<>();
        String rawRes = replica1Connection.getAppointmentSchedule(userID);
        results.add(rawRes);
        //todo: replica2, replica3, replica4
        return results;
    }

    public List<String> AddAppointment(String city_, String time_, String date_, String month_, String year_, String type_, int capacity) throws RemoteException, NotBoundException {
        List<String> results = new ArrayList<>();
        String appointmentID = city_ + time_ + date_ + month_ + year_;
        String exchangedAvailableAppointments = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(type_));
        String res_replica1 = replica1Connection.addAppointment(appointmentID, exchangedAvailableAppointments, capacity);
        results.add(res_replica1);
        //todo: replica2, replica3, replica4

        return results;
    }

    public List<String> RemoveAppointment(String appointmentID, String type_) throws RemoteException, NotBoundException {
        List<String> results = new ArrayList<>();
        String exchangedAvailableAppointments = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(type_));
        String res_replica1 = replica1Connection.removeAppointment(appointmentID, exchangedAvailableAppointments);
        results.add(res_replica1);
        //todo: replica2, replica3, replica4
        return results;
    }

    public List<String> ViewAvailableAppointments() {
        List<String> results = new ArrayList<>();
        String exchangedAvailableAppointments = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.PHYS);
        String rawValiableRes = replica1Connection.listAppointmentAvailability(exchangedAvailableAppointments);
        rawValiableRes = rawValiableRes.substring(1, rawValiableRes.length()-1);
        results.add(rawValiableRes);
        //todo: replica2, replica3, replica4

        return results;
    }

    public List<String> SwapAppointment(String cityType, String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType)
    {
        String oldAppType = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(oldAppointmentType));
        String newAppType = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(newAppointmentType));

        List<String> results = new ArrayList<>();
        String res_replica1 = replica1Connection.swapAppointment(patientID, oldAppointmentID, oldAppType,
                newAppointmentID, newAppType);
        results.add(res_replica1);
        //todo: replica2, replica3, replica4

        return results;
    }
}
