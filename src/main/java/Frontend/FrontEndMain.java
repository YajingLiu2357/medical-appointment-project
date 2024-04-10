package com.example.webservice;

import javax.xml.ws.Endpoint;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class FrontEndMain {
    public static void main(String args[]){
        try {
            new UserInterface("Distributed Health Care Management System (DHMS) Client");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
