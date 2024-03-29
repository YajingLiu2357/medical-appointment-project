package com.example.webservice;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class CentralServer {
    private static CentralServer instance = null;
    private Lock lock = new ReentrantLock();
    private ConcurrentHashMap<Type.CityType, Integer> hospitalServerList = new ConcurrentHashMap<Type.CityType, Integer>();
    private ConcurrentHashMap<Type.CityType, Hospital> cityOperations = new ConcurrentHashMap<Type.CityType, Hospital>();

    //Server is a singleton
    public static CentralServer getInstance() throws NotBoundException, RemoteException {
        if(instance == null){
            instance = new CentralServer();
        }
        return instance;
    }

    public Hospital GetCityOpr(Type.CityType c){ return cityOperations.get(c); }

    public String RegisterUser(Type.CityType cityT, Type.UserType userT) throws NotBoundException, RemoteException {
        return GetCityOpr(cityT).RegisterUser((short)cityT.ordinal(), (short)userT.ordinal());
    }

    public boolean CheckUser(Type.CityType city, String userID) throws NotBoundException, RemoteException {
        return GetCityOpr(city).CheckUser(userID);
    }

    public short BookAppointment(Type.CityType city, String userid_, String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return GetCityOpr(city).BookAppointment(userid_, appointmentID, (short)type.ordinal());
    }

    public boolean CancelAppointment(Type.CityType city, String userID, String apID) throws NotBoundException, RemoteException {
        return GetCityOpr(city).CancelAppointment(userID, apID);
    }

    public String GetAppointmentScheduleMarshalling(Type.CityType cityType, String userID){
        return GetCityOpr(cityType).GetAppointmentSchedule(userID);
    }

    public String GetAppointmentScheduleMarshallingCompatibility(Type.CityType cityType, String userID){
        return GetCityOpr(cityType).GetAppointmentScheduleCompatibility(userID);
    }

    public short SwapAppointment(String cityType, String patientID, String oldAppointmentID,
                                 short oldAppointmentType, String newAppointmentID, short newAppointmentType)
    {
        return GetCityOpr(Type.CityType.valueOf(cityType)).SwapAppointment(patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
    }

    public HashMap<String, Type.AppointmentType> GetAppointmentSchedule(Type.CityType city, String userID) throws NotBoundException, RemoteException {
        String rawRes = GetCityOpr(city).GetAppointmentSchedule(userID);
        return Type.UnmarshallingAppointmentsAndType(rawRes);
    }

    public boolean addAppointment(Type.CityType city, String appointmentID, Type.AppointmentType type, int capacity) throws NotBoundException, RemoteException {
        return GetCityOpr(city).AddAppointment(appointmentID, (short)type.ordinal(), (short)capacity);
    }

    public boolean RemoveAppointment(Type.CityType city, String appointmentID, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return GetCityOpr(city).RemoveAppointment(appointmentID, (short)type.ordinal());
    }

    public String ListAppointmentAvailabilityMarshalling(Type.CityType city, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return GetCityOpr(city).ListAppointmentAvailability((short)type.ordinal());
    }

    public String ListAppointmentAvailabilityMarshallingCompatibility(Type.CityType city, Type.AppointmentType type) throws NotBoundException, RemoteException {
        return GetCityOpr(city).ListAppointmentAvailabilityCompatibility((short)type.ordinal());
    }

    ConcurrentHashMap<String, Integer> ListAppointmentAvailability(Type.CityType city, Type.AppointmentType type) throws NotBoundException, RemoteException {
        String rawRes = GetCityOpr(city).ListAppointmentAvailability((short)type.ordinal());
        return Type.UnmarshallingConcurrentHashMap(rawRes);
    }

    public void Initialize()  {
        hospitalServerList.put(Type.CityType.MTL, 1099);
        hospitalServerList.put(Type.CityType.QUE, 1100);
        hospitalServerList.put(Type.CityType.SHE, 1101);

        try{
            URL urlMTL = new URL("http://localhost:8081/MTL?wsdl");
            QName qNameMTL = new QName("http://webservice.example.com/", "HospitalImplService");
            Service serviceMTL = Service.create(urlMTL, qNameMTL);
            Hospital MTLService = serviceMTL.getPort(Hospital.class);

            URL urlQUE = new URL("http://localhost:8082/QUE?wsdl");
            QName qNameQUE = new QName("http://webservice.example.com/", "HospitalImplService");
            Service serviceQUE = Service.create(urlQUE, qNameQUE);
            Hospital QUEService = serviceQUE.getPort(Hospital.class);

            URL urlSHE = new URL("http://localhost:8083/SHE?wsdl");
            QName qNameSHE = new QName("http://webservice.example.com/", "HospitalImplService");
            Service serviceSHE = Service.create(urlSHE, qNameSHE);
            Hospital SHEService = serviceSHE.getPort(Hospital.class);

            cityOperations.put(Type.CityType.MTL, MTLService);
            cityOperations.put(Type.CityType.QUE, QUEService);
            cityOperations.put(Type.CityType.SHE, SHEService);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private CentralServer() throws NotBoundException, RemoteException {
        Initialize();
    }

}
