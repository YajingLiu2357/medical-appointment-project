package replicas.Replica2.com.service.dhms;

import Replica2.com.service.dhms.Appointment;
import Replica2.com.service.dhms.Constants;
import Replica2.com.service.dhms.MontrealServer;
import Replica2.com.service.dhms.Publish;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Replica2Test {
    static Appointment mtl;
    static Appointment que;
    static Appointment she;

    @BeforeAll
    static void setUp() {
        //TODO: delete the following comments.
        // I tried to run the servers through this test class but bind error occurred.
        // You need to run the servers manually before running this test class.
//        Thread thread0 = new Thread (() -> {
//            String[] arguments = new String[] {"123"};
//            Publish.main(arguments);
//        });
//        thread0.start();
//        Thread thread1 = new Thread (() -> {
//            String[] arguments = new String[] {"123"};
//            try {
//                MontrealServer.main(arguments);
//            } catch (SocketException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        thread1.start();
//        Thread thread2 = new Thread (() -> {
//            String[] arguments = new String[] {"123"};
//            try {
//                MontrealServer.main(arguments);
//            } catch (SocketException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        thread2.start();
//        Thread thread3 = new Thread (() -> {
//            String[] arguments = new String[] {"123"};
//            try {
//                MontrealServer.main(arguments);
//            } catch (SocketException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        thread3.start();
        try {
            //TODO: edit ip address
            String ip = "localhost";
            InetAddress address = InetAddress.getByName(ip);
            URL urlMTL = new URL("http://" + ip + ":8080/appointment/mtl?wsdl");
            QName qnameMTL = new QName("http://dhms.service.com.Replica2/", "MontrealServerService");
            Service serviceMTL = Service.create(urlMTL, qnameMTL);
            mtl = serviceMTL.getPort(Appointment.class);

            URL urlQUE = new URL("http://" + ip + ":8080/appointment/que?wsdl");
            QName qnameQUE = new QName("http://dhms.service.com.Replica2/", "QuebecServerService");
            Service serviceQUE = Service.create(urlQUE, qnameQUE);
            que = serviceQUE.getPort(Appointment.class);

            URL urlSHE = new URL("http://" + ip + ":8080/appointment/she?wsdl");
            QName qnameSHE = new QName("http://dhms.service.com.Replica2/", "SherbrookeServerService");
            Service serviceSHE = Service.create(urlSHE, qnameSHE);
            she = serviceSHE.getPort(Appointment.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    void addAppointmentTest() {
        String result = mtl.addAppointment("MTLA080224", "Physician", 4);
        assertEquals(Constants.SUCCESS, result);
    }
    @Test
    void listAppointmentAvailabilityTest() {
        mtl.addAppointment("MTLA080224", "Physician", 4);
        String result = mtl.listAppointmentAvailability("Physician");
        assertEquals("{MTLA080224=4}", result);
    }
}
