package com.example.webservice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class RequestProcessor {
    //sequencer port number
    int sequencerPort = 8000;
    String sequencerLink = "127.0.0.1";

    private static RequestProcessor instance = null;

    public static RequestProcessor getInstance() {
        if(instance == null){
            instance = new RequestProcessor();
        }
        return instance;
    }

    //udp operation
    private String OperationUDP(String methodInfo, String parameterInfo) {
        try{
            DatagramSocket aSocket = new DatagramSocket();
            //marshalling process: bookAppointment:userID,appointmentID,appointmentType
            String m = methodInfo + ":" + parameterInfo;
            InetAddress aHost = InetAddress.getByName(sequencerLink);
            int serverPort = sequencerPort;
            DatagramPacket request =
                    new DatagramPacket(m.getBytes(),  m.length(), aHost, serverPort);
            aSocket.send(request);

            //the reply of the sequencer
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            String responseStr = new String(reply.getData());
            System.out.println("responseStr:" + responseStr.trim());

            return responseStr.trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> SeperateReplicaReply(String reply){
        List<String> res = new ArrayList<>();
        String[] results = reply.split(":");
        for(int i =0; i < results.length; ++i){
            res.add(results[i]);
        }
        return res;
    }

    public List<String> RegisterUser(String cityValue, String userType) {
        short cityIndex = (short)Type.CityType.valueOf(cityValue).ordinal();
        short userIndex = (short)Type.UserType.valueOf(userType).ordinal();
        String results = OperationUDP("RegisterUser", cityIndex + "," + userIndex);
        return SeperateReplicaReply(results);
    }

    public List<String> IsValidUserName(String userID) {
        Type.UserEntity userEntity1 = new Type.UserEntity();
        userEntity1.DeserializeUser(userID);
        String results = OperationUDP("IsValidUserName", userEntity1.city.ordinal() + "," + userID);
        return SeperateReplicaReply(results);
    }

    public List<String> BookAppointment(String userid_, String appointmentID, String exchangedAvailableAppointments) {
        String results = OperationUDP("BookAppointment", userid_+"," + appointmentID+","+exchangedAvailableAppointments);
        return SeperateReplicaReply(results);
    }

    public List<String> CancelAppointment(String userID, String appointmentID) {
        String results = OperationUDP("CancelAppointment", userID+"," + appointmentID);
        return SeperateReplicaReply(results);
    }

    public List<String> ViewBookedAppointments(String userID) {
        String results = OperationUDP("ViewBookedAppointments", userID);
        return SeperateReplicaReply(results);
    }

    public List<String> AddAppointment(String appointmentID, String exchangedAvailableAppointments, String capacity) {
        String results = OperationUDP("AddAppointment", appointmentID+"," + exchangedAvailableAppointments + "," + capacity);
        return SeperateReplicaReply(results);
    }

    public List<String> RemoveAppointment(String appointmentID, String exchangedAvailableAppointments){
        String results = OperationUDP("RemoveAppointment", appointmentID+"," + exchangedAvailableAppointments);
        return SeperateReplicaReply(results);
    }

    public List<String> ViewAvailableAppointments() {
        String results = OperationUDP("ViewAvailableAppointments", "");
        return SeperateReplicaReply(results);
    }

    public List<String> SwapAppointment(String patientID, String oldAppointmentID, String oldAppType, String newAppointmentID, String newAppType)
    {
        String results = OperationUDP("SwapAppointment", patientID+","+oldAppointmentID+","+oldAppType+","+newAppointmentID+","+newAppType);
        return SeperateReplicaReply(results);
    }
}
