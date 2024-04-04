package Replica3.quebec_server.src.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class database{
    //appointment map declaration
    //slot counts
    Map<String, String> physician_slot_count = new HashMap<String, String>(){
        {
            put("MTLM010324","7");
            put("MTLA010324","5");
            put("MTLE010324","4");
            put("QUEM010324","9");
            put("QUEA010324","2");
            put("QUEE010324","6");
            put("SHEM010324","2");
            put("SHEA010324","7");
            put("SHEE010324","3");
        }
    };
    Map<String, String> surgeon_slot_count = new HashMap<String, String>(){
        {
            put("MTLM020324","7");
            put("MTLA020324","1");
            put("MTLE020324","4");
            put("QUEM020324","9");
            put("QUEA020324","2");
            put("QUEE020324","6");
            put("SHEM020324","2");
            put("SHEA020324","7");
            put("SHEE020324","3");
        }
    };
    Map<String, String> dentist_slot_count = new HashMap<String, String>(){
        {
            put("MTLM030324","7");
            put("MTLA030324","5");
            put("MTLE030324","4");
            put("QUEM030324","9");
            put("QUEA030324","2");
            put("QUEE030324","6");
            put("SHEM030324","2");
            put("SHEA030324","0");
            put("SHEE030324","3");
        }
    };

    Map<String, Map<String, String>> appointments = new HashMap<String, Map<String, String>>(){
        {
            put("Physician",physician_slot_count);
            put("Surgeon",surgeon_slot_count);
            put("Dentist",dentist_slot_count);
        }
    };

    //appointment records by patient
    Map<String, String[]> patients_appointment = new HashMap<String, String[]>(){
        {
            put("MTLP0001", new String[]{"MTLM010224","MTLM120124","MTLM010324","SHEM010324","QUEE220224","SHEE220324"});
        }
    };
}