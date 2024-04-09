package Replica1.com.example.webservice;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.omg.PortableInterceptor.SUCCESSFUL;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@WebService(endpointInterface = "Replica1.com.example.webservice.Center")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class CenterImpl implements Center {
    public String sayHello(String name) {
        return "Hello, " + name + "!";
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
}