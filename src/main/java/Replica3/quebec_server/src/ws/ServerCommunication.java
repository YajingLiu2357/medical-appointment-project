package Replica3.quebec_server.src.ws;

import java.io.*;
import java.net.*;
import java.util.Map;

public class ServerCommunication {
	public void send_appointment_records() {
		DatagramSocket serverSocket = null;
		try {
			// Create a UDP socket
            serverSocket = new DatagramSocket(8081);

            // Create buffer for receiving data
            byte[] receiveData = new byte[1024];

            while (true) {
            // Create UDP packet for receiving data
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // Receive data
            serverSocket.receive(receivePacket);

            // Convert received data to String
            String appointment_type = new String(receivePacket.getData(), 0, receivePacket.getLength());
            
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            
            database db = new database();
            String[] records = new String[db.appointments.get(appointment_type).values().size()];
            int i = 0;
            for (Map.Entry<String, String> ar : db.appointments.get(appointment_type).entrySet()){
                records[i] = "Appointment ID: " + ar.getKey() + " Available Slot(s): " + ar.getValue();
                i++;
            }
            
            String response = String.join(";", records);
        	byte[] sendData = response.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket); // Send response back to client
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
	}
	
	public String request_appointment_records_montreal(String appointment_type){
		String response = "";
		DatagramSocket serverSocket = null;
        try {
        	// Specify the destination address and port
            InetAddress IPAddress = InetAddress.getLocalHost();
            int port = 8080;

            // Create a UDP socket
            serverSocket = new DatagramSocket();

            // Create the message to send
            byte[] sendData = appointment_type.getBytes();

            // Create UDP packet with the message and send it
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);

            byte[] receiveData = new byte[4000];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        	serverSocket.receive(receivePacket); // Receive response from server
            response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
        
        return response;
	}
	
	public String request_appointment_records_sherbrooke(String appointment_type){
		String response = "";
		DatagramSocket serverSocket = null;
        try {
        	// Specify the destination address and port
            InetAddress IPAddress = InetAddress.getLocalHost();
            int port = 8082;

            // Create a UDP socket
            serverSocket = new DatagramSocket();

            // Create the message to send
            byte[] sendData = appointment_type.getBytes();

            // Create UDP packet with the message and send it
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);

            byte[] receiveData = new byte[4000];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        	serverSocket.receive(receivePacket); // Receive response from server
            response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
        
        return response;
	}
}
