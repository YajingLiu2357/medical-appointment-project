package dataReplies;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import constants.*;
import servers.HospitalMTL;
import servers.HospitalQUE;
import servers.HospitalSHE;

public class ReplyRecord extends Thread{
    String portNum = "";
    public ReplyRecord(String portNum){
        this.portNum = portNum;
    }
    @Override
    public void run (){
        try{
            DatagramSocket socket = new DatagramSocket(Integer.parseInt(portNum));
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket;
            while (true){
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                InetAddress addressRecord = receivePacket.getAddress();
                int portRecord = receivePacket.getPort();
                List<String> recordList;
                if (receivePacket.getData() != null){
                    switch (portNum){
                        case Constants.QUE_RECORD_PORT:
                            recordList = HospitalQUE.getRecordList();
                            break;
                        case Constants.MTL_RECORD_PORT:
                            recordList = HospitalMTL.getRecordList();
                            break;
                        case Constants.SHE_RECORD_PORT:
                            recordList = HospitalSHE.getRecordList();
                            break;
                        default:
                            recordList = null;
                    }
                    String replyRecord = "";
                    if(recordList.size() == 0){
                        replyRecord = Constants.NOT_AVAILABLE;
                    }else{
                        replyRecord = recordList.toString();
                    }
                    DatagramPacket replyPacketRecord = new DatagramPacket(replyRecord.getBytes(), replyRecord.length(), addressRecord, portRecord);
                    socket.send(replyPacketRecord);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
