package com.example.webservice;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;


public class HospitalServer {
    static public Type.CityType cityType;
    public ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>> serverData = new ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>>();
    //UserID appointment record
    public ConcurrentHashMap<String, ConcurrentHashMap<String, Type.AppointmentType>> bookingRecord = new ConcurrentHashMap<String, ConcurrentHashMap<String, Type.AppointmentType>>();
    private static HospitalServer instance = null;
    public AtomicInteger userIndex = new AtomicInteger();
    private ConcurrentHashMap<String, String> registedUser = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<Type.CityType, Integer> hospitalUDPPorts = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Type.CityType, Hospital> cityHospitalInterface = new ConcurrentHashMap<>();

    private boolean isInitializeServerConnection = false;

    //to make sure the correctness of concurrency
    private Lock readwritelock = new ReentrantLock();
    private Lock swapLock = new ReentrantLock(); //make sure the swap operation is an atomic operation



    //Server is a singleton
    public static HospitalServer getInstance() throws NotBoundException, RemoteException {
        if(instance == null){
            instance = new HospitalServer();
        }
        return instance;
    }

    private HospitalServer() throws NotBoundException, RemoteException {

        hospitalUDPPorts.put(Type.CityType.MTL, 6000);
        hospitalUDPPorts.put(Type.CityType.QUE, 6001);
        hospitalUDPPorts.put(Type.CityType.SHE, 6002);

        serverData.put(Type.AppointmentType.DENT, new ConcurrentHashMap<String, Integer>());
        serverData.put(Type.AppointmentType.PHYS, new ConcurrentHashMap<String, Integer>());
        serverData.put(Type.AppointmentType.SURG, new ConcurrentHashMap<String, Integer>());
        InitializeServerConnection();
        try{
            InitializeFileSystem(HospitalServer.cityType);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    public boolean InitializeServerConnection() throws RemoteException, NotBoundException {
        if(isInitializeServerConnection) return true;
        System.out.println("InitializeServerConnection");
        isInitializeServerConnection = true;

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


            cityHospitalInterface.put(Type.CityType.MTL, MTLService);
            cityHospitalInterface.put(Type.CityType.QUE, QUEService);
            cityHospitalInterface.put(Type.CityType.SHE, SHEService);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean InitializeFileSystem(Type.CityType cityType_) throws IOException {
        this.cityType = cityType_;
        return true;
    }
    public String RegisterUser(Type.CityType city, Type.UserType userType) throws NotBoundException, RemoteException {
        if(!isInitializeServerConnection){
            InitializeServerConnection();
        }
        Integer newUser = userIndex.getAndIncrement();
        Type.UserEntity entity = new Type.UserEntity();
        entity.user = userType;
        entity.city = city;
        entity.index = newUser;
        String newUserID = entity.SerializeUser();
        registedUser.put(newUserID, "some client information");
        return newUserID;
    }
    public boolean CheckUser(String userID) throws NotBoundException, RemoteException {
        if(!isInitializeServerConnection){
            InitializeServerConnection();
        }
        if(registedUser.containsKey(userID))
            return true;
        return false;
    }
    public boolean AddAppointment(String appointmentID, Type.AppointmentType type, int capacity) {
        try
        {
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            readwritelock.lock();
            if(serverData.get(type).containsKey(appointmentID))
                return false;
            HospitalServer.getInstance().serverData.get(type).put(appointmentID, capacity);
            System.out.println("Add Availiable Appointment:" + appointmentID + " Appointment Type:" + type.toString() + " Capacity:" + String.valueOf(capacity));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } finally {
            readwritelock.unlock();
        }
        return true;
    }

    public boolean RemoveAppointment(String appointmentID, Type.AppointmentType type){
        try{
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            readwritelock.lock();

            if(HospitalServer.getInstance().serverData.get(type).containsKey(appointmentID)){
                HospitalServer.getInstance().serverData.get(type).remove(appointmentID);
                //just remove related appointments
                for(String userID : bookingRecord.keySet()){
                    if(bookingRecord.get(userID).containsKey(appointmentID)){
                        bookingRecord.get(userID).remove(appointmentID);
                    }
                }
                return true;
            }
            else { //not exist
                return false;
            }
            //System.out.println("Remove Availiable Appointment:" + appointmentID + " Appointment Type:" + type.toString());
        }
        catch (IOException e){
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } finally {
            readwritelock.unlock();
        }
    }

    public HashMap<String, Integer> ListAppointmentAvailabilityLocal(Type.AppointmentType type){
        HashMap<String, Integer> resObject = new HashMap<String, Integer>();
        for(String key : serverData.get(type).keySet())
        {
            Integer value = serverData.get(type).get(key);
            //current appoint has been full
            if(value > 0)
                resObject.put(key, value);
        }
        return resObject;
    }

    public String MarshallingAppointmentAvaliableLocal(){
        HashMap<String, Integer> dentalApps = ListAppointmentAvailabilityLocal(Type.AppointmentType.DENT);
        String dentalPlatStr = Type.MarshallingHashMap(dentalApps);
        HashMap<String, Integer> surgeonApps = ListAppointmentAvailabilityLocal(Type.AppointmentType.SURG);
        String surPlatStr = Type.MarshallingHashMap(surgeonApps);
        HashMap<String, Integer> physicalApps = ListAppointmentAvailabilityLocal(Type.AppointmentType.PHYS);
        String phyPlatStr = Type.MarshallingHashMap(physicalApps);
        return dentalPlatStr + surPlatStr + phyPlatStr;
    }

    class AdminRecordsQuery implements Runnable {
        private int portNum;
        public ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>> queryRecords;
        // constructor
        public AdminRecordsQuery(int portNum, ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>> query_) {
            this.portNum = portNum;
            this.queryRecords = query_;
        }

        int Unmarshalling(String originStr, int startIndex, Type.AppointmentType type){
            System.out.println(originStr);
            String dentalNumStr = originStr.substring(startIndex, startIndex + 4);
            startIndex = startIndex + 4;
            System.out.println("number:" + dentalNumStr + "\n");
            Integer dentalNum = Integer.valueOf(dentalNumStr);
            for(int i = 0; i < dentalNum; ++i){
                String appointmentID = originStr.substring(startIndex, startIndex + 10);
                System.out.println("appID:" + appointmentID + "\n");
                startIndex = startIndex + 10;
                String capacityNum = originStr.substring(startIndex, startIndex + 4);
                System.out.println("capa:" + capacityNum + "\n");
                startIndex = startIndex + 4;
               queryRecords.get(type).put(appointmentID, Integer.valueOf(capacityNum));
            }
            return startIndex;
        }

        public void run() {
            DatagramSocket aSocket = null;
            try {
                aSocket = new DatagramSocket();
                String m = "1";
                InetAddress aHost = InetAddress.getByName("127.0.0.1");
                int serverPort = this.portNum;
                DatagramPacket request =
                        new DatagramPacket(m.getBytes(),  m.length(), aHost, serverPort);
                aSocket.send(request);
                System.out.println("Sent");
                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);
                System.out.println("Received");
                String responseStr = new String(reply.getData());
                int startIndex = 0;

                startIndex = Unmarshalling(responseStr, startIndex, Type.AppointmentType.DENT);
                startIndex = Unmarshalling(responseStr, startIndex, Type.AppointmentType.SURG);
                startIndex = Unmarshalling(responseStr, startIndex, Type.AppointmentType.PHYS);

            } catch (RemoteException | SocketException e) {
                throw new RuntimeException(e);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                if(aSocket != null) aSocket.close();
            }
        }
    }

    public ConcurrentHashMap<String, Integer> ListAppointmentAvailability(Type.AppointmentType type)
    {
        ConcurrentHashMap<String, Integer> resObject = new ConcurrentHashMap<String, Integer>();
        try{
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            //multi thread
            int numofHospitals = 3;
            ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>> totalRecord = new ConcurrentHashMap<Type.AppointmentType, ConcurrentHashMap<String, Integer>>();
            totalRecord.put(Type.AppointmentType.DENT, new ConcurrentHashMap<String, Integer>());
            totalRecord.put(Type.AppointmentType.PHYS, new ConcurrentHashMap<String, Integer>());
            totalRecord.put(Type.AppointmentType.SURG, new ConcurrentHashMap<String, Integer>());
            Thread[] threads = new Thread[numofHospitals];

            System.out.println("first build up threads");

            threads[0] = new Thread(new AdminRecordsQuery(6000, totalRecord));
            threads[1] = new Thread(new AdminRecordsQuery(6001, totalRecord));
            threads[2] = new Thread(new AdminRecordsQuery(6002, totalRecord));

            for (int i = 0; i < numofHospitals; i++) {
                threads[i].start();
            }

            System.out.println("start threads");

            for (Thread t : threads) {
                t.join();
            }

            //System.out.println("List Availiable Appointment Type:" + type.toString());
            System.out.println("listres:" + totalRecord.toString());
            for(Type.AppointmentType typeKey : totalRecord.keySet())
            {
                ConcurrentHashMap<String, Integer> value = totalRecord.get(typeKey);
                for(String itemKey : value.keySet()){
                    Integer capacity = value.get(itemKey);
                    resObject.put(typeKey.toString() + ":" + itemKey, capacity);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } finally {
            //lock.unlock();
        }
        return resObject;
    }


    //return value: 0 Success 1 APPOINTMENT_NOT_EXIST 2 HAVE_SAME_TYPE_APPOINTMENT_SAME_DAY 3 THREE_APPOINTMENTS_OTHER_CITIES 4 NO_CAPACITY
    public short BookAppointment(String patientID, String appointmentID, Type.AppointmentType type){
        //there is no such open appointment
        try
        {
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            readwritelock.lock();
            Type.UserEntity userEnt = new Type.UserEntity();
            userEnt.DeserializeUser(patientID);

            Type.AppointmentEntity entity = new Type.AppointmentEntity();
            entity.DeserializeAppointmentEntity(appointmentID);

            if(entity.city == this.cityType){
                if(!serverData.get(type).containsKey(appointmentID))
                    return 1;

                int prev =  serverData.get(type).get(appointmentID).intValue();
                int appointmentSpace = prev;

                //there is no free space
                if(appointmentSpace <= 0)
                    return 4;

                if(!bookingRecord.containsKey(patientID))
                    bookingRecord.put(patientID, new ConcurrentHashMap<String, Type.AppointmentType>());
                else{
                    //duplicated record
                    if(bookingRecord.get(patientID).containsKey(appointmentID))
                        return 2;

                    //should not go other cities for three times
                    int bookedTimes = 0;
                    for(String key : bookingRecord.get(patientID).keySet()){
                        Type.AppointmentEntity entity1 = new Type.AppointmentEntity();
                        entity1.DeserializeAppointmentEntity(key);
                        if(entity1.city != userEnt.city) bookedTimes = bookedTimes + 1;
                    }
                    if(bookedTimes > 3) return 3;
                }

                bookingRecord.get(patientID).put(appointmentID, type);
                appointmentSpace = appointmentSpace - 1;

                serverData.get(type).remove(appointmentID);
                serverData.get(type).put(appointmentID, new Integer(appointmentSpace));
                System.out.println("Book Appointment:" + appointmentID + " Appointment Type:" + type.toString() + " patient:" + patientID);
            }
            else
            {
                short res = cityHospitalInterface.get(entity.city).BookAppointment(patientID, appointmentID, (short)type.ordinal());
                System.out.println("Book Appointment:" + appointmentID + " Appointment Type:" + type.toString() + " patient:" + patientID  + " Res:"+String.valueOf(res));
            }
        } catch (IOException | NotBoundException e) {
            throw new RuntimeException(e);
        } finally
        {
            readwritelock.unlock();
        }
        return 0;
    }

    public boolean CancelAppointment(String patientID, String appointmentID)
    {
        try
        {
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            Type.AppointmentEntity entity = new Type.AppointmentEntity();
            entity.DeserializeAppointmentEntity(appointmentID);
            readwritelock.lock();
            if(entity.city == this.cityType){
                //there is no such record
                if(!(bookingRecord.containsKey(patientID) && bookingRecord.get(patientID).containsKey(appointmentID)))
                    return false;

                //add the appointment
                Type.AppointmentType appType = bookingRecord.get(patientID).get(appointmentID);

                Integer freeSpace = serverData.get(appType).get(appointmentID);
                freeSpace = freeSpace + 1;

                serverData.get(appType).put(appointmentID, freeSpace);
                //cancel appointment
                bookingRecord.get(patientID).remove(appointmentID);

                System.out.println("Cancel Appointment:" + appointmentID + " patient:" + patientID);
                return true;
            }
            else{ //call other city
                boolean res = cityHospitalInterface.get(entity.city).CancelAppointment(patientID, appointmentID);
                return res;
            }
        } catch (IOException | NotBoundException e) {
            throw new RuntimeException(e);
        } finally
        {
            readwritelock.unlock();
        }
    }
    public HashMap<String, Type.AppointmentType> GetAppointmentScheduleLocal(String patientID){

        System.out.println("Call GetAppointmentScheduleLocal:" + this.cityType.toString());
        HashMap<String, Type.AppointmentType> resObject = new HashMap<String, Type.AppointmentType>();
        if(!bookingRecord.containsKey(patientID))
            return resObject;

        System.out.println("Call GetAppointmentScheduleLocal inside");
        for(String key : bookingRecord.get(patientID).keySet())
        {
            Type.AppointmentType value = bookingRecord.get(patientID).get(key);
            //current appoint has been full
            resObject.put(key, value);
        }
        System.out.println("Res:" + resObject.size());

        return resObject;
    }

    public HashMap<String, Type.AppointmentType> GetAppointmentSchedule(String patientID){
        HashMap<String, Type.AppointmentType> resObject = new HashMap<String, Type.AppointmentType>();
        try{
            if(!isInitializeServerConnection){
                InitializeServerConnection();
            }
            readwritelock.lock();
            //User Multiple RMI methods
            String rawLocalMtl = cityHospitalInterface.get(Type.CityType.MTL).GetAppointmentScheduleLocal(patientID);
            String rawLocalQue = cityHospitalInterface.get(Type.CityType.QUE).GetAppointmentScheduleLocal(patientID);
            String rawLocalShe = cityHospitalInterface.get(Type.CityType.SHE).GetAppointmentScheduleLocal(patientID);


            HashMap<String, Type.AppointmentType> resObjectLocalMtl = Type.UnmarshallingAppointmentsAndType(rawLocalMtl);
            HashMap<String, Type.AppointmentType> resObjectLocalQue = Type.UnmarshallingAppointmentsAndType(rawLocalQue);
            HashMap<String, Type.AppointmentType> resObjectLocalShe = Type.UnmarshallingAppointmentsAndType(rawLocalShe);

            for(String key : resObjectLocalMtl.keySet())
            {
                Type.AppointmentType value = resObjectLocalMtl.get(key);
                resObject.put(key, value);
            }
            for(String key : resObjectLocalQue.keySet())
            {
                Type.AppointmentType value = resObjectLocalQue.get(key);
                resObject.put(key, value);
            }
            for(String key : resObjectLocalShe.keySet())
            {
                Type.AppointmentType value = resObjectLocalShe.get(key);
                resObject.put(key, value);
            }
            System.out.println("Get Appointment Schedule of patient:" + patientID);
            for(String key : resObject.keySet())
            {
                Type.AppointmentType value = resObject.get(key);
            }
        } catch (IOException | NotBoundException e) {
            throw new RuntimeException(e);
        } finally {
            readwritelock.unlock();
        }
        return resObject;
    }

    public boolean CheckAppointmentAvailableLocal(String appointmentID, short appointmentType)
    {
        if(serverData.get(Type.AppointmentType.values()[appointmentType]).containsKey(appointmentID)){
            return serverData.get(Type.AppointmentType.values()[appointmentType]).get(appointmentID) > 0;
        }
        return false;
    }

    public boolean CheckAppointmentAvailableLocal(String receiveStr){
        int startIndex = 1;
        String appointmentID = receiveStr.substring(startIndex, startIndex + 10);
        startIndex += 10;
        String appointmentTypeStr = receiveStr.substring(startIndex, startIndex + 4);
        return CheckAppointmentAvailableLocal(appointmentID, Integer.valueOf(appointmentTypeStr).shortValue());
    }

    public boolean CheckAppointmentBookedLocal(String patientID, String appointmentID, short appointmentType){
        return bookingRecord.containsKey(patientID) && bookingRecord.get(patientID).containsKey(appointmentID);
    }

    public boolean CheckAppointmentBookedLocal(String receiveStr){
        int startIndex = 1;
        String patientID = receiveStr.substring(startIndex, startIndex + 8);
        startIndex += 8;
        String appointmentID = receiveStr.substring(startIndex, startIndex + 10);
        startIndex += 10;
        String appTypeStr = receiveStr.substring(startIndex, startIndex + 4);
        return CheckAppointmentBookedLocal(patientID, appointmentID, Integer.valueOf(appTypeStr).shortValue());
    }

    private boolean OperationUDP(Type.CityType cityType, String oper, String infor){
        try{
            DatagramSocket aSocket = new DatagramSocket();
            String m = oper + infor;
            InetAddress aHost = InetAddress.getByName("127.0.0.1");
            int serverPort = hospitalUDPPorts.get(cityType);
            DatagramPacket request =
                    new DatagramPacket(m.getBytes(),  m.length(), aHost, serverPort);
            aSocket.send(request);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            String responseStr = new String(reply.getData());
            System.out.println("the operation " + oper +  ":" + responseStr);
            String finalres = responseStr.substring(0, 4);
            return finalres.equals("true");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean CheckAppointmentAvailableUDP(Type.CityType cityType, String appointmentID, short appointmentType){
        String infor = appointmentID + Type.AlignStr(String.valueOf(appointmentType), 4);
        return OperationUDP(cityType, "2", infor);
    }

    class AppointmentAvaQuery implements Runnable {
        private Type.CityType type;
        private String appointmentID;
        private short appTyp;
        public boolean res = false;
        // constructor
        public AppointmentAvaQuery(Type.CityType type_, String appointmentID, short appointmentType) {
            this.type = type_;
            this.appointmentID = appointmentID;
            this.appTyp = appointmentType;
        }
        public void run() {
            res = CheckAppointmentAvailableUDP(this.type, this.appointmentID, this.appTyp);
        }
    }

    private boolean CheckAppointmentExistUDP(Type.CityType cityType, String patientID, String appointmentID, short appointmentType){
        String infor = patientID + appointmentID + Type.AlignStr(String.valueOf(appointmentType), 4);
        boolean res = OperationUDP(cityType, "3", infor);
        System.out.println("CheckAppointmentExistUDP res:" + res);
        return res;
    }

    class AppointmentExistQuery implements Runnable {
        private Type.CityType type;
        private String patientID;
        private String appointmentID;
        private short appTyp;
        public boolean res = false;
        // constructor
        public AppointmentExistQuery(Type.CityType type_, String patientID, String appointmentID, short appointmentType) {
            this.type = type_;
            this.patientID = patientID;
            this.appointmentID = appointmentID;
            this.appTyp = appointmentType;
        }
        public void run() {
            res = CheckAppointmentExistUDP(this.type, this.patientID, this.appointmentID, this.appTyp);
        }
    }

    private boolean BookAppointmentUDP(Type.CityType cityType, String patientID, String appointmentID, Type.AppointmentType appType){
        String info = patientID + appointmentID + Type.AlignStr(String.valueOf(appType), 4);
        return OperationUDP(cityType, "4", info);
    }

    class AppointmentBookQuery implements Runnable {
        private Type.CityType type;
        private String patientID;
        private String appointmentID;
        private Type.AppointmentType appTyp;
        public boolean res = false;
        // constructor
        public AppointmentBookQuery(Type.CityType type_, String patientID, String appointmentID, Type.AppointmentType appType) {
            this.type = type_;
            this.patientID = patientID;
            this.appointmentID = appointmentID;
            this.appTyp = appType;
        }
        public void run() {
            res = BookAppointmentUDP(this.type, this.patientID, this.appointmentID, this.appTyp);
        }
    }

    public short BookAppointmentUDPProcess(String receiveStr){
        int startIndex = 1;
        String patientID = receiveStr.substring(startIndex, startIndex + 8);
        startIndex += 8;
        String appointmentID = receiveStr.substring(startIndex, startIndex + 10);
        startIndex += 10;
        String appTypeStr = receiveStr.substring(startIndex, startIndex + 4);
        Type.AppointmentType appType = Type.AppointmentType.valueOf(appTypeStr);
        return BookAppointment(patientID, appointmentID, appType);
    }

    private boolean CancelAppointmentUDP(Type.CityType type, String patientID, String appointmentID)
    {
        String info = patientID + appointmentID;
        return OperationUDP(type, "5", info);
    }

    class AppointmentCancelQuery implements Runnable {
        private Type.CityType type;
        private String patientID;
        private String appointmentID;
        private Type.AppointmentType appTyp;
        public boolean res = false;
        // constructor
        public AppointmentCancelQuery(Type.CityType type_, String patientID, String appointmentID) {
            this.type = type_;
            this.patientID = patientID;
            this.appointmentID = appointmentID;
        }
        public void run() {
            res = CancelAppointmentUDP(this.type, this.patientID, this.appointmentID);
        }
    }

    public boolean CancelAppointmentUDPProcess(String receiveStr)
    {
        int startIndex = 1;
        String patientID = receiveStr.substring(startIndex, startIndex + 8);
        startIndex += 8;
        String appointmentID = receiveStr.substring(startIndex, startIndex + 10);
        startIndex += 10;
        return CancelAppointment(patientID, appointmentID);
    }

    //0 SUCCESS 1 APPOINTMENT_NOT_EXIST 2 NO_CAPACITY
    public short SwapAppointment(String patientID, String oldAppointmentID,
                                 short oldAppointmentType, String newAppointmentID, short newAppointmentType)
    {
        try{
            swapLock.lock();

            Type.AppointmentEntity oldAppEntity = new Type.AppointmentEntity();
            oldAppEntity.DeserializeAppointmentEntity(oldAppointmentID);
            Type.AppointmentEntity newAppEntity = new Type.AppointmentEntity();
            newAppEntity.DeserializeAppointmentEntity(newAppointmentID);

            Type.CityType oldCity = oldAppEntity.city;
            Type.CityType newCity = newAppEntity.city;


            AppointmentAvaQuery avaQObj = new AppointmentAvaQuery(newCity, newAppointmentID, newAppointmentType);
            AppointmentExistQuery existQObj = new AppointmentExistQuery(oldCity, patientID, oldAppointmentID, oldAppointmentType);
            AppointmentBookQuery bookQObj = new AppointmentBookQuery(newCity, patientID, newAppointmentID, Type.AppointmentType.values()[newAppointmentType]);
            AppointmentCancelQuery cancelQObj = new AppointmentCancelQuery(oldCity, patientID, oldAppointmentID);

            Thread[] threads = new Thread[4];
            threads[0] = new Thread(avaQObj);
            threads[1] = new Thread(existQObj);
            threads[2] = new Thread(bookQObj);
            threads[3] = new Thread(cancelQObj);

            threads[0].start();
            threads[1].start();
            threads[0].join();
            threads[1].join();

            boolean CheckAppointmentExistRes = existQObj.res;
            if(!CheckAppointmentExistRes)
                return 1; //the old appointment is not exist
            boolean CheckAppointmentAvaiRes = avaQObj.res;
            if(!CheckAppointmentAvaiRes)
                return 2; //the new appointment is not exist

            threads[2].start();
            threads[3].start();
            threads[2].join();
            threads[3].join();


            boolean CancelAppointmentOp = cancelQObj.res;
            boolean AddNewAppointmentOp = bookQObj.res;
            if(!(CancelAppointmentOp && AddNewAppointmentOp))
                return 3; //the swap operation is failed

            return 0;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            swapLock.unlock();
        }
    }
}
