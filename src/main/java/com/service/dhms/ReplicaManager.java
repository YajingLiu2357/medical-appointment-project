package com.service.dhms;

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
        try {
            ip = InetAddress.getLocalHost();
            Endpoint endpoint = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/mtl", new MontrealServer());
        } catch (Exception e) {
        }
        // Check the software failure
        DatagramSocket socket = new DatagramSocket(5001);
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
                    MontrealServer server = new MontrealServer();
                    Endpoint endpoint = Endpoint.publish("http://" + ip.getHostAddress() + ":8080/appointment/mtl", server);
                    server.recoverFromLog();
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }

    }
}
