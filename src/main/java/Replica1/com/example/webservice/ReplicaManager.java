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
        // Create and initialize the actively replicated server subsystem
        Endpoint endpointCenter = null;
        Endpoint endpointMTL = null;
        Endpoint endpointQUE = null;
        Endpoint endpointSHE = null;
        try {
            ip = InetAddress.getLocalHost();
            endpointCenter = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/center", new CenterImpl());
            endpointMTL = Endpoint.publish("http://" + ip.getHostAddress() + ":8081/MTL", new HospitalImpl());
            endpointQUE = Endpoint.publish("http://" + ip.getHostAddress() + ":8082/QUE", new HospitalImpl());
            endpointSHE = Endpoint.publish("http://" + ip.getHostAddress() + ":8083/SHE", new HospitalImpl());
            System.out.println("Replica1 services are published at " + ip.getHostAddress());
        } catch (Exception e) {
        }
        // Check the software failure
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
                    ip = InetAddress.getLocalHost();
                    HospitalImpl mtl = new HospitalImpl();
                    HospitalImpl que = new HospitalImpl();
                    HospitalImpl she = new HospitalImpl();
                    endpointCenter.stop();
                    endpointMTL.stop();
                    endpointQUE.stop();
                    endpointSHE.stop();
                    Endpoint endpointCenterNew = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/center", new CenterImpl());
                    Endpoint endpointMTLNew = Endpoint.publish("http://" + ip.getHostAddress() + ":8081/MTL", mtl);
                    Endpoint endpointQUENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8082/QUE", que);
                    Endpoint endpointSHENew = Endpoint.publish("http://" + ip.getHostAddress() + ":8083/SHE", she);
                    System.out.println("Replica2 recovers services." );
                    mtl.recoverFromLog("Montreal");
                    que.recoverFromLog("Quebec");
                    she.recoverFromLog("Sherbrooke");
                    System.out.println("Replica2 finishes recovery." );
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
