package Replica3.quebec_server.src.main;

import javax.xml.ws.Endpoint;

import ws.DhmsQuebecImplementation;
import ws.ServerCommunication;

public class quebecMain {

	public static void main(String[] args) {
		try {
			DhmsQuebecImplementation obj = new DhmsQuebecImplementation();
			Endpoint ep = Endpoint.publish("http://localhost:8081/DhmsQuebec", obj);
			if (ep.isPublished()) {
				System.out.println("QUEBEC SERVER IS READY");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		ServerCommunication sc = new ServerCommunication();
		sc.send_appointment_records();
	}

}
