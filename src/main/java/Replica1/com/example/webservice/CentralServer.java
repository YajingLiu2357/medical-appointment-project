package Replica1.com.example.webservice;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.net.URL;
import javax.jws.WebMethod;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class CentralServer {
    private static CentralServer instance = null;
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

    public String addAppointment(String appointmentID, String appointmentType, int capacity){
        Type.AppointmentEntity appInstance = new Type.AppointmentEntity();
        appInstance.DeserializeAppointmentEntity(appointmentID);
        return GetCityOpr(appInstance.city).addAppointment(appointmentID, appointmentType, capacity);
    }

    public String removeAppointment(String appointmentID, String appointmentType){
        Type.AppointmentEntity appInstance = new Type.AppointmentEntity();
        appInstance.DeserializeAppointmentEntity(appointmentID);
        return GetCityOpr(appInstance.city).removeAppointment(appointmentID, appointmentType);
    }

    public String listAppointmentAvailability (String appointmentType){
        return GetCityOpr(Type.CityType.MTL).listAppointmentAvailability(appointmentType);
    }

    public String bookAppointment(String patientID, String appointmentID, String appointmentType){
        Type.UserEntity userInstance = new Type.UserEntity();
        userInstance.DeserializeUser(patientID);
        return GetCityOpr(userInstance.city).bookAppointment(patientID, appointmentID, appointmentType);
    }

    public String getAppointmentSchedule(String patientID){
        Type.UserEntity userInstance = new Type.UserEntity();
        userInstance.DeserializeUser(patientID);
        return GetCityOpr(userInstance.city).getAppointmentSchedule(patientID);
    }

    public String cancelAppointment(String patientID, String appointmentID){
        Type.UserEntity userInstance = new Type.UserEntity();
        userInstance.DeserializeUser(patientID);
        return GetCityOpr(userInstance.city).cancelAppointment(patientID, appointmentID);
    }

    public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType){
        Type.UserEntity userInstance = new Type.UserEntity();
        userInstance.DeserializeUser(patientID);
        return GetCityOpr(userInstance.city).swapAppointment(patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
    }

    public void Initialize()  {
        hospitalServerList.put(Type.CityType.MTL, 1099);
        hospitalServerList.put(Type.CityType.QUE, 1100);
        hospitalServerList.put(Type.CityType.SHE, 1101);

        try{
            String ip = InetAddress.getLocalHost().getHostAddress();
            URL urlMTL = new URL("http://"+ip+":8081/MTL?wsdl");
            QName qNameMTL = new QName("http://webservice.example.com.Replica1/", "HospitalImplService");
            Service serviceMTL = Service.create(urlMTL, qNameMTL);
            QName qNameMTL2 = new QName("http://webservice.example.com.Replica1/", "HospitalImplPort");
            Hospital MTLService = serviceMTL.getPort(qNameMTL2, Hospital.class);

            URL urlQUE = new URL("http://"+ip+":8082/QUE?wsdl");
            QName qNameQUE = new QName("http://webservice.example.com.Replica1/", "HospitalImplService");
            Service serviceQUE = Service.create(urlQUE, qNameQUE);
            QName qNameQUE2 = new QName("http://webservice.example.com.Replica1/", "HospitalImplPort");
            Hospital QUEService = serviceQUE.getPort(qNameQUE2, Hospital.class);

            URL urlSHE = new URL("http://"+ip+":8083/SHE?wsdl");
            QName qNameSHE = new QName("http://webservice.example.com.Replica1/", "HospitalImplService");
            Service serviceSHE = Service.create(urlSHE, qNameSHE);
            QName qNameSHE2 = new QName("http://webservice.example.com.Replica1/", "HospitalImplPort");
            Hospital SHEService = serviceSHE.getPort(qNameSHE2, Hospital.class);

            cityOperations.put(Type.CityType.MTL, MTLService);
            cityOperations.put(Type.CityType.QUE, QUEService);
            cityOperations.put(Type.CityType.SHE, SHEService);
        } catch (MalformedURLException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private CentralServer() {
        Initialize();
    }

}
