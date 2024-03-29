package Replica2.com.service.dhms;

public final class Constants {
    private Constants(){
        throw new IllegalStateException("Constants class");
    }
    public static final String MTL = "MTL";
    public static final String QUE = "QUE";
    public static final String SHE = "SHE";

    public static final String ADD_APPOINTMENT = " Add appointment. ";
    public static final String REQUEST_PARAMETERS = "Request parameters: ";
    public static final String REQUEST_SUCCESS = " Request: success ";
    public static final String RESPONSE = "Response: ";
    public static final String SUCCESS = "success";
    public static final String APPOINTMENT_ALREADY_EXISTS = "fail because appointment already exists";
    public static final String NOT_AVAILABLE = "Not available";
    public static final String REMOVE_APPOINTMENT = " Remove appointment. ";
    public static final String NO_NEXT_APPOINTMENT = "fail because patient has no next available appointment";
    public static final String APPOINTMENT_NOT_EXIST = "fail because appointment does not exist";
    public static final String LIST_APPOINTMENT_AVAILABILITY = " List appointment availability. ";
    public static final String APPOINTMENT_TYPE_NOT_EXIST = "fail because appointment type does not exist";
    public static final String BOOK_APPOINTMENT = " Book appointment. ";
    public static final String SAME_APPOINTMENT = "fail because patient already has appointment";
    public static final String HAVE_SAME_TYPE_APPOINTMENT_SAME_DAY = "fail because patient cannot book same type of appointment on the same day";
    public static final String THREE_APPOINTMENTS_OTHER_CITIES = "fail because patient cannot book more than 3 appointments from other cities";
    public static final String NO_CAPACITY = "fail because appointment does not exist or no capacity";
    public static final String BOOK = "book";
    public static final String CANCEL = "cancel";
    public static final String RECORD = "record";
    public static final String GET_APPOINTMENT_SCHEDULE = " Get appointment schedule. ";
    public static final String NO_APPOINTMENT = "fail because patient has no appointment";
    public static final String CANCEL_APPOINTMENT = " Cancel appointment. ";
    public static final String SWAP_APPOINTMENT  = " Swap appointment. ";

    public static final String LOCALHOST = "localhost";
    public static final String QUE_APPOINTMENT_PORT = "5001";
    public static final String MTL_APPOINTMENT_PORT = "5002";
    public static final String SHE_APPOINTMENT_PORT = "5003";
    public static final String QUE_RECORD_PORT = "5004";
    public static final String MTL_RECORD_PORT = "5005";
    public static final String SHE_RECORD_PORT = "5006";
    public static final String QUE_BOOK_CANCEL_PORT = "5007";
    public static final String MTL_BOOK_CANCEL_PORT = "5008";
    public static final String SHE_BOOK_CANCEL_PORT = "5009";

    public static final String SPACE = " ";
    public static final String NEW_LINE = "\n";

    public static final String LOG_FILE_PATH = "./src/main/java/Replica2/com/service/dhms/logs/";
    public static final String DATA_APPOINTMENT = "./src/main/java/Replica2/com/service/dhms/data/appointment/";
    public static final String DATA_RECORD = "./src/main/java/Replica2/com/service/dhms/data/record/";
    public static final String MONTREAL_TXT = "Montreal.txt";
    public static final String QUEBEC_TXT = "Quebec.txt";
    public static final String SHERBROOKE_TXT = "Sherbrooke.txt";

    public static final String PHYSICIAN = "Physician";
    public static final String SURGEON = "Surgeon";
    public static final String DENTAL = "Dental";

    public static final String NAME_SERVICE = "NameService";

    public static final String TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
}
