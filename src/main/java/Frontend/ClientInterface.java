package com.example.webservice;


import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class ClientInterface {
    public String userID;

    private static ClientInterface instance = null;

    public static ClientInterface getInstance() {
        if(instance == null){
            instance = new ClientInterface();
        }
        return instance;
    }

    public void Initialize(String cityValue_, String userType_) {
        List<String> rawResults = RequestProcessor.getInstance().RegisterUser(cityValue_, userType_);
        String result = ResultProcessor.getInstance().RegisterUserResultsProcess(rawResults);
        userID = result;
    }

    public void Initialize(String ID){
        this.userID = ID;
    }

    public boolean IsValidUserName(String userID) {
        List<String> rawResults = RequestProcessor.getInstance().IsValidUserName(userID);
        boolean result = ResultProcessor.getInstance().IsValidUserName(rawResults);
        return result;
    }

    public boolean IsPatient(){
        Type.UserEntity userInstance = new Type.UserEntity();
        userInstance.DeserializeUser(userID);
        return userInstance.user == Type.UserType.P;
    }

    private ClientInterface() {
    }

    public String BookAppointment(String userid_, String city_, String time_, String date_, String month_, String year_, String type_) {

        List<Object> processedInput = DataProcessor.getInstance().BookAppointmentDataProcessor(userid_, city_, time_, date_, month_, year_, type_);
        List<String> rawResults = RequestProcessor.getInstance().BookAppointment((String) processedInput.get(0), (String) processedInput.get(1), (String) processedInput.get(2));
        String result = ResultProcessor.getInstance().BookAppointmentResultsProcess(rawResults);
        String processedResult = DataProcessor.getInstance().BookAppointmentResultsProcess(result);
        return processedResult;
    }

    public String CancelAppointment(String appointmentID)  {
        List<Object> processedInput = DataProcessor.getInstance().CancelAppointmentDataProcessor(userID, appointmentID);
        List<String> rawResults = RequestProcessor.getInstance().CancelAppointment((String) processedInput.get(0), (String) processedInput.get(1));
        String result = ResultProcessor.getInstance().BookAppointmentResultsProcess(rawResults);
        String processedResult = DataProcessor.getInstance().BookAppointmentResultsProcess(result);
        return processedResult;
    }

    public String[] ViewBookedAppointments() {
        List<Object> processedInput = DataProcessor.getInstance().ViewBookedAppointmentsDataProcessor(userID);
        List<String> rawResults = RequestProcessor.getInstance().ViewBookedAppointments((String) processedInput.get(0));
        String result = ResultProcessor.getInstance().ViewBookedAppointmentsResultsProcess(rawResults);
        String[] processedResult = DataProcessor.getInstance().ViewBookedAppointmentsResultsProcessor(result);
        return processedResult;
    }

    public String AddAppointment(String city_, String time_, String date_, String month_, String year_, String type_, int capacity)
    {
        List<Object> processedInput = DataProcessor.getInstance().AddAppointmentDataProcessor(city_, time_, date_, month_, year_, type_, capacity);
        System.out.println("processedInput:" + processedInput.get(0) + " " + processedInput.get(1) + " " +processedInput.get(2));
        List<String> rawResults = RequestProcessor.getInstance().AddAppointment((String) processedInput.get(0), (String) processedInput.get(1), processedInput.get(2).toString());
        String result = ResultProcessor.getInstance().AddAppointmentResultsProcess(rawResults);
        String processedResult = DataProcessor.getInstance().AddAppointmentResultProcessor(result);
        return processedResult;
    }

    public String RemoveAppointment(String appointmentID, String type_) {
        List<Object> processedInput = DataProcessor.getInstance().RemoveAppointmentDataProcessor(appointmentID, type_);
        List<String> rawResults = RequestProcessor.getInstance().RemoveAppointment((String) processedInput.get(0), (String) processedInput.get(1));
        String result = ResultProcessor.getInstance().RemoveAppointmentResultsProcess(rawResults);
        String processedResult = DataProcessor.getInstance().RemoveAppointmentResultProcessor(result);
        return processedResult;
    }

    public String[] ViewAvailableAppointments() {
        List<String> rawResults = RequestProcessor.getInstance().ViewAvailableAppointments();
        String result = ResultProcessor.getInstance().ViewAvailableAppointmentsResultsProcess(rawResults);
        String[] processedResult = DataProcessor.getInstance().ViewAvailableAppointmentsResultsProcessor(result);
        return processedResult;
    }

    public String SwapAppointment(String cityType, String patientID, String oldAppointmentID, String oldAppointmentType,
                                  String newAppointmentID, String newAppointmentType)
    {
        List<Object> processedInput = DataProcessor.getInstance().SwapAppointmentsDataProcessor(cityType, patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
        List<String> rawResults = RequestProcessor.getInstance().SwapAppointment((String) processedInput.get(0), (String) processedInput.get(1),
                (String) processedInput.get(2), (String) processedInput.get(3),
                (String) processedInput.get(4));
        String result = ResultProcessor.getInstance().SwapAppointmentResultsProcess(rawResults);
        String processedResult = DataProcessor.getInstance().SwapAppointmentResultProcessor(result);
        return processedResult;
    }
}
