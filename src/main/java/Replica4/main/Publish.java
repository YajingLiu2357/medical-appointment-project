package main;

import main.*;
import javax.xml.ws.Endpoint;

public class Publish {
    public static void main(String[] args){
        Endpoint endpointMTL = Endpoint.publish("http://localhost:8080/appointment/mtl", new ServerMTL());
        Endpoint endpointQUE = Endpoint.publish("http://localhost:8080/appointment/que", new ServerQUE());
        Endpoint endpointSHE = Endpoint.publish("http://localhost:8080/appointment/she", new ServerSHE());
        System.out.println("Services are published. ");
    }
}
