package com.service.dhms;

public class Admin extends Client{
    public Admin(String ID, Appointment server) {
        super(ID, server);
    }

    @Override
    public void addAppointment(String appointmentID, String appointmentType, int capacity) {
        String log = server.addAppointment(appointmentID, appointmentType, capacity);
        writeLog(log);
    }

    @Override
    public void removeAppointment(String appointmentID, String appointmentType) {
        String log = server.removeAppointment(appointmentID, appointmentType);
        writeLog(log);
    }

    @Override
    public void listAppointmentAvailability(String appointmentType) {
        String log = server.listAppointmentAvailability(appointmentType);
        writeLog(log);
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
