package Replica3.montreal_server.src.main;

import Replica3.montreal_server.src.ws.DhmsMontrealImplementation;
import Replica3.montreal_server.src.ws.ServerCommunication;

import javax.xml.ws.Endpoint;

public class montrealMain {

	public static void main(String[] args) {
		try {
			DhmsMontrealImplementation obj = new DhmsMontrealImplementation();
			Endpoint ep = Endpoint.publish("http://localhost:8080/DhmsMontreal", obj);
			if (ep.isPublished()) {
				System.out.println("MONTREAL SERVER IS READY");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		ServerCommunication sc = new ServerCommunication();
		sc.send_appointment_records();
	}

}
