package com.example.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@WebService(endpointInterface = "com.example.webservice.Hospital")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class HospitalImpl implements Hospital {
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }

    @Override
    public boolean AddAppointment(String appointmentID, short appointmentType, short capacity) {
        try{
            return HospitalServer.getInstance().AddAppointment(appointmentID, Type.AppointmentType.values()[appointmentType], capacity);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean RemoveAppointment(String appointmentID, short appointmentType) {
        try{
            return HospitalServer.getInstance().RemoveAppointment(appointmentID, Type.AppointmentType.values()[appointmentType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String ListAppointmentAvailability(short AppointmentType) {
        try{
            ConcurrentHashMap<String, Integer> res = HospitalServer.getInstance().ListAppointmentAvailability(Type.AppointmentType.values()[AppointmentType]);
            System.out.println("ths concurrent map size:" + res.size());
            return Type.MarshallingHashMap(res);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String ListAppointmentAvailabilityCompatibility(short AppointmentType) {
        try{
            ConcurrentHashMap<String, Integer> res = HospitalServer.getInstance().ListAppointmentAvailability(Type.AppointmentType.values()[AppointmentType]);
            System.out.println("ths concurrent map size:" + res.size());
            return Type.MarshallingHashMapCompatibility(res);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public short BookAppointment(String patientID, String appointmentID, short appointmentType) {
        try{
            return HospitalServer.getInstance().BookAppointment(patientID, appointmentID, Type.AppointmentType.values()[appointmentType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String GetAppointmentSchedule(String patientID) {
        try{
            HashMap<String, Type.AppointmentType> res = HospitalServer.getInstance().GetAppointmentSchedule(patientID);
            return Type.MarshallingAppointmentsAndType(res);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String GetAppointmentScheduleLocal(String patientID) {
        try{
            HashMap<String, Type.AppointmentType> res = HospitalServer.getInstance().GetAppointmentScheduleLocal(patientID);
            return Type.MarshallingAppointmentsAndType(res);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String GetAppointmentScheduleCompatibility(String patientID){
        try{
            HashMap<String, Type.AppointmentType> res = HospitalServer.getInstance().GetAppointmentScheduleLocal(patientID);
            return Type.MarshallingAppointmentsAndTypeCompatibility(patientID, res);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean CancelAppointment(String patientID, String appointmentID) {
        try{
            return HospitalServer.getInstance().CancelAppointment(patientID, appointmentID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean CheckUser(String userID) {
        try{
            return HospitalServer.getInstance().CheckUser(userID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String RegisterUser(short cityType, short userType) {
        try{
            return HospitalServer.getInstance().RegisterUser(Type.CityType.values()[cityType], Type.UserType.values()[userType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public short SwapAppointment(String patientID, String oldAppointmentID, short oldAppointmentType, String newAppointmentID, short newAppointmentType) {
        try{
            return HospitalServer.getInstance().SwapAppointment(patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}