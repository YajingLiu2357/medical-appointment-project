package Frontend;

import java.util.ArrayList;
import java.util.List;

//preprocess for the inputs
public class DataProcessor {
    private static DataProcessor instance = null;

    public static DataProcessor getInstance() {
        if(instance == null){
            instance = new DataProcessor();
        }
        return instance;
    }

    public List<Object> BookAppointmentDataProcessor(String userid_, String city_, String time_, String date_, String month_, String year_, String type_)
    {
        List<Object> processedInput = new ArrayList<>();
        String appointmentID = city_ + time_ + date_ + month_ + year_;
        String exchangedAvailableAppointments = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(type_));
        processedInput.add(userid_);
        processedInput.add(appointmentID);
        processedInput.add(exchangedAvailableAppointments);
        return processedInput;
    }

    public String BookAppointmentResultsProcess(String rawInfor){
        String[] bookingResults = rawInfor.split("$");

        //todo: process the error information
        return rawInfor;
    }

    public List<Object> CancelAppointmentDataProcessor(String userID, String appointmentID)
    {
        List<Object> processedInput = new ArrayList<>();
        processedInput.add(userID);
        processedInput.add(appointmentID);
        return processedInput;
    }

    public String CancelAppointmentResultsProcess(String rawInfor){
        //todo: process the error information
        return rawInfor;
    }

    public List<Object> ViewBookedAppointmentsDataProcessor(String userID)
    {
        List<Object> processedInput = new ArrayList<>();
        processedInput.add(userID);
        return processedInput;
    }

    public String[] ViewBookedAppointmentsResultsProcessor(String rawInfor)
    {
        //todo: process the results
        String[] res = {rawInfor};



        return res;
    }

    public List<Object> AddAppointmentDataProcessor(String city_, String time_, String date_,
                                                    String month_, String year_, String type_, int capacity)
    {
        List<Object> processedInput = new ArrayList<>();
        String appointmentID = city_ + time_ + date_ + month_ + year_;
        String exchangedAvailableAppointments = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(type_));

        processedInput.add(appointmentID);
        processedInput.add(exchangedAvailableAppointments);
        processedInput.add(capacity);
        return processedInput;
    }

    public String AddAppointmentResultProcessor(String rawInfo){
        //todo: process the result
        return rawInfo;
    }

    public List<Object> RemoveAppointmentDataProcessor(String appointmentID, String type_)
    {
        List<Object> processedInput = new ArrayList<>();
        String exchangedAvailableAppointments = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(type_));
        processedInput.add(appointmentID);
        processedInput.add(exchangedAvailableAppointments);
        return processedInput;
    }

    public String RemoveAppointmentResultProcessor(String rawInfo){
        //todo: process the result
        return rawInfo;
    }

    public List<Object> ViewAvailableAppointmentsDataProcessor()
    {
        List<Object> processedInput = new ArrayList<>();
        return processedInput;
    }

    public String[] ViewAvailableAppointmentsResultsProcessor(String rawInfor)
    {
        //todo: process the results
        String[] res = {rawInfor};
        return res;
    }

    public List<Object> SwapAppointmentsDataProcessor(String cityType,
                                                               String patientID,
                                                               String oldAppointmentID,
                                                               String oldAppointmentType,
                                                               String newAppointmentID,
                                                               String newAppointmentType)
    {
        String oldAppType = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(oldAppointmentType));
        String newAppType = Type.ExchangeAppointTypeCompatibility(Type.AppointmentType.valueOf(newAppointmentType));

        List<Object> processedInput = new ArrayList<>();
        processedInput.add(patientID);
        processedInput.add(oldAppointmentID);
        processedInput.add(oldAppType);
        processedInput.add(newAppointmentID);
        processedInput.add(newAppType);

        return processedInput;
    }

    public String SwapAppointmentResultProcessor(String rawInfo){
        //todo: process the result
        return rawInfo;
    }
}
