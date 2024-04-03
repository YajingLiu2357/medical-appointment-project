package Replica2.com.service.dhms;

import javax.xml.ws.Endpoint;

public class Publish {
    public static void main(String[] args){
        Endpoint endpointMTL = Endpoint.publish("http://localhost:8080/appointment/mtl", new MontrealServer());
        Endpoint endpointQUE = Endpoint.publish("http://localhost:8080/appointment/que", new QuebecServer());
        Endpoint endpointSHE = Endpoint.publish("http://localhost:8080/appointment/she", new SherbrookeServer());
        System.out.println("Services are published. ");
    }
}
