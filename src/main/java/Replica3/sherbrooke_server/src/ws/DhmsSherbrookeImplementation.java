package Replica3.sherbrooke_server.src.ws;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService(endpointInterface="Replica3.sherbrooke_server.src.ws.DhmsSherbrooke")
@SOAPBinding(style=Style.RPC)
public class DhmsSherbrookeImplementation implements DhmsSherbrooke {
	public String hello() {
		return "Hello world";
	}
	//schedule for individual patient
    public String[] get_appointment_schedule(String patient_id) {
        database db = new database();
        String[] records = db.patients_appointment.get(patient_id);
        return records;
    }

    public String add_appointment(String appointment_id, String type, String capacity) {
        String message = "";
        Integer flag = 0;

        database db = new database();

        //checking if the appointment is already created or not
        for(Map.Entry<String, Map<String, String>> a : db.appointments.entrySet()){
            for (Map.Entry<String, String> b : a.getValue().entrySet()){
                if (b.getKey().equals(appointment_id) && a.getKey().equals(type)){
                    flag = 1;
                }
            }
        }

        if(flag.equals(1)){
            message = "Appointment is already added";
        }else {
            //storing data in hashmap
        	Map<String, String> appointment_capacity = new HashMap<>();
        	appointment_capacity.put(appointment_id, capacity);
            db.appointments.put(type, appointment_capacity);

            //creating log file and keeping records
            try {
                File log_file = new File("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt");
                if (log_file.createNewFile()) {
                    FileWriter log_file_writer = new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt");
                    log_file_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment added for " + type + " Appointment ID #" + appointment_id + " Slots of " + capacity + "\n");
                    log_file_writer.close();
                } else {
                    BufferedWriter log_file_buffered_writer = new BufferedWriter(new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt", true));
                    log_file_buffered_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment added for " + type + " Appointment ID #" + appointment_id + " Slots of " + capacity + "\n");
                    log_file_buffered_writer.close();
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            message = "Appointment added successfully";
        }

        return message;
    }

    public String book_appointment(String patient_id, String appointment_id, String type) {
        String message = "";
        String location = "";
        Integer flag = 0;

        database db = new database();

        //specifying the user's location
        location = appointment_id.substring(0, Math.min(patient_id.length(), 3));
        
        //checking if the patient already at other hospital out of city more than 3 times
        int count = 0;
        for (Map.Entry<String, String[]> a : db.patients_appointment.entrySet()){
            for (int i = 0; i < a.getValue().length; i++){
            	if(a.getKey().equals(patient_id.toUpperCase()) && location.toUpperCase().equals("MTL")){
                    if (a.getValue()[i].substring(0, Math.min(patient_id.length(), 3)).equals("QUE") || a.getValue()[i].substring(0, Math.min(patient_id.length(), 3)).equals("SHE")){
                        count++;
                    }
            	}
            	if(a.getKey().equals(patient_id.toUpperCase()) && location.toUpperCase().equals("QUE")){
                    if (a.getValue()[i].substring(0, Math.min(patient_id.length(), 3)).equals("MTL") || a.getValue()[i].substring(0, Math.min(patient_id.length(), 3)).equals("SHE")){
                        count++;
                    }
            	}
            	if(a.getKey().equals(patient_id.toUpperCase()) && location.toUpperCase().equals("SHE")){
                    if (a.getValue()[i].substring(0, Math.min(patient_id.length(), 3)).equals("QUE") || a.getValue()[i].substring(0, Math.min(patient_id.length(), 3)).equals("MTL")){
                        count++;
                    }
            	}
            }
        }

        if(count >= 3){
            message = "You've already booked 3 appointments outside of your city";
        }else{
        	//checking if the appointment is already booked or not
            for (Map.Entry<String, String[]> a : db.patients_appointment.entrySet()){
                for (Integer i = 0; i < a.getValue().length; i++){
                    if (a.getValue()[i].equals(appointment_id) && a.getKey().equals(patient_id)){
                        flag = 1;
                    }
                }
            }

            if(flag.equals(1)){
                message = "Appointment is already booked";
            }else {
                //storing data in hashmap and calculation of appointment capacity
                db.patients_appointment.put(patient_id, new String[]{appointment_id});
                for(Map.Entry<String, Map<String, String>> a : db.appointments.entrySet()){
                    for (Map.Entry<String, String> b : a.getValue().entrySet()){
                        if (b.getKey().equals(appointment_id) && a.getKey().equals(type)){
                            Integer capacity = (Integer.valueOf(b.getValue()) - 1);
                            a.getValue().replace(appointment_id, capacity.toString());
                        }
                    }
                }

                //creating log file and keeping records
                try {
                    File log_file = new File("D:\\web_services\\sherbrooke_server\\logs\\"+ patient_id +".txt");
                    if (log_file.createNewFile()) {
                        //client file writer
                        FileWriter log_file_writer = new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\"+ patient_id +".txt");
                        log_file_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment booked for patient #" + patient_id + " of "+ type +" Appointment ID #" + appointment_id  + "\n");
                        log_file_writer.close();
                        //admin file writer
                        FileWriter admin_log_file_writer = new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt");
                        admin_log_file_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment booked for patient #" + patient_id + " of "+ type +" Appointment ID #" + appointment_id  + "\n");
                        admin_log_file_writer.close();
                    } else {
                        //client file writer
                        BufferedWriter log_file_buffered_writer = new BufferedWriter(new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\"+ patient_id +".txt", true));
                        log_file_buffered_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment booked for patient #" + patient_id + " of "+ type +" Appointment ID #" + appointment_id  + "\n");
                        log_file_buffered_writer.close();
                        //admin file writer
                        BufferedWriter admin_log_file_buffered_writer = new BufferedWriter(new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt", true));
                        admin_log_file_buffered_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment booked for patient #" + patient_id + " of "+ type +" Appointment ID #" + appointment_id  + "\n");
                        admin_log_file_buffered_writer.close();
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }

                message = "Appointment booked successfully";
            }
        }

        return message;
    }

    public String remove_appointment(String appointment_id, String type) {
        String message = "";
        Integer flag = 0;

        database db = new database();

        //checking if the appointment is created or not
        for(Map.Entry<String, Map<String, String>> a : db.appointments.entrySet()){
            for (Map.Entry<String, String> b : a.getValue().entrySet()){
                if (b.getKey().equals(appointment_id) && a.getKey().equals(type)){
                    flag = 1;
                }
            }
        }

        if(flag.equals(0)){
            message = "No appointment found";
        }else {
            //removing data in hashmap
        	if(type.equals("Physician")){
        		db.physician_slot_count.remove(appointment_id);
        	}
        	if(type.equals("Dentist")){
        		db.dentist_slot_count.remove(appointment_id);
        	}
        	if(type.equals("Surgeon")){
        		db.surgeon_slot_count.remove(appointment_id);
        	}

            //creating log file and keeping records
            try {
                File log_file = new File("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt");
                if (log_file.createNewFile()) {
                    FileWriter log_file_writer = new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt");
                    log_file_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] #" + appointment_id + " " + type + " appointment removed" + "\n");
                    log_file_writer.close();
                } else {
                    BufferedWriter log_file_buffered_writer = new BufferedWriter(new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt", true));
                    log_file_buffered_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] #" + appointment_id + " " + type + " appointment removed" + "\n");
                    log_file_buffered_writer.close();
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            message = "Appointment removed successfully";
        }

        return message;
    }

    public String cancel_appointment(String appointment_id, String patient_id, String type) {
        String message = "";
        Integer flag = 0;

        database db = new database();

        //checking if the appointment is booked or not
        for (Map.Entry<String, String[]> a : db.patients_appointment.entrySet()){
            for (Integer i = 0; i < a.getValue().length; i++){
                if (a.getValue()[i].equals(appointment_id) && a.getKey().equals(patient_id)){
                    flag = 1;
                }
            }
        }

        if(flag.equals(0)){
            message = "Appointment not found";
        }else {
            //removing data in hashmap and calculation of appointment capacity
            String[] appointment_ids = new String[0];
            for (Map.Entry<String, String[]> a : db.patients_appointment.entrySet()){
                appointment_ids = new String[a.getValue().length];
                for (Integer i = 0; i < a.getValue().length; i++){
                    if (!a.getValue()[i].equals(appointment_id) && a.getKey().equals(patient_id)){
                        appointment_ids[i] = a.getValue()[i];
                    }
                }
            }
            String[] finalAppointment_ids = appointment_ids;
            db.patients_appointment.replace(patient_id, finalAppointment_ids);
            for(Map.Entry<String, Map<String, String>> a : db.appointments.entrySet()){
                for (Map.Entry<String, String> b : a.getValue().entrySet()){
                    if (b.getKey().equals(appointment_id) && a.getKey().equals(type)){
                        Integer capacity = (Integer.valueOf(b.getValue()) + 1);
                        a.getValue().replace(appointment_id, capacity.toString());
                    }
                }
            }

            //creating log file and keeping records
            try {
                File log_file = new File("D:\\web_services\\sherbrooke_server\\logs\\"+ patient_id +".txt");
                if (log_file.createNewFile()) {
                    //client file writer
                    FileWriter log_file_writer = new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\"+ patient_id +".txt");
                    log_file_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment canceled for patient #" + patient_id + " of "+ type +" Appointment ID #" + appointment_id  + "\n");
                    log_file_writer.close();
                    //admin file writer
                    FileWriter admin_log_file_writer = new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt");
                    admin_log_file_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment canceled for patient #" + patient_id + " of "+ type +" Appointment ID #" + appointment_id  + "\n");
                    admin_log_file_writer.close();
                } else {
                    //client file writer
                    BufferedWriter log_file_buffered_writer = new BufferedWriter(new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\"+ patient_id +".txt", true));
                    log_file_buffered_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment canceled for patient #" + patient_id + " of "+ type +" Appointment ID #" + appointment_id  + "\n");
                    log_file_buffered_writer.close();
                    //admin file writer
                    BufferedWriter admin_log_file_buffered_writer = new BufferedWriter(new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt", true));
                    admin_log_file_buffered_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment canceled for patient #" + patient_id + " of "+ type +" Appointment ID #" + appointment_id  + "\n");
                    admin_log_file_buffered_writer.close();
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            message = "Appointment canceled successfully";
        }

        return message;
    }

    public String[] get_appointment_records(String appointment_type) {
        ServerCommunication sc = new ServerCommunication();
        
        String records_montreal_string = sc.request_appointment_records_montreal(appointment_type);
        String[] records_montreal = records_montreal_string.split(";");
        
        String records_quebec_string = sc.request_appointment_records_quebec(appointment_type);
        String[] records_quebec = records_quebec_string.split(";");
        
        database db = new database();
    	int array_size = db.appointments.get(appointment_type).values().size() + records_quebec.length + records_montreal.length;
        String[] records = new String[array_size];
        int i = 0;
        for (Map.Entry<String, String> ar : db.appointments.get(appointment_type).entrySet()){
            records[i] = "Appointment ID: " + ar.getKey() + " Available Slot(s): " + ar.getValue();
            i++;
        }
        
        for(String record : records_montreal){
        	records[i] = record;
        	i++;
        }
        
        for(String record : records_quebec){
        	records[i] = record;
        	i++;
        }
        
        return records;
    }
    
    public String swap_appointment(String patient_id, String old_appointment_id, String old_type, String appointment_id, String type){
    	String message = "";
        String location = "";
        Integer flag = 0;

        database db = new database();
    	//checking if the appointment is already booked or not
        for (Map.Entry<String, String[]> a : db.patients_appointment.entrySet()){
            for (Integer i = 0; i < a.getValue().length; i++){
                if (a.getValue()[i].equals(appointment_id) && a.getKey().equals(patient_id)){
                    flag = 1;
                }
            }
        }

        if(flag.equals(1)){
            message = "Appointment is already booked";
        }else {
        	//removing data in hashmap
            String[] appointment_ids = new String[0];
            for (Map.Entry<String, String[]> a : db.patients_appointment.entrySet()){
                appointment_ids = new String[a.getValue().length];
                for (Integer i = 0; i < a.getValue().length; i++){
                    if (!a.getValue()[i].equals(old_appointment_id) && a.getKey().equals(patient_id)){
                        appointment_ids[i] = a.getValue()[i];
                    }
                }
            }
            
            //storing data in hashmap and calculation of appointment capacity
            db.patients_appointment.put(patient_id, new String[]{appointment_id});
            for(Map.Entry<String, Map<String, String>> a : db.appointments.entrySet()){
                for (Map.Entry<String, String> b : a.getValue().entrySet()){
                    if (b.getKey().equals(appointment_id) && a.getKey().equals(type)){
                        Integer capacity = (Integer.valueOf(b.getValue()) - 1);
                        a.getValue().replace(appointment_id, capacity.toString());
                    }
                }
            }

            //creating log file and keeping records
            try {
                File log_file = new File("D:\\web_services\\sherbrooke_server\\logs\\"+ patient_id +".txt");
                if (log_file.createNewFile()) {
                    //client file writer
                    FileWriter log_file_writer = new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\"+ patient_id +".txt");
                    log_file_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment swaped for patient #" + patient_id + " of "+ old_type +" Appointment ID #" + old_appointment_id + " to "+ type +" Appointment ID #" + appointment_id + "\n");
                    log_file_writer.close();
                    //admin file writer
                    FileWriter admin_log_file_writer = new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt");
                    admin_log_file_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment swaped for patient #" + patient_id + " of "+ old_type +" Appointment ID #" + old_appointment_id  + " to "+ type +" Appointment ID #" + appointment_id + "\n");
                    admin_log_file_writer.close();
                } else {
                    //client file writer
                    BufferedWriter log_file_buffered_writer = new BufferedWriter(new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\"+ patient_id +".txt", true));
                    log_file_buffered_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment swaped for patient #" + patient_id + " of "+ old_type +" Appointment ID #" + old_appointment_id  + " to "+ type +" Appointment ID #" + appointment_id + "\n");
                    log_file_buffered_writer.close();
                    //admin file writer
                    BufferedWriter admin_log_file_buffered_writer = new BufferedWriter(new FileWriter("D:\\web_services\\sherbrooke_server\\logs\\admin_log.txt", true));
                    admin_log_file_buffered_writer.write("[" + new Timestamp(System.currentTimeMillis()) + "] Appointment swaped for patient #" + patient_id + " of "+ old_type +" Appointment ID #" + old_appointment_id  + " to "+ type +" Appointment ID #" + appointment_id + "\n");
                    admin_log_file_buffered_writer.close();
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            message = "Appointment swaped successfully";
        }
        
    	return message;
    }

}
