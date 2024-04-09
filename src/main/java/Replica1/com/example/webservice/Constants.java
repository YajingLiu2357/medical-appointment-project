package Replica1.com.example.webservice;

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

    public static final String LOG_FILE_PATH = "./src/main/java/Replica1/com/example/webservice/log/";
    public static final String MONTREAL_TXT = "Montreal.txt";
    public static final String QUEBEC_TXT = "Quebec.txt";
    public static final String SHERBROOKE_TXT = "Sherbrooke.txt";
}