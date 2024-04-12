package Replica1.com.example.webservice;


import javax.xml.ws.Endpoint;
import java.net.InetAddress;


public class ServerCenterCorba {
    public static void main(String args[]){
        try {
            InetAddress ip = InetAddress.getLocalHost();
            Endpoint endpoint = Endpoint.publish("http://"+ip.getHostAddress()+":8080/center", new CenterImpl());
            System.out.println("Hello service is published: " + endpoint.isPublished());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
