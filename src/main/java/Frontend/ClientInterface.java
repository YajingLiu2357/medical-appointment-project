package com.example.webservice;


import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientInterface {
    private static ClientInterface instance = null;

    public static ClientInterface getInstance() {
        if(instance == null){
            instance = new ClientInterface();
        }
        return instance;
    }

    private ClientInterface() {
    }

    public String BookAppointment(String userid_, String city_, String time_, String date_, String month_, String year_, String type_) {
        String appointmentID = city_ + time_ + date_ + month_ + year_;
        String exchangedAvailableAppointments = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(type_));
        String res = RequestProcessor.getInstance().BookAppointment(userid_, appointmentID, exchangedAvailableAppointments);

        return res;
    }

    public String CancelAppointment(String appointmentID) throws RemoteException, NotBoundException {
        String res = centralPlatform.cancelAppointment(userID, appointmentID);
        try{
            clientLog.WriteStr("Cancel Appointment Operation ID:" + appointmentID + " Res:" + String.valueOf(res));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return res;
    }

    public String[] ViewBookedAppointments() throws RemoteException, InterruptedException, NotBoundException {
        String rawRes = centralPlatform.getAppointmentSchedule(userID);
        System.out.println("UnmarshallingAppointmentsAndType:" + rawRes);
        String[] ret = new String[1];
        ret[0] = rawRes;
        return ret;
    }

    public String AddAppointment(String city_, String time_, String date_, String month_, String year_, String type_, int capacity) throws RemoteException, NotBoundException {
        String appointmentID = city_ + time_ + date_ + month_ + year_;

        String exchangedAvailableAppointments = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(type_));
        String res = centralPlatform.addAppointment(appointmentID, exchangedAvailableAppointments, capacity);
        try{
            clientLog.WriteStr("Add Appointment Operation ID:" + appointmentID + " appointment Type:" + type_ +  " Capacity:" + capacity + "  Res:" + String.valueOf(res));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return res;
    }

    public String RemoveAppointment(String appointmentID, String type_) throws RemoteException, NotBoundException {
        String exchangedAvailableAppointments = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(type_));
        String res = centralPlatform.removeAppointment(appointmentID, exchangedAvailableAppointments);
        try{
            clientLog.WriteStr("Remove Appointment Operation ID:" + appointmentID + " appointment Type:" + type_ +  "  Res:" + String.valueOf(res));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        return res;
    }

    public String[] ViewAvailableAppointments() throws RemoteException, InterruptedException, NotBoundException {
        String exchangedAvailableAppointments = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.PHYS);
        String rawValiableRes = centralPlatform.listAppointmentAvailability(exchangedAvailableAppointments);
        rawValiableRes = rawValiableRes.substring(1, rawValiableRes.length()-1);
        String[] temp1;
        String delimeter1 = ", ";
        temp1 = rawValiableRes.split(delimeter1); // 分割字符串
        for(String x :  temp1){
            System.out.println(x);
        }
        System.out.println("rawValiableRes:" + rawValiableRes);
        return temp1;
    }

    public String SwapAppointment(String cityType, String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType)
    {
        String oldAppType = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(oldAppointmentType));
        String newAppType = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(newAppointmentType));
        return centralPlatform.swapAppointment(patientID, oldAppointmentID, oldAppType,
                newAppointmentID, newAppType);
    }


}
