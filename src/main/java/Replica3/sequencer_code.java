import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class Sequencer {
    // Port number for the sequencer
    private static final int PORT = 8000;
    // Port numbers for server replicas
    private static final int[] SERVER_PORTS = {9001, 9002, 9003, 9004};
    // IP address of the sequencer
    private static final String IP = "frontend_ip";
    // IP addresses of server replicas
    private static final String[] SERVER_IPS = {"server1_ip", "server2_ip", "server3_ip", "server4_ip"};
    private static final AtomicInteger sequenceNumber = new AtomicInteger(0);

    public static void main(String[] args) {
        try {
            DatagramSocket sequencerSocket = new DatagramSocket(FE_PORT);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                sequencerSocket.receive(receivePacket);

                String clientRequest = new String(receivePacket.getData()).trim();

                // Assign unique sequence number
                int seqNum = sequenceNumber.getAndIncrement();

                // Multicast the request to server replicas
                for (int i = 0; i < SERVER_PORTS.length; i++) {
                    // Format: seqNum:clientRequest
                    String message = seqNum + ":" + clientRequest;
                    byte[] sendData = message.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER_IPS[i]), SERVER_PORTS[i]);
                    sequencerSocket.send(sendPacket);
                }
                System.out.println("Request with sequence number " + seqNum + " sent to server replicas.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
