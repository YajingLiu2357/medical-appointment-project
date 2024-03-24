package com.service.dhms;

import sun.security.pkcs11.wrapper.Constants;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class FrontEnd {
    // Get results from different replicas
    static List<String> results = new ArrayList<>();

    // Check software failure
    public static String getMajority(){
        String majority = "";
        int count = 0;
        for (int i = 0; i < results.size(); i++){
            int tempCount = 0;
            for (int j = 0; j < results.size(); j++){
                if (results.get(i).equals(results.get(j))){
                    tempCount++;
                }
            }
            if (tempCount > count){
                majority = results.get(i);
                count = tempCount;
            }
        }
        if (count > results.size()/2){
            return majority;
        }else{
            return "Fail";
        }
    }
    public static void checkSoftwareFailure(String majority){
        boolean allCorrect = true;
        int replicaNo=-1;
        for (int i = 0; i < results.size(); i++) {
            if (!results.get(i).equals(majority)) {
                allCorrect = false;
                replicaNo = i + 1;
                break;
            }
        }
        if (!allCorrect){
            notifyAllReplicaManager(replicaNo, "Software failure");
        }
    }
    public static void notifyAllReplicaManager(int replicaNo, String errorType){
        try {
            int portNum = 5001;
            InetAddress address = InetAddress.getLocalHost();
            notifyReplicaManager(replicaNo, errorType, address, portNum);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    public static void notifyReplicaManager(int replicaNo, String errorType, InetAddress address, int portNum){
        // Notify the replica manager
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            String sendData = replicaNo + " " + errorType;
            byte[] sendBuffer = sendData.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNum);
            socket.send(sendPacket);
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            String receiveData = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println(receiveData);
        } catch(IOException e){
            e.printStackTrace();
        } finally{
            if(socket != null){
                socket.close();
            }
        }
    }
    public static void checkProcessCrash(List<Long> times){
       for (int i = 0; i < times.size(); i++){
           if (times.get(i) > 60000){
               notifyAllReplicaManager(i+1, "Process crash");
           }
       }
    }
    public static void main(String[] args) throws InterruptedException {
        List<Long> times = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        String result1 = "Success";
        long getResult1Time = System.currentTimeMillis() - startTime;
        times.add(getResult1Time);
        results.add(result1);
        String result2 = "Success";
        long getResult2Time = System.currentTimeMillis() - startTime;
        times.add(getResult2Time);
        results.add(result2);
        String result3 = "Fail";
        long getResult3Time = System.currentTimeMillis() - startTime;
        times.add(getResult3Time);
        results.add(result3);
        long getResult4Time = System.currentTimeMillis() - startTime;
        times.add(getResult4Time);
        checkProcessCrash(times);
        String majority = getMajority();
        checkSoftwareFailure(majority);
    }
}
