package Replica3.sherbrooke_server.src.main;

import javax.xml.ws.Endpoint;

import ws.DhmsSherbrookeImplementation;
import ws.ServerCommunication;

public class sherbrookeMain {

	public static void main(String[] args) {
		try {
			DhmsSherbrookeImplementation obj = new DhmsSherbrookeImplementation();
			Endpoint ep = Endpoint.publish("http://localhost:8082/DhmsSherbrooke", obj);
			if (ep.isPublished()) {
				System.out.println("SHERBROOKE SERVER IS READY");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		ServerCommunication sc = new ServerCommunication();
		sc.send_appointment_records();
	}

}
