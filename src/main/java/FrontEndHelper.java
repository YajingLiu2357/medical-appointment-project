import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FrontEndHelper {
    static List<String> results = new ArrayList<>();
    static List<String> replicaManagerIPs = new ArrayList<>();
    static List<Integer> replicaManagerPorts = new ArrayList<>();

    public static void setResults(List<String> results) {
        FrontEndHelper.results = results;
    }

    public static void setReplicaManagerIPs(List<String> replicaManagerIPs) {
        FrontEndHelper.replicaManagerIPs = replicaManagerIPs;
    }

    public static void setReplicaManagerPorts(List<Integer> replicaManagerPorts) {
        FrontEndHelper.replicaManagerPorts = replicaManagerPorts;
    }

    public static List<String> getReplicaManagerIPs() {
        return replicaManagerIPs;
    }

    public static List<Integer> getReplicaManagerPorts() {
        return replicaManagerPorts;
    }

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
            return "Majority not found";
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
            for (int i = 0; i < replicaManagerIPs.size(); i++) {
                InetAddress address = InetAddress.getByName(replicaManagerIPs.get(i));
                int portNum = replicaManagerPorts.get(i);
                notifyReplicaManager(replicaNo, errorType, address, portNum);
            }
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
            if (times.get(i) > 30000){
                notifyAllReplicaManager(i+1, "Process crash");
            }
        }
    }
    public static void inputIP(){
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < 4; i++){
            System.out.println("Enter the IP address of replica " + (i+1) + ": ");
            replicaManagerIPs.add(scanner.nextLine());
        }
    }
    public static void inputPort(){
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < 4; i++){
            System.out.println("Enter the port number of replica " + (i+1) + ": ");
            replicaManagerPorts.add(scanner.nextInt());
        }
    }
}
