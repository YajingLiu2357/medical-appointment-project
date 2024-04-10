package Replica1.com.example.webservice;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.omg.PortableInterceptor.SUCCESSFUL;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;

@WebService(endpointInterface = "Replica1.com.example.webservice.Center")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class CenterImpl implements Center {
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }

    @Override
    public void Reset(Type.CityType city) {
        try{
            CentralServer.getInstance().GetCityOpr(city).Reset();
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //compatibility interface: return the constants results to the front end
    @Override
    public String addAppointment(String appointmentID, String appointmentType, int capacity) {
        try{
            return CentralServer.getInstance().addAppointment(appointmentID, appointmentType, capacity);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String removeAppointment(String appointmentID, String appointmentType) {
        try{
            return CentralServer.getInstance().removeAppointment(appointmentID, appointmentType);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String listAppointmentAvailability(String appointmentType) {
        try{
            return CentralServer.getInstance().listAppointmentAvailability(appointmentType);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
        try{
            return CentralServer.getInstance().bookAppointment(patientID, appointmentID, appointmentType);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAppointmentSchedule(String patientID) {
        try{
            return CentralServer.getInstance().getAppointmentSchedule(patientID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String cancelAppointment(String patientID, String appointmentID) {
        try{
            return CentralServer.getInstance().cancelAppointment(patientID, appointmentID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) {
        try{
            return CentralServer.getInstance().swapAppointment(patientID,
                    oldAppointmentID, patientID, newAppointmentID, newAppointmentType);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //origin interface
    @Override
    public String RegisterUser(short cityType, short userType) {
        try{
            System.out.println("invocation in center interfalce impl:RegisterUser");
            return CentralServer.getInstance().RegisterUser(Type.CityType.values()[cityType], Type.UserType.values()[userType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean CheckUser(short city, String userID) {
        try{
            System.out.println("invocation in center interfalce impl:CheckUser");
            return CentralServer.getInstance().CheckUser(Type.CityType.values()[city], userID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    public void recoverFromLog(String city){
        String filePath = Constants.LOG_FILE_PATH  + city + ".txt";
        try {
            Reset(Type.CityType.valueOf(city));
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