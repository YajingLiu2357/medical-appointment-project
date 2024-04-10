package publish;

import servers.HospitalMTL;
import servers.HospitalQUE;
import servers.HospitalSHE;

import javax.xml.ws.Endpoint;

public class Publish {
    public static void main(String[] args){
        Endpoint endpointMTL = Endpoint.publish("http://localhost:8080/appointment/mtl", new HospitalMTL());
        Endpoint endpointQUE = Endpoint.publish("http://localhost:8080/appointment/que", new HospitalQUE());
        Endpoint endpointSHE = Endpoint.publish("http://localhost:8080/appointment/she", new HospitalSHE());
        System.out.println("Services are published. ");
    }
}
