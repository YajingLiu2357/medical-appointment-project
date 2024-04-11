package Replica1.com.example.webservice;

import Replica2.com.service.dhms.MontrealServer;
import Replica2.com.service.dhms.QuebecServer;
import Replica2.com.service.dhms.SherbrookeServer;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ReplicaManager {
    static int replicaNo = 1;
    static InetAddress ip;
    public static void main(String[] args) throws SocketException {
        try{
            ip = InetAddress.getLocalHost();
            System.out.println("Replica1 services are published at " + ip.getHostAddress());
        }catch (Exception e) {
        }
        DatagramSocket socket = new DatagramSocket(5010);
        System.out.println("Replica1 manager is running at port" + socket.getLocalPort());
        try{
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket;
            while(true){
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String [] data = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(" ");
                int replicaErrorNo = Integer.parseInt(data[0]);
                String errorType = data[1];
                InetAddress address = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String reply = errorType + " information received";
                DatagramPacket replyPacketBook = new DatagramPacket(reply.getBytes(), reply.length(), address, port);
                socket.send(replyPacketBook);
                if (replicaErrorNo == replicaNo){
                    if (errorType == "Software failure"){
                        ip = InetAddress.getLocalHost();
                        CenterImpl center = new CenterImpl();
                        System.out.println("Replica1 starts recovery." );
                        center.recoverFromLog("MTL");
                        center.recoverFromLog("QUE");
                        center.recoverFromLog("SHE");
                        System.out.println("Replica1 finishes recovery." );
                    } else if (errorType == "Process crash"){
                        try{
                            ip = InetAddress.getLocalHost();
                            System.out.println("Replica1 services are published at " + ip.getHostAddress());
                        }catch (Exception e) {
                        }
                        CenterImpl center = new CenterImpl();
                        Thread webServicesThread = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            ServerHospitalMTL.main(arguments);
                        });
                        webServicesThread.start();
                        Thread webServicesThread2 = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            ServerHospitalQUE.main(arguments);
                        });
                        webServicesThread2.start();
                        Thread webServicesThread3 = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            ServerHospitalSHE.main(arguments);
                        });
                        webServicesThread3.start();
                        Thread webServicesThread4 = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            ServerCenterCorba.main(arguments);
                        });
                        webServicesThread4.start();
                        System.out.println("Replica1 starts recovery." );
                        center.recoverFromLog("MTL");
                        center.recoverFromLog("QUE");
                        center.recoverFromLog("SHE");
                        System.out.println("Replica1 finishes recovery." );
                    }
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
