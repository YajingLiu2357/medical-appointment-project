# Server Standards

## Interface

```java
public interface Appointment {
    String addAppointment(String appointmentID, String appointmentType, int capacity);
    String removeAppointment(String appointmentID, String appointmentType);
    String listAppointmentAvailability (String appointmentType);
    String bookAppointment(String patientID, String appointmentID, String appointmentType);
    String getAppointmentSchedule(String patientID);
    String cancelAppointment(String patientID, String appointmentID);
    String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType);
}
```

## Return Strings

Severs only return the execution results to the front end. 

1. Create new class `Constants` and copy the following codes.
2. Use this class to return results. e.g. `Constants.SUCCESS`

```java
public final class Constants {
  // all success requests
  public static final String SUCCESS = "success";
  // add same appointment twice
  public static final String APPOINTMENT_ALREADY_EXISTS = "fail because appointment already exists";
  // remove non-exist appointment; cancel non-exist appointment; swap non-old appointment; 
  public static final String APPOINTMENT_NOT_EXIST = "fail because appointment does not exist";
  // list appointment
  // Map toString() e.g. {MTLA080224=4, QUEA100224=1}
  // a patient cannot book a same type of appointment multiple times in a day.
  public static final String HAVE_SAME_TYPE_APPOINTMENT_SAME_DAY = "fail because patient cannot book same type of appointment on the same day";
  // a patient cannot book more than three appointments in other cities in a week.
  public static final String THREE_APPOINTMENTS_OTHER_CITIES = "fail because patient cannot book more than 3 appointments from other cities";
  // get appointment schedule
  // List<String>  toString() e.g. [MTLP0002 QUEA100224 Physician]
  // book appointment no capacity; swap new appointment no capacity
  public static final String NO_CAPACITY = "fail because appointment does not exist or no capacity";
}
```

## Logs

Log format: Time Action Parameters Request Results Response Results

e.g.

```
2024/03/12 21:22:19 Add appointment. Request parameters: MTLA080224 Physician 4 Request: success Response: success
```

```java
// Copy the following codes to the previous Constants class
public final class Constants {
  // e.g. time + Constants.ADD_APPOINTMENT + Constants.REQUEST_PARAMETERS + appointmentID + Constants.SPACE + appointmentType + Constants.SPACE + capacity + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.APPOINTMENT_ALREADY_EXISTS;
  public static final String ADD_APPOINTMENT = " Add appointment. ";
  public static final String REMOVE_APPOINTMENT = " Remove appointment. ";
  public static final String LIST_APPOINTMENT_AVAILABILITY = " List appointment availability. ";
  public static final String BOOK_APPOINTMENT = " Book appointment. ";
  public static final String CANCEL_APPOINTMENT = " Cancel appointment. ";
  public static final String SWAP_APPOINTMENT  = " Swap appointment. ";
  public static final String GET_APPOINTMENT_SCHEDULE = " Get appointment schedule. ";
  public static final String REQUEST_PARAMETERS = "Request parameters: ";
  public static final String REQUEST_SUCCESS = " Request: success ";
  public static final String RESPONSE = "Response: ";
  public static final String SPACE = " ";
  
  // e.g. log file path: replica2/logs/Montreal.txt
  public static final String LOG_FILE_PATH = "./logs/";
  public static final String MONTREAL_TXT = "Montreal.txt";
  public static final String QUEBEC_TXT = "Quebec.txt";
  public static final String SHERBROOKE_TXT = "Sherbrooke.txt";
}
```

