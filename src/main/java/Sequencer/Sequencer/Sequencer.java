package Sequencer;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Sequencer {
    // Port numbers for server replicas
    private static final int[] REPLICA_PORTS = {9001, 9002, 9003, 9004};
    // IP addresses of server replicas
    private static final String[] REPLICA_IPS = {"replica1_ip", "replica2_ip", "replica3_ip", "replica4_ip"};
    private static final AtomicInteger sequenceNumber = new AtomicInteger(0);

    private static Sequencer instance = null;
    public static Sequencer Instance() {
        if(instance == null){
            instance = new Sequencer();
        }
        return instance;
    }

    public Center replica1;

    private Sequencer() {
        try {
            //todo: make clear about the replica 1, 2, 3, 4
            URL url = new URL("http://localhost:8080/center?wsdl");
            QName qName = new QName("http://webservice.example.com/", "CenterImplService");
            Service service = Service.create(url, qName);
            replica1 = service.getPort(Center.class);
        }
        catch (Exception e) {
            System.out.println("Client exception: " + e);
            e.printStackTrace();
        }
    }

    //ip:port:bookAppointment:userID,appointmentID,appointmentType
    public List<String> UnmarshallingFrontEndRequest(String requestStr)
    {
        List<String> results = new ArrayList<>();
        String[] splittedRequest = requestStr.split(":");
        String[] splittedParams = splittedRequest[3].split(",");
        for(int i =0; i <splittedRequest.length - 1;++i){
            results.add(splittedRequest[i]);
        }
        for(int i=0; i<splittedParams.length; ++i){
            results.add(splittedParams[i]);
        }
        return results;
    }

    public static void main(String[] args) {

        try {
            DatagramSocket sequencerSocket = new DatagramSocket(8000);
            while (true) {
                //receive the data from the front-end
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                sequencerSocket.receive(receivePacket);
                String frontendRequest = new String(receivePacket.getData()).trim();
                List<String> requestInformations = Sequencer.Instance().UnmarshallingFrontEndRequest(frontendRequest);

                String frontEndIP = requestInformations.get(0);
                String frontEndPort = requestInformations.get(1);
                String frontEndMethod = requestInformations.get(2);

                // Assign unique sequence number
                int seqNum = sequenceNumber.getAndIncrement();

                String resCode = "";
                if(frontEndMethod.equals("BookAppointment")){
                    //todo: replica 1, 2, 3, 4
                    String replica1Res =  Sequencer.Instance().replica1.bookAppointment(requestInformations.get(3),
                            requestInformations.get(4), requestInformations.get(5));
                    resCode = replica1Res + "$" + replica1Res + "$" + replica1Res + "$" + replica1Res;
                }
                else if(frontEndMethod.equals("CancelAppointment")){
                    String replica1Res = Sequencer.Instance().replica1.cancelAppointment(requestInformations.get(3),
                            requestInformations.get(4));
                    resCode = replica1Res + "$" + replica1Res + "$" + replica1Res + "$" + replica1Res;
                }
                else if(frontEndMethod.equals("ViewBookedAppointments")){
                    String replica1Res = Sequencer.Instance().replica1.getAppointmentSchedule(requestInformations.get(3));
                    resCode = replica1Res + "$" + replica1Res + "$" + replica1Res + "$" + replica1Res;
                }
                else if(frontEndMethod.equals("AddAppointment")){
                    String replica1Res = Sequencer.Instance().replica1.addAppointment(requestInformations.get(3),
                            requestInformations.get(4), Integer.parseInt(requestInformations.get(5)));
                    resCode = replica1Res + "$" + replica1Res + "$" + replica1Res + "$" + replica1Res;
                }
                else if(frontEndMethod.equals("RemoveAppointment")){
                    String replica1Res = Sequencer.Instance().replica1.removeAppointment(
                            requestInformations.get(3),
                            requestInformations.get(4)
                    );
                    resCode = replica1Res + "$" + replica1Res + "$" + replica1Res + "$" + replica1Res;
                }
                else if(frontEndMethod.equals("ViewAvailableAppointments")){
                    String replica1Res = Sequencer.Instance().replica1.removeAppointment(
                            requestInformations.get(3),
                            requestInformations.get(4)
                    );
                    resCode = replica1Res + "$" + replica1Res + "$" + replica1Res + "$" + replica1Res;
                }
                else if(frontEndMethod.equals("SwapAppointment")){
                    String replica1Res = Sequencer.Instance().replica1.swapAppointment(
                            requestInformations.get(3),
                            requestInformations.get(4),
                            requestInformations.get(5),
                            requestInformations.get(6),
                            requestInformations.get(7)
                    );
                    resCode = replica1Res + "$" + replica1Res + "$" + replica1Res + "$" + replica1Res;
                }

                System.out.println("Sent Data:" + resCode);


                DatagramPacket reply = new DatagramPacket(resCode.getBytes(), resCode.length(),
                        receivePacket.getAddress(), receivePacket.getPort());
                sequencerSocket.send(reply);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
