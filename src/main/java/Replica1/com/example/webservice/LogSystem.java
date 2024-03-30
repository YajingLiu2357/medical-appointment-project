package com.example.webservice;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Calendar;

public class LogSystem
{
    private static LogSystem instance = null;
    //Server is a singleton
    public static LogSystem getInstance() {
        if(instance == null){
            instance = new LogSystem();
        }
        return instance;
    }

    public void Initiate(String servername){fileName = servername;}
    //format example: 2024/03/12 21:22:19 Add appointment. Request parameters: MTLA080224 Physician 4 Request: success Response: success
    public String fileName = "";
    void WriteStr(String operation, String inputParams, String request, String response) throws IOException {
        try{
            Calendar cal = Calendar.getInstance();
            int date = cal.get(Calendar.DATE);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);

            String dateStr = String.valueOf(year) + "/" + Type.AlignStr(String.valueOf(month), 2) + "/" + Type.AlignStr(String.valueOf(date), 2);
            String timeStr = Type.AlignStr(String.valueOf(hour), 2) + ":" + Type.AlignStr(String.valueOf(minute), 2) + ":" + Type.AlignStr(String.valueOf(second), 2);
            String finalStr = dateStr + " " + timeStr + " " + operation + ". Request parameters: " + inputParams + " Request: " + request + " Response: " + response;

            BufferedWriter out = new BufferedWriter(new FileWriter(fileName,true));
            out.write(finalStr + "\n");
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
