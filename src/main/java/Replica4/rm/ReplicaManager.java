package Replica4.rm;

import Replica4.main.ServerMTL;
import Replica4.main.ServerQUE;
import Replica4.main.ServerSHE;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ReplicaManager {
    // Unique identifier for this replica
    static int replicaNo = 4;
    // IP address of this replica
    static InetAddress ip;
    static int errorTimes = 0;
    public static void main(String[] args) throws SocketException {
        // Initialize and publish the web services for Montreal, Quebec, and Sherbrooke
        // Create and initialize the actively replicated server subsystem
        Endpoint endpointMTL = null;
        Endpoint endpointQUE = null;
        Endpoint endpointSHE = null;
        try {
            // Get the IP address of the local host
            InetAddress ip = InetAddress.getLocalHost();
            // Publish the web services with unique endpoints
            endpointMTL = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/mtl", new ServerMTL());
            endpointQUE = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/que", new ServerQUE());
            endpointSHE = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/she", new ServerSHE());
            System.out.println("Replica 4 services are published at " + ip.getHostAddress());
        } catch (Exception e) {
        }
        // Check the software failure
        DatagramSocket socket = new DatagramSocket(5010);
        System.out.println("Replica 4 manager is running at port 5010");
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
                        if (errorTimes >= 3) {
                            errorTimes = 0;
                            ip = InetAddress.getLocalHost();
                            ServerMTL mtl = new ServerMTL();
                            ServerQUE que = new ServerQUE();
                            ServerSHE she = new ServerSHE();
                            endpointMTL.stop();
                            endpointQUE.stop();
                            endpointSHE.stop();
                            Endpoint endpointMTLNew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/mtl", mtl);
                            Endpoint endpointQUENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/que", que);
                            Endpoint endpointSHENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/she", she);
                            System.out.println("Replica4 starts recovery." );
                            mtl.recoverFromLog();
                            que.recoverFromLog();
                            she.recoverFromLog();
                            System.out.println("Replica4 finishes recovery." );
                        }
                    }else if (errorType.equals("Process crash")){
                        ip = InetAddress.getLocalHost();
                        ServerMTL mtl = new ServerMTL();
                        ServerQUE que = new ServerQUE();
                        ServerSHE she = new ServerSHE();
                        endpointMTL.stop();
                        endpointQUE.stop();
                        endpointSHE.stop();
                        Endpoint endpointMTLNew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/mtl", mtl);
                        Endpoint endpointQUENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/que", que);
                        Endpoint endpointSHENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/she", she);
                        Thread webServicesThread = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            try {
                                ServerMTL.main(arguments);
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        webServicesThread.start();
                        Thread webServicesThread2 = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            try {
                                ServerQUE.main(arguments);
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        webServicesThread2.start();
                        Thread webServicesThread3 = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            try {
                                ServerSHE.main(arguments);
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        webServicesThread3.start();
                        System.out.println("Replica4 starts recovery." );
                        mtl.recoverFromLog();
                        que.recoverFromLog();
                        she.recoverFromLog();
                        System.out.println("Replica4 finishes recovery." );
                    }
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
