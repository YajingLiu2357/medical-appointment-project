package com.example.webservice;

import java.util.ArrayList;
import java.util.List;

public class ResultProcessor {

    private static ResultProcessor instance = null;

    public static ResultProcessor getInstance() {
        if(instance == null){
            instance = new ResultProcessor();
        }
        return instance;
    }

    public boolean IsValidUserName(List<Boolean> results){
        //todo: check the validation of the results

        return results.get(0);
    }

    public String BookAppointmentResultsProcess(List<String> results){
        //todo: check the validation of the results

        return results.get(0);
    }


    public String CancelAppointmentResultsProcess(List<String> results){
        //todo: check the validation of the results

        return results.get(0);
    }

    public String ViewBookedAppointmentsResultsProcess(List<String> results){
        //todo: check the validation of the results

        return results.get(0);
    }

    public String AddAppointmentResultsProcess(List<String> results){
        //todo: check the validation of the results

        return results.get(0);
    }

    public String RemoveAppointmentResultsProcess(List<String> results){
        //todo: check the validation of the results

        return results.get(0);
    }

    public String ViewAvailableAppointmentsResultsProcess(List<String> results){
        //todo: check the validation of the results

        return results.get(0);
    }

    public String SwapAppointmentResultsProcess(List<String> results){
        //todo: check the validation of the results

        return results.get(0);
    }

}
