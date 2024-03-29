package com.example.webservice;


import javax.xml.ws.Endpoint;


public class ServerCenterCorba {
    public static void main(String args[]){
        Endpoint endpoint = Endpoint.publish("http://localhost:8080/center", new CenterImpl());
        System.out.println("Hello service is published: " + endpoint.isPublished());
    }
}
