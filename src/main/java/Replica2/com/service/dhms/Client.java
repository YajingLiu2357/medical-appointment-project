package com.service.dhms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public abstract class Client {
   String ID;
   Appointment server;
    public Client(String ID, Appointment server){
        this.ID = ID;
        this.server = server;
    }
    public abstract void addAppointment(String appointmentID, String appointmentType, int capacity);
    public abstract void removeAppointment(String appointmentID, String appointmentType);
    public abstract void listAppointmentAvailability(String appointmentType);
    public abstract void bookAppointment(String patientID, String appointmentID, String appointmentType);
    public abstract void getAppointmentSchedule(String patientID);
    public abstract void cancelAppointment(String patientID, String appointmentID);
    public abstract void swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType);
    public void writeLog (String log){
        String path = "./com/service/dhms/logs/client/"+ID+".txt";
        try{
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(path, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(log);
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();
            System.out.println(log);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void printInvalidCommandMessage(){
        System.out.println("Invalid command. Please try again.");
    }
}

