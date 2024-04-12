package Replica3.softwareFailure;

import Replica3.constants.Constants;
import Replica3.servers.HospitalMTL;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class SoftwareFailure {
    public static void main(String[] args){
        changeErrorFlagData("false");
        while(true){
            System.out.println("Do you want to insert software failures? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if(input.equals("yes")){
                changeErrorFlagData("true");
            }else{
                changeErrorFlagData("false");
            }
        }
    }
    public static void changeErrorFlagData(String failure){
        String filePath = "./src/main/java/Replica3/dataTexts/errorFlag/errorFlag.txt";
        try{
            PrintWriter writer = new PrintWriter(filePath);
            writer.print("");
            writer.close();
            FileWriter fileWriter = new FileWriter(filePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(failure);
            bufferedWriter.close();
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static boolean isFailure() {
        String filePath = "./src/main/java/Replica3/dataTexts/errorFlag/errorFlag.txt";
        try{
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null){
                if (line.equals("true")){
                    return true;
                }
            }
            bufferedReader.close();
            fileReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
    public static String generateRandomReturn(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        System.out.println("Software failure occurred. Return " + generatedString);
        return generatedString;
    }
}
