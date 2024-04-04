package Replica1.com.example.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@WebService(endpointInterface = "Replica1.com.example.webservice.Hospital")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class HospitalImpl implements Hospital {
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }

    @Override
    public String addAppointment(String appointmentID, String appointmentType, int capacity) {
        try{
            boolean res = HospitalServer.getInstance().AddAppointment(appointmentID, Type.ExchangeStringCompatibility(appointmentType), capacity);
            String resStr = Constants.SUCCESS;
            if(!res) resStr = Constants.APPOINTMENT_ALREADY_EXISTS;
            LogSystem.getInstance().WriteStr("Add appointment", appointmentID + " " + appointmentType + " " + String.valueOf(capacity),
                    Constants.SUCCESS, resStr);
            return resStr;
        } catch (NotBoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String removeAppointment(String appointmentID, String appointmentType) {
        try{
            boolean res = HospitalServer.getInstance().RemoveAppointment(appointmentID, Type.ExchangeStringCompatibility(appointmentType));
            String resStr = Constants.SUCCESS;
            if(!res) resStr = Constants.APPOINTMENT_NOT_EXIST;
            LogSystem.getInstance().WriteStr("Remove appointment", appointmentID + " " + appointmentType,
                    Constants.SUCCESS, resStr);
            return resStr;
        } catch (NotBoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String listAppointmentAvailability(String appointmentType) {
        try{
            ConcurrentHashMap<String, Integer> res =
                    HospitalServer.getInstance().ListAppointmentAvailability(
                            Type.ExchangeStringCompatibility(appointmentType));
            LogSystem.getInstance().WriteStr("List appointment", " ",
                    Constants.SUCCESS, Constants.SUCCESS);
            return Type.MarshallingHashMapCompatibility(res);
        } catch (NotBoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
        try{
            short res = HospitalServer.getInstance().BookAppointment(patientID, appointmentID,
                    Type.ExchangeStringCompatibility(appointmentType));
            String resStr = Constants.SUCCESS;
            if(res == 1)
                resStr = Constants.APPOINTMENT_ALREADY_EXISTS;
            else if(res == 2)
                resStr = Constants.HAVE_SAME_TYPE_APPOINTMENT_SAME_DAY;
            else if(res == 3)
                resStr = Constants.THREE_APPOINTMENTS_OTHER_CITIES;
            else if(res == 4)
                resStr = Constants.NO_CAPACITY;
            LogSystem.getInstance().WriteStr("Book appointment", patientID + " " + appointmentID + " " + appointmentType,
                    Constants.SUCCESS, resStr);
            return resStr;
        } catch (NotBoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAppointmentSchedule(String patientID) {
        try{
            HashMap<String, Type.AppointmentType> res = HospitalServer.getInstance().GetAppointmentSchedule(patientID);
            System.out.println("getAppointmentSchedule debug:" + res.toString());
            LogSystem.getInstance().WriteStr("Get appointment schedule", patientID,
                    Constants.SUCCESS, Constants.SUCCESS);
            return Type.MarshallingAppointmentsAndTypeCompatibility(patientID, res);
        } catch (NotBoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String cancelAppointment(String patientID, String appointmentID) {
        try{
            boolean res =  HospitalServer.getInstance().CancelAppointment(patientID, appointmentID);
            String resStr = Constants.SUCCESS;
            if(!res) resStr = Constants.APPOINTMENT_NOT_EXIST;
            LogSystem.getInstance().WriteStr("Cancel appointment", patientID + " " + appointmentID,
                    Constants.SUCCESS, resStr);
            return resStr;
        } catch (NotBoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) {
        try{
            short oldAppIndex = Type.GetAppTypeIndex(Type.ExchangeStringCompatibility(oldAppointmentType));
            short newAppIndex = Type.GetAppTypeIndex(Type.ExchangeStringCompatibility(newAppointmentType));

            short res = HospitalServer.getInstance().SwapAppointment(patientID, oldAppointmentID,
                    oldAppIndex, newAppointmentID, newAppIndex);
            String resStr = Constants.SUCCESS;
            if(res == 1)
                resStr = Constants.APPOINTMENT_NOT_EXIST;
            else if(res == 2)
                resStr = Constants.NO_CAPACITY;
            LogSystem.getInstance().WriteStr("Swap appointment", patientID + " " + oldAppointmentID + " " + oldAppointmentType+ " " + newAppointmentID + " " + newAppointmentType,
                    Constants.SUCCESS, resStr);
            return resStr;
        } catch (NotBoundException | IOException e) {
            throw new RuntimeException(e);
        }
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
    public void recoverFromLog(String city){
        String filePath = Constants.LOG_FILE_PATH  + city + ".txt";
        try {
            // Recover from log
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String lineInFile;
            ArrayList<String> logs = new ArrayList<>();
            while ((lineInFile = bufferedReader.readLine()) != null) {
                logs.add(lineInFile);
            }
            PrintWriter writer = new PrintWriter(filePath);
            writer.print("");
            writer.close();
            for (String line : logs) {
                if (line.contains(Replica2.com.service.dhms.Constants.ADD_APPOINTMENT)){
                    String [] lineSplit = line.split(Replica2.com.service.dhms.Constants.SPACE);
                    int parameterIndex = Arrays.asList(lineSplit).indexOf("parameters:");
                    String appointmentID = lineSplit[parameterIndex + 1];
                    String appointmentType = lineSplit[parameterIndex + 2];
                    int capacity = Integer.parseInt(lineSplit[parameterIndex + 3]);
                    addAppointment(appointmentID, appointmentType, capacity);
                }else if (line.contains(Replica2.com.service.dhms.Constants.REMOVE_APPOINTMENT)){
                    String [] lineSplit = line.split(Replica2.com.service.dhms.Constants.SPACE);
                    int parameterIndex = Arrays.asList(lineSplit).indexOf("parameters:");
                    String appointmentID = lineSplit[parameterIndex + 1];
                    String appointmentType = lineSplit[parameterIndex + 2];
                    removeAppointment(appointmentID, appointmentType);
                }else if (line.contains(Replica2.com.service.dhms.Constants.BOOK_APPOINTMENT)){
                    String [] lineSplit = line.split(Replica2.com.service.dhms.Constants.SPACE);
                    int parameterIndex = Arrays.asList(lineSplit).indexOf("parameters:");
                    String patientID = lineSplit[parameterIndex + 1];
                    String appointmentID = lineSplit[parameterIndex + 2];
                    String appointmentType = lineSplit[parameterIndex + 3];
                    bookAppointment(patientID, appointmentID, appointmentType);
                }else if (line.contains(Replica2.com.service.dhms.Constants.CANCEL_APPOINTMENT)){
                    String [] lineSplit = line.split(Replica2.com.service.dhms.Constants.SPACE);
                    int parameterIndex = Arrays.asList(lineSplit).indexOf("parameters:");
                    String patientID = lineSplit[parameterIndex + 1];
                    String appointmentID = lineSplit[parameterIndex + 2];
                    cancelAppointment(patientID, appointmentID);
                }else if (line.contains(Replica2.com.service.dhms.Constants.SWAP_APPOINTMENT)){
                    String [] lineSplit = line.split(Replica2.com.service.dhms.Constants.SPACE);
                    int parameterIndex = Arrays.asList(lineSplit).indexOf("parameters:");
                    String patientID = lineSplit[parameterIndex + 1];
                    String oldAppointmentID = lineSplit[parameterIndex + 2];
                    String oldAppointmentType = lineSplit[parameterIndex + 3];
                    String newAppointmentID = lineSplit[parameterIndex + 4];
                    String newAppointmentType = lineSplit[parameterIndex + 5];
                    swapAppointment(patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
                }
            }
            fileReader.close();
            System.out.println("Finished recovering from log");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}