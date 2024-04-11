package replies;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import main.*;

import entities.Constants;

public class ReplyAppointment extends Thread{
    String portNum = "";
    public ReplyAppointment(String portNum){
        this.portNum = portNum;
    }
    @Override
    public void run (){
        try{
            DatagramSocket socket = new DatagramSocket(Integer.parseInt(portNum));
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket;
            while(true){
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                InetAddress address = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String appointmentType = new String(receivePacket.getData(), 0, receivePacket.getLength());
                Map<String, Integer> appointment;
                if (receivePacket.getData() != null){
                    switch (portNum){
                        case Constants.QUE_APPOINTMENT_PORT:
                            appointment = ServerQUE.getAppointment().get(appointmentType);
                            break;
                        case Constants.MTL_APPOINTMENT_PORT:
                            appointment = ServerMTL.getAppointment().get(appointmentType);
                            break;
                        case Constants.SHE_APPOINTMENT_PORT:
                            appointment = ServerSHE.getAppointment().get(appointmentType);
                            break;
                        default:
                            appointment = null;
                    }
                    String reply = "";
                    if(appointment == null || appointment.size() == 0){
                        reply = Constants.NOT_AVAILABLE;
                    }else{
                        reply = appointment.toString();
                    }
                    DatagramPacket replyPacket = new DatagramPacket(reply.getBytes(), reply.length(), address, port);
                    socket.send(replyPacket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
