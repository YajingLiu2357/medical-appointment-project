package Replica1.com.example.webservice;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


import javax.xml.ws.Endpoint;

public class ServerHospitalMTL {
    public static void main(String args[]){
        HospitalServer.cityType = Type.CityType.MTL;

        LogSystem.getInstance().Initiate(Constants.LOG_FILE_PATH + "MTL.txt");
        // Start web services server in one thread
        Thread webServicesThread = new Thread(() -> {
            try{
                InetAddress ip = InetAddress.getLocalHost();
                Endpoint endpoint = Endpoint.publish("http://"+ip.getHostAddress()+":8081/MTL", new HospitalImpl());
                System.out.println("Hello service is published: " + endpoint.isPublished());
            }catch (Exception e) {
                e.printStackTrace();
            }
        });
        webServicesThread.start();

        // Start UDP server in another thread
        Thread udpThread = new Thread(() -> {
            try{
                DatagramSocket aSocket = new DatagramSocket(6000);
                byte[] buffer = new byte[1000];
                while(true){
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request);
                    String res = new String(buffer);
                    String operationBit = res.substring(0,1);
                    System.out.println("received:" + res);
                    String returnValue = "";

                    //process request
                    if(operationBit.equals("1")){
                        System.out.println("get 1!");
                        returnValue = HospitalServer.getInstance().MarshallingAppointmentAvaliableLocal();
                    }
                    else if(operationBit.equals("2")){ //CheckAppointmentAvailableUDP
                        System.out.println("get 2!");
                        returnValue = String.valueOf(HospitalServer.getInstance().CheckAppointmentAvailableLocal(res));
                    }
                    else if(operationBit.equals("3")){ //CheckAppointmentExistUDP
                        System.out.println("get 3!");
                        returnValue = String.valueOf(HospitalServer.getInstance().CheckAppointmentBookedLocal(res));
                    }
                    else if(operationBit.equals("4")){ //BookAppointmentUDP
                        System.out.println("get 4!");
                        returnValue = String.valueOf(HospitalServer.getInstance().BookAppointmentUDPProcess(res));
                    }
                    else if(operationBit.equals("5")){ //CancelAppointmentUDP
                        System.out.println("get 5!");
                        returnValue = String.valueOf(HospitalServer.getInstance().CancelAppointmentUDPProcess(res));
                    }

                    System.out.println("Sent Data:" + returnValue);
                    DatagramPacket reply = new DatagramPacket(returnValue.getBytes(), returnValue.length(),
                            request.getAddress(), request.getPort());
                    aSocket.send(reply);
                }
            }
            catch (Exception e) {
                System.err.println("ComputeEngine exception:");
                e.printStackTrace();
            }
        });
        udpThread.start();
    }
}
