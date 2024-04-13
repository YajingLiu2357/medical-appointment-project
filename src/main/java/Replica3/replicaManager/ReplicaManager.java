package Replica3.replicaManager;

import Replica3.servers.HospitalMTL;
import Replica3.servers.HospitalQUE;
import Replica3.servers.HospitalSHE;
import Replica3.softwareFailure.SoftwareFailure;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;



public class ReplicaManager {
    // Unique identifier for this replica
    static int replicaNo = 3;
    // IP address of this replica
    static InetAddress ip;
    static int errorTimes = 0;
    static String ipAddr = "";
    public static void main(String[] args) throws SocketException {
        // Initialize and publish the web services for Montreal, Quebec, and Sherbrooke
        // Create and initialize the actively replicated server subsystem
        Endpoint endpointMTL = null;
        Endpoint endpointQUE = null;
        Endpoint endpointSHE = null;
        try {
//            InetAddress ip = InetAddress.getLocalHost();
//            // Publish the web services with unique endpoints
//            endpointMTL = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/mtl", new HospitalMTL());
//            endpointQUE = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/que", new HospitalQUE());
//            endpointSHE = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/she", new HospitalSHE());
//            System.out.println("Replica3 services are published at " +ip.getHostAddress());
            InetAddress ip = InetAddress.getLocalHost();
            // Publish the web services with unique endpoints
            endpointMTL = Endpoint.publish("http://" + ipAddr + ":8080/appointment/mtl", new HospitalMTL());
            endpointQUE = Endpoint.publish("http://" + ipAddr + ":8080/appointment/que", new HospitalQUE());
            endpointSHE = Endpoint.publish("http://" + ipAddr + ":8080/appointment/she", new HospitalSHE());
            System.out.println("Replica3 services are published at " + ipAddr);
        } catch (Exception e) {
        }
        // Check the software failure
        DatagramSocket socket = new DatagramSocket(5010);
        System.out.println("Replica3 manager is running at port 5010");
        try{
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket;
            while(true){
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String [] data = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(" ");
                int replicaErrorNo = Integer.parseInt(data[0]);
                String errorType = data[1] + " " + data[2];
                InetAddress address = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String reply = errorType + " information received";
                DatagramPacket replyPacketBook = new DatagramPacket(reply.getBytes(), reply.length(), address, port);
                socket.send(replyPacketBook);
                if (replicaErrorNo == replicaNo){
                    if (errorType.equals("Software failure")){
                        errorTimes++;
                        if (errorTimes >= 3){
                            SoftwareFailure.changeErrorFlagData("false");
                            ip = InetAddress.getLocalHost();
                            HospitalMTL mtl = new HospitalMTL();
                            HospitalQUE que = new HospitalQUE();
                            HospitalSHE she = new HospitalSHE();
                            endpointMTL.stop();
                            endpointQUE.stop();
                            endpointSHE.stop();
//                            Endpoint endpointMTLNew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/mtl", mtl);
//                            Endpoint endpointQUENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/que", que);
//                            Endpoint endpointSHENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/she", she);
                            Endpoint endpointMTLNew = Endpoint.publish("http://" + ipAddr + ":8080/appointment/mtl", mtl);
                            Endpoint endpointQUENew = Endpoint.publish("http://" + ipAddr + ":8080/appointment/que", que);
                            Endpoint endpointSHENew = Endpoint.publish("http://" + ipAddr + ":8080/appointment/she", she);
                            System.out.println("Replica 3 starts recovery." );
                            mtl.recoverFromLog();
                            que.recoverFromLog();
                            she.recoverFromLog();
                            System.out.println("Replica3 finishes recovery." );
                        }
                    }else if (errorType.equals("Process crash")){
                        SoftwareFailure.changeErrorFlagData("false");
                        ip = InetAddress.getLocalHost();
                        HospitalMTL mtl = new HospitalMTL();
                        HospitalQUE que = new HospitalQUE();
                        HospitalSHE she = new HospitalSHE();
                        endpointMTL.stop();
                        endpointQUE.stop();
                        endpointSHE.stop();
//                        Endpoint endpointMTLNew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/mtl", mtl);
//                        Endpoint endpointQUENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/que", que);
//                        Endpoint endpointSHENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/she", she);
                        Endpoint endpointMTLNew = Endpoint.publish("http://" + ipAddr + ":8080/appointment/mtl", mtl);
                        Endpoint endpointQUENew = Endpoint.publish("http://" + ipAddr + ":8080/appointment/que", que);
                        Endpoint endpointSHENew = Endpoint.publish("http://" + ipAddr + ":8080/appointment/she", she);
                        Thread webServicesThread = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            try {
                                HospitalMTL.main(arguments);
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        webServicesThread.start();
                        Thread webServicesThread2 = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            try {
                                HospitalQUE.main(arguments);
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        webServicesThread2.start();
                        Thread webServicesThread3 = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            try {
                                HospitalSHE.main(arguments);
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        webServicesThread3.start();
                        System.out.println("Replica3 starts recovery." );
                        mtl.recoverFromLog();
                        que.recoverFromLog();
                        she.recoverFromLog();
                        System.out.println("Replica3 finishes recovery." );
                    }
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
