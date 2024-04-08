import DHMSApp.DHMS;

public class Patient extends Client{
    public Patient(String ID, DHMS server) {
        super(ID, server);
    }

    @Override
    public void addAppointment(String appointmentID, String appointmentType, int capacity) {
        printInvalidCommandMessage();
    }

    @Override
    public void removeAppointment(String appointmentID, String appointmentType) {
        printInvalidCommandMessage();
    }

    @Override
    public void listAppointmentAvailability(String appointmentType) {
        printInvalidCommandMessage();
    }

    @Override
    public void bookAppointment(String patientID, String appointmentID, String appointmentType) {
        String log = server.bookAppointment(patientID, appointmentID, appointmentType);
        writeLog(log);
    }

    @Override
    public void getAppointmentSchedule(String patientID) {
        String log = server.getAppointmentSchedule(patientID);
        writeLog(log);
    }

    @Override
    public void cancelAppointment(String patientID, String appointmentID) {
        String log = server.cancelAppointment(patientID, appointmentID);
        writeLog(log);
    }

    @Override
    public void swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) {
        String log = server.swapAppointment(patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
        writeLog(log);
    }
}
