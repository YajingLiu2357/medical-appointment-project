package com.example.webservice;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.omg.PortableInterceptor.SUCCESSFUL;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@WebService(endpointInterface = "com.example.webservice.Center")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class CenterImpl implements Center {
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }

    //compatibility interface: return the constants results to the front end
    @Override
    public String addAppointment(String appointmentID, String appointmentType, int capacity) {
        try{
            Type.AppointmentEntity appEntity = new Type.AppointmentEntity();
            appEntity.DeserializeAppointmentEntity(appointmentID);
            //todo: compatibility the appointment type
            Type.AppointmentType at = Type.AppointmentType.valueOf(appointmentType);
            boolean res = CentralServer.getInstance().addAppointment(appEntity.city,
                    appointmentID,
                    at,
                    capacity);

            if(res)
                return Constants.SUCCESS;
            return Constants.APPOINTMENT_ALREADY_EXISTS;
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String removeAppointment(String appointmentID, String appointmentType) {
        try{
            Type.AppointmentEntity appEntity = new Type.AppointmentEntity();
            appEntity.DeserializeAppointmentEntity(appointmentID);
            //todo: compatibility the appointment type
            Type.AppointmentType at = Type.AppointmentType.valueOf(appointmentType);
            boolean res = CentralServer.getInstance().RemoveAppointment(appEntity.city, appointmentID, at);
            if(res)
                return Constants.SUCCESS;
            return Constants.APPOINTMENT_NOT_EXIST;
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String listAppointmentAvailability(String appointmentType) {
        try{
            return CentralServer.getInstance().ListAppointmentAvailabilityMarshallingCompatibility(Type.CityType.values()[0],
                    Type.AppointmentType.valueOf(appointmentType));
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
        try{
            Type.UserEntity userInstance = new Type.UserEntity();
            userInstance.DeserializeUser(patientID);
            Type.AppointmentEntity appInstance = new Type.AppointmentEntity();
            appInstance.DeserializeAppointmentEntity(appointmentID);

            short res = CentralServer.getInstance().BookAppointment(appInstance.city,
                    patientID,
                    appointmentID,
                    Type.AppointmentType.valueOf(appointmentType));
            if(res == 1)
                return Constants.APPOINTMENT_ALREADY_EXISTS;
            else if(res == 2)
                return Constants.HAVE_SAME_TYPE_APPOINTMENT_SAME_DAY;
            else if(res == 3)
                return Constants.THREE_APPOINTMENTS_OTHER_CITIES;
            else if(res == 4)
                return Constants.NO_CAPACITY;
            return Constants.SUCCESS;
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAppointmentSchedule(String patientID) {
        try{
            Type.UserEntity userInstance = new Type.UserEntity();
            userInstance.DeserializeUser(patientID);
            return CentralServer.getInstance().GetAppointmentScheduleMarshallingCompatibility(userInstance.city,
                    patientID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String cancelAppointment(String patientID, String appointmentID) {
        try{
            Type.AppointmentEntity appInstance = new Type.AppointmentEntity();
            appInstance.DeserializeAppointmentEntity(appointmentID);
            boolean res = CentralServer.getInstance().CancelAppointment(appInstance.city,
                    patientID, appointmentID);
            if(res)
                return Constants.SUCCESS;
            return Constants.APPOINTMENT_NOT_EXIST;
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) {
        try{
            Type.AppointmentEntity appInstance = new Type.AppointmentEntity();
            appInstance.DeserializeAppointmentEntity(oldAppointmentID);

            short oldAppIndex = Type.GetAppTypeIndex(oldAppointmentType);
            short newAppIndex = Type.GetAppTypeIndex(newAppointmentType);

            short res = CentralServer.getInstance().SwapAppointment(appInstance.city.toString(), patientID,
                    oldAppointmentID, oldAppIndex, newAppointmentID, newAppIndex);

            //0 SUCCESS 1 APPOINTMENT_NOT_EXIST 2 NO_CAPACITY
            if(res == 1)
                return Constants.APPOINTMENT_NOT_EXIST;
            else if(res == 2)
                return Constants.NO_CAPACITY;
            return Constants.SUCCESS;
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

    @Override
    public boolean BookAppointment(short city, String userID, String appointmentID, short appointmentType) {
        try{
            short res = CentralServer.getInstance().BookAppointment(Type.CityType.values()[city], userID, appointmentID,
                    Type.AppointmentType.values()[appointmentType]);
            if(res == 0) return true;
            return false;
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean CancelAppointment(short city, String userID, String appointmentID) {
        try{
            return CentralServer.getInstance().CancelAppointment(Type.CityType.values()[city],
                    userID, appointmentID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String GetAppointmentSchedule(short city, String userID) {
        try{
            return CentralServer.getInstance().GetAppointmentScheduleMarshalling(Type.CityType.values()[city],
                    userID);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public short SwapAppointment(short CityType, String patientID, String oldAppointmentID, short oldAppointmentType, String newAppointmentID, short newAppointmentType) {
        try{
            return CentralServer.getInstance().SwapAppointment(Type.CityType.values()[CityType].toString(), patientID,
                    oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean AddAppointment(short cityType, String appointmentID, short appointmentType, short capacity) {
        try{
            return CentralServer.getInstance().addAppointment(Type.CityType.values()[cityType],
                    appointmentID,
                    Type.AppointmentType.values()[appointmentType],
                    capacity);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean RemoveAppointment(short cityType, String appointmentID, short appointmentType) {
        try{
            return CentralServer.getInstance().RemoveAppointment(Type.CityType.values()[cityType],
                    appointmentID,
                    Type.AppointmentType.values()[appointmentType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String ListAppointmentAvailability(short cityType, short appointmentType) {
        try{
            return CentralServer.getInstance().ListAppointmentAvailabilityMarshalling(Type.CityType.values()[cityType],
                    Type.AppointmentType.values()[appointmentType]);
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}