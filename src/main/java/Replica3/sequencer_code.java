package Replica3;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class sequencer_code {
    // Port numbers for server replicas
    private static final int[] REPLICA_PORTS = {9001, 9002, 9003, 9004};
    // IP addresses of server replicas
    private static final String[] REPLICA_IPS = {"replica1_ip", "replica2_ip", "replica3_ip", "replica4_ip"};
    private static final AtomicInteger sequenceNumber = new AtomicInteger(0);

    public static void main(String[] args) {
        //replica message variables for merging the result
        String replica1Message = "";
        String replica2Message = "";
        String replica3Message = "";
        String replica4Message = "";

        try {
            DatagramSocket sequencerSocket = new DatagramSocket(8000);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                sequencerSocket.receive(receivePacket);

                String clientRequest = new String(receivePacket.getData()).trim();
                String[] splittedClientRequest = clientRequest.split(",");

                // Assign unique sequence number
                int seqNum = sequenceNumber.getAndIncrement();

                DatagramSocket sequencerSocket = new DatagramSocket(splittedClientRequest[0]); //at index '0' of the array I can find the port number as per marshlling process

                /*
                Marshalling portion

                try {
            // Marshalling the data
            ByteArrayOutputStream bstream = new ByteArrayOutputStream(MAX_MSG_LENGTH);
            DataOutputStream dstream = new DataOutputStream(bstream);
            dstream.writeLong(++sequenceNo);
            dstream.write(msg, 0, msg.length);
            DatagramPacket message = new DatagramPacket(bstream.toByteArray(), bstream.size(), grpIP, PORT);
            multicastSock.send(message);
        } catch (Exception ex) {
            System.out.println("couldnt send message" + ex);
        }
                */

                if(splittedClientRequest[1].equals("BookAppointment")){
                    try {
                        URL url = new URL("urls?wsdl");
                        QName qName = new QName("url", "service implementation file");
                        Service service = Service.create(url, qName);
                        centralPlatform = service.getPort(Center.class);
                        String message = centralPlatform.bookAppointment();
                    }
                    catch (Exception e) {
                        System.out.println("Client exception: " + e);
                        e.printStackTrace();
                    }
                }

                //this postion need to modify but don't know where to use
                // Multicast the request to server replicas
//                for (int i = 0; i < SERVER_PORTS.length; i++) {
//                    // Format: seqNum:clientRequest
//                    String message = seqNum + ":" + clientRequest;
//                    byte[] sendData = message.getBytes();
//                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER_IPS[i]), SERVER_PORTS[i]);
//                    sequencerSocket.send(sendPacket);
//                }
                System.out.println("Request with sequence number " + seqNum + " sent to server replicas.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
