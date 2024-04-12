import Replica2.com.service.dhms.Appointment;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class App {
    public static void main(String[] args) throws MalformedURLException {
        String replica3_ip = "172.30.78.209"; // Need input replica 3 ip address

        URL urlMTL = new URL("http://" + replica3_ip + ":8080/appointment/mtl?wsdl");
        QName qnameMTL = new QName("http://servers.Replica3/", "HospitalMTLService");
        Service serviceMTL = Service.create(urlMTL, qnameMTL);
        QName qnameMTL2 = new QName("http://servers.Replica3/", "HospitalMTLPort");
        Appointment replica3_mtl = serviceMTL.getPort(qnameMTL2, Appointment.class);
        System.out.println("test:" + replica3_mtl);
    }
}
