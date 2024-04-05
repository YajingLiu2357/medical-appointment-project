package com.example.webservice;

public class DataProcessor {
    private static DataProcessor instance = null;

    public static DataProcessor getInstance() {
        if(instance == null){
            instance = new DataProcessor();
        }
        return instance;
    }







}
