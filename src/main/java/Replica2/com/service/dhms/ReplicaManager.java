package Replica2.com.service.dhms;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ReplicaManager {
    static int replicaNo = 2;
    static InetAddress ip;
    static int errorTimes = 0;
    static String ipAddr = "";
    public static void main(String[] args) throws SocketException {
        // Create and initialize the actively replicated server subsystem
        Endpoint endpointMTL = null;
        Endpoint endpointQUE = null;
        Endpoint endpointSHE = null;
        try {
//            ip = InetAddress.getLocalHost();
//            endpointMTL = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/mtl", new MontrealServer());
//            endpointQUE = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/que", new QuebecServer());
//            endpointSHE = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/she", new SherbrookeServer());
//            System.out.println("Replica2 services are published at " + ip.getHostAddress());
//            ip = InetAddress.getLocalHost();
            endpointMTL = Endpoint.publish("http://" + ipAddr + ":8080/appointment/mtl", new MontrealServer());
            endpointQUE = Endpoint.publish("http://" + ipAddr + ":8080/appointment/que", new QuebecServer());
            endpointSHE = Endpoint.publish("http://" + ipAddr + ":8080/appointment/she", new SherbrookeServer());
            System.out.println("Replica2 services are published at " + ipAddr);
        } catch (Exception e) {
        }
        // Check the software failure
        DatagramSocket socket = new DatagramSocket(5010);
        System.out.println("Replica2 manager is running at port 5010");
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
                            errorTimes = 0;
                            ip = InetAddress.getLocalHost();
                            MontrealServer mtl = new MontrealServer();
                            QuebecServer que = new QuebecServer();
                            SherbrookeServer she = new SherbrookeServer();
                            endpointMTL.stop();
                            endpointQUE.stop();
                            endpointSHE.stop();
//                            Endpoint endpointMTLNew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/mtl", mtl);
//                            Endpoint endpointQUENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/que", que);
//                            Endpoint endpointSHENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/she", she);
                            Endpoint endpointMTLNew = Endpoint.publish("http://" + ipAddr + ":8080/appointment/mtl", mtl);
                            Endpoint endpointQUENew = Endpoint.publish("http://" + ipAddr + ":8080/appointment/que", que);
                            Endpoint endpointSHENew = Endpoint.publish("http://" + ipAddr + ":8080/appointment/she", she);
                            System.out.println("Replica2 starts recovery." );
                            mtl.recoverFromLog();
                            que.recoverFromLog();
                            she.recoverFromLog();
                            System.out.println("Replica2 finishes recovery." );
                        }
                    }else if (errorType.equals("Process crash")){
                        ip = InetAddress.getLocalHost();
                        MontrealServer mtl = new MontrealServer();
                        QuebecServer que = new QuebecServer();
                        SherbrookeServer she = new SherbrookeServer();
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
                                MontrealServer.main(arguments);
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        webServicesThread.start();
                        Thread webServicesThread2 = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            try {
                                QuebecServer.main(arguments);
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        webServicesThread2.start();
                        Thread webServicesThread3 = new Thread(() -> {
                            String[] arguments = new String[] {"123"};
                            try {
                                SherbrookeServer.main(arguments);
                            } catch (SocketException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        webServicesThread3.start();
                        System.out.println("Replica2 starts recovery." );
                        mtl.recoverFromLog();
                        que.recoverFromLog();
                        she.recoverFromLog();
                        System.out.println("Replica2 finishes recovery." );
                    }
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
