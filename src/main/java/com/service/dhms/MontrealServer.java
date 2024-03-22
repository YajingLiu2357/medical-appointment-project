package com.service.dhms;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.io.BufferedReader;
import java.io.FileReader;

@WebService(endpointInterface = "com.service.dhms.Appointment")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class MontrealServer implements Appointment{
    @Override
    public void hello() {
        System.out.println("Hello from Montreal");
    }
    public void recoverFromLog(){
        String filePath = "src/main/java/com/service/dhms/mtl.txt";
        try {
            // Recover from log
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("Hello")){
                    hello();
                }
            }
            fileReader.close();
            System.out.println("Finished recovering from log");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
