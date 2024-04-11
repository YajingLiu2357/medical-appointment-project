package Replica3.servers;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import Replica3.constants.Constants;
import Replica3.dataReplies.ReplyAppointment;
import Replica3.dataReplies.ReplyRecord;
import Replica3.webService.HospitalWS;

@WebService(endpointInterface = "Replica3.webService.HospitalWS")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class HospitalSHE implements HospitalWS {
    private ConcurrentHashMap <String, ConcurrentHashMap<String, Integer>> appointmentOuter;
    private List<String> recordList;
    private List<String> recordOtherCities;
    public HospitalSHE(){
        appointmentOuter = new ConcurrentHashMap<>();
        recordList = Collections.synchronizedList(new LinkedList<>());
        recordOtherCities = Collections.synchronizedList(new LinkedList<>());
        changeAppointmentData();
        changeRecordData();
    }

    @Override
    public String addAppointment(String appointmentID, String appointmentType, int capacity) {
        String time = getTime();
        ConcurrentHashMap<String, Integer> appointmentInner = appointmentOuter.get(appointmentType);
        String log = "";
        String returnVal = "";
        if (appointmentInner != null && appointmentInner.containsKey(appointmentID)){
            log = time + Constants.ADD_APPOINTMENT + Constants.REQUEST_PARAMETERS + appointmentID + Constants.SPACE + appointmentType + Constants.SPACE + capacity + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.APPOINTMENT_ALREADY_EXISTS;
            returnVal = Constants.APPOINTMENT_ALREADY_EXISTS;
        }else{
            if (appointmentInner == null){
                appointmentInner = new ConcurrentHashMap<>();
                appointmentInner.put(appointmentID, capacity);
                appointmentOuter.put(appointmentType, appointmentInner);
            }
            else{
                appointmentInner.put(appointmentID, capacity);
            }
            changeAppointmentData();
            log = time + Constants.ADD_APPOINTMENT + Constants.REQUEST_PARAMETERS + appointmentID + Constants.SPACE + appointmentType + Constants.SPACE + capacity + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.SUCCESS;
            returnVal = Constants.SUCCESS;
        }
        writeLog(log);
        return returnVal;
    }

    @Override
    public String removeAppointment(String appointmentID, String appointmentType) {
        String time = getTime();
        ConcurrentHashMap<String, Integer> appointmentInner = appointmentOuter.get(appointmentType);
        String log = "";
        String returnVal = "";
        if(appointmentInner != null && appointmentInner.containsKey(appointmentID)){
            for (String record : recordList){
                String [] recordSplit = record.split(Constants.SPACE);
                if(recordSplit[1].equals(appointmentID)){
                    String patientID = recordSplit[0];
                    String response = getNextAppointment(appointmentType, appointmentID);
                    if (!response.equals(Constants.NOT_AVAILABLE)){
                        recordList.remove(record);
                        bookAppointment(patientID, response, appointmentType);
                        appointmentInner.remove(appointmentID);
                        changeAppointmentData();
                        log = time + Constants.REMOVE_APPOINTMENT + Constants.REQUEST_PARAMETERS + appointmentID + Constants.SPACE + appointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.SUCCESS;
                        returnVal = Constants.SUCCESS;
                        if (appointmentInner.size() == 0){
                            appointmentOuter.remove(appointmentType);
                        }
                        changeAppointmentData();
                        writeLog(log);
                        return returnVal;
                    }else{
                        log = time + Constants.REMOVE_APPOINTMENT + Constants.REQUEST_PARAMETERS + appointmentID + Constants.SPACE + appointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.NO_NEXT_APPOINTMENT;
                        returnVal = Constants.NO_NEXT_APPOINTMENT;
                        writeLog(log);
                        return returnVal;
                    }
                }
            }
            appointmentInner.remove(appointmentID);
            changeAppointmentData();
            log = time + Constants.REMOVE_APPOINTMENT + Constants.REQUEST_PARAMETERS + appointmentID + Constants.SPACE + appointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.SUCCESS;
            returnVal = Constants.SUCCESS;
            if (appointmentInner.size() == 0){
                appointmentOuter.remove(appointmentType);
            }
            changeAppointmentData();
        }else{
            log = time + Constants.REMOVE_APPOINTMENT + Constants.REQUEST_PARAMETERS + appointmentID + Constants.SPACE + appointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.APPOINTMENT_NOT_EXIST;
            returnVal = Constants.APPOINTMENT_NOT_EXIST;
        }
        writeLog(log);
        return returnVal;
    }

    @Override
    public String listAppointmentAvailability(String appointmentType) {
        String time = getTime();
        ConcurrentHashMap<String, Integer> appointmentInner = appointmentOuter.get(appointmentType);
        String log = "";
        String returnVal = "";
        Map<String, Integer> appointmentAll = new HashMap<>();
        if(appointmentInner != null){
            appointmentAll.putAll(appointmentInner);
        }
        Map<String, Integer> queAppointment = getOtherAppointment(appointmentType, Constants.QUE_APPOINTMENT_PORT);
        if(queAppointment != null){
            appointmentAll.putAll(queAppointment);
        }
        Map<String, Integer> mtlAppointment = getOtherAppointment(appointmentType, Constants.MTL_APPOINTMENT_PORT);
        if(mtlAppointment != null){
            appointmentAll.putAll(mtlAppointment);
        }
        if(appointmentAll.size()!=0){
            log = time + Constants.LIST_APPOINTMENT_AVAILABILITY + Constants.REQUEST_PARAMETERS + appointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + appointmentAll.toString();
            returnVal = appointmentAll.toString();
        }else{
            log = time + Constants.LIST_APPOINTMENT_AVAILABILITY + Constants.REQUEST_PARAMETERS + appointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.APPOINTMENT_TYPE_NOT_EXIST;
            returnVal = Constants.APPOINTMENT_TYPE_NOT_EXIST;
        }
        writeLog(log);
        return returnVal;
    }

    @Override
    public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
        if (appointmentID.startsWith(Constants.SHE)){
            String time = getTime();
            ConcurrentHashMap<String, Integer> appointmentInner = appointmentOuter.get(appointmentType);
            String log = "";
            String returnVal = "";
            List<String>  recordAllList = getAllRecordList();
            if(appointmentInner != null && appointmentInner.containsKey(appointmentID) && appointmentInner.get(appointmentID) > 0){
                for (String record : recordAllList){
                    String [] recordSplit = record.split(Constants.SPACE);
                    if(recordSplit[0].equals(patientID) && recordSplit[1].equals(appointmentID)){
                        log = time + Constants.BOOK_APPOINTMENT + Constants.REQUEST_PARAMETERS + patientID + Constants.SPACE + appointmentID + Constants.SPACE + appointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.SAME_APPOINTMENT;
                        returnVal = Constants.SAME_APPOINTMENT;
                        writeLog(log);
                        return returnVal;
                    }
                    if(recordSplit[0].equals(patientID) && recordSplit[2].equals(appointmentType) && recordSplit[1].substring(4,10).equals(appointmentID.substring(4,10))){
                        log = time + Constants.BOOK_APPOINTMENT + Constants.REQUEST_PARAMETERS + patientID + Constants.SPACE + appointmentID + Constants.SPACE + appointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.HAVE_SAME_TYPE_APPOINTMENT_SAME_DAY;
                        returnVal = Constants.HAVE_SAME_TYPE_APPOINTMENT_SAME_DAY;
                        writeLog(log);
                        return returnVal;
                    }
                    if(!recordSplit[0].substring(0, 3).equals(recordSplit[1].substring(0,3))){
                        recordOtherCities.add(record);
                    }
                }
                if (recordOtherCities.size() > 3){
                    if (checkThreeOtherAppointment(patientID)){
                        log = time + Constants.BOOK_APPOINTMENT + Constants.REQUEST_PARAMETERS + patientID + Constants.SPACE + appointmentID + Constants.SPACE  + appointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.THREE_APPOINTMENTS_OTHER_CITIES;
                        returnVal = Constants.THREE_APPOINTMENTS_OTHER_CITIES;
                        writeLog(log);
                        return returnVal;
                    }
                }
                appointmentInner.put(appointmentID, appointmentInner.get(appointmentID) - 1);
                String bookRecord = patientID + Constants.SPACE + appointmentID + Constants.SPACE + appointmentType;
                recordList.add(bookRecord);
                log = time + Constants.BOOK_APPOINTMENT + Constants.REQUEST_PARAMETERS + bookRecord + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.SUCCESS;
                returnVal = Constants.SUCCESS;
                changeAppointmentData();
                changeRecordData();
            }else{
                log = time + Constants.BOOK_APPOINTMENT + Constants.REQUEST_PARAMETERS + patientID + Constants.SPACE + appointmentID + Constants.SPACE + appointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.NO_CAPACITY;
                returnVal = Constants.NO_CAPACITY;
            }
            writeLog(log);
            return returnVal;
        }else{
            String serverName = appointmentID.substring(0,3);
            if (serverName.equals(Constants.QUE)){
                return bookCancelOtherAppointment(patientID, appointmentID, appointmentType, Constants.QUE_BOOK_CANCEL_PORT, Constants.BOOK);
            }else if (serverName.equals(Constants.MTL)){
                return bookCancelOtherAppointment(patientID, appointmentID, appointmentType, Constants.MTL_BOOK_CANCEL_PORT, Constants.BOOK);
            }
            return null;
        }
    }

    @Override
    public String getAppointmentSchedule(String patientID) {
        String returnVal = "";
        String time = getTime();
        List<String> schedule = new LinkedList<>();
        List<String> recordAllList = getAllRecordList();
        for (String record : recordAllList){
            String [] recordSplit = record.split(Constants.SPACE);
            if(recordSplit[0].equals(patientID)){
                schedule.add(record);
            }
        }
        String log = "";
        if(schedule.size() > 0){
            log = time + Constants.GET_APPOINTMENT_SCHEDULE + Constants.REQUEST_PARAMETERS + patientID + Constants.REQUEST_SUCCESS + Constants.RESPONSE + schedule.toString();
            returnVal = schedule.toString();
        }else{
            log = time + Constants.GET_APPOINTMENT_SCHEDULE + Constants.REQUEST_PARAMETERS + patientID + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.NO_APPOINTMENT;
            returnVal = Constants.NO_APPOINTMENT;
        }
        writeLog(log);
        return returnVal;
    }

    @Override
    public String cancelAppointment(String patientID, String appointmentID) {
        if (appointmentID.startsWith(Constants.SHE)){
            String time = getTime();
            String log = "";
            String returnVal = "";
            for (String record : recordList){
                String [] recordSplit = record.split(" ");
                if(recordSplit[0].equals(patientID) && recordSplit[1].equals(appointmentID)){
                    recordList.remove(record);
                    appointmentOuter.get(recordSplit[2]).put(appointmentID, appointmentOuter.get(recordSplit[2]).get(appointmentID) + 1);
                    log = time + Constants.CANCEL_APPOINTMENT + Constants.REQUEST_PARAMETERS + patientID + Constants.SPACE + appointmentID + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.SUCCESS;
                    returnVal = Constants.SUCCESS;
                    changeAppointmentData();
                    changeRecordData();
                    writeLog(log);
                    return returnVal;
                }
            }
            log = time + Constants.CANCEL_APPOINTMENT + Constants.REQUEST_PARAMETERS + patientID + Constants.SPACE + appointmentID + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.APPOINTMENT_NOT_EXIST;
            returnVal = Constants.APPOINTMENT_NOT_EXIST;
            writeLog(log);
            return returnVal;
        }else{
            String serverName = appointmentID.substring(0,3);
            if (serverName.equals(Constants.QUE)){
                return bookCancelOtherAppointment(patientID, appointmentID, null, Constants.QUE_BOOK_CANCEL_PORT, Constants.CANCEL);
            }else if (serverName.equals(Constants.MTL)){
                return bookCancelOtherAppointment(patientID, appointmentID, null, Constants.MTL_BOOK_CANCEL_PORT, Constants.CANCEL);
            }
            return null;
        }
    }

    @Override
    public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) {
        boolean oldAppointmentExist = false;
        boolean newAppointmentAvailable = false;
        String time = getTime();
        String log = "";
        String returnVal = "";
        for (String record : recordList){
            String [] recordSplit = record.split(Constants.SPACE);
            if (recordSplit[0].equals(patientID) && recordSplit[1].equals(oldAppointmentID) && recordSplit[2].equals(oldAppointmentType)){
                oldAppointmentExist = true;
                break;
            }
        }
        if (!oldAppointmentExist){
            log = time + Constants.SWAP_APPOINTMENT + Constants.REQUEST_PARAMETERS + patientID + Constants.SPACE + oldAppointmentID + Constants.SPACE + oldAppointmentType + Constants.SPACE + newAppointmentID + Constants.SPACE + newAppointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.APPOINTMENT_NOT_EXIST;
            returnVal = Constants.APPOINTMENT_NOT_EXIST;
            writeLog(log);
            return returnVal;
        }
        String serverName = newAppointmentID.substring(0,3);
        if (serverName.equals(Constants.QUE)){
            Map<String, Integer> queAppointment = getOtherAppointment(newAppointmentType, Constants.QUE_APPOINTMENT_PORT);
            if (queAppointment.size() != 0 && queAppointment.containsKey(newAppointmentID) && queAppointment.get(newAppointmentID) > 0){
                newAppointmentAvailable = true;
            }
        }else if (serverName.equals(Constants.MTL)){
            Map<String, Integer> sheAppointment = getOtherAppointment(newAppointmentType, Constants.MTL_APPOINTMENT_PORT);
            if (sheAppointment.size() != 0 && sheAppointment.containsKey(newAppointmentID) && sheAppointment.get(newAppointmentID) > 0){
                newAppointmentAvailable = true;
            }
        }
        if (!newAppointmentAvailable){
            log = time + Constants.SWAP_APPOINTMENT + Constants.REQUEST_PARAMETERS + patientID + Constants.SPACE + oldAppointmentID + Constants.SPACE + oldAppointmentType + Constants.SPACE + newAppointmentID + Constants.SPACE + newAppointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.NO_CAPACITY;
            returnVal = Constants.NO_CAPACITY;
            writeLog(log);
            return returnVal;
        }
        String logBook = bookAppointment(patientID, newAppointmentID, newAppointmentType);
        if (!logBook.contains(Constants.NO_CAPACITY)){
            String logCancel = cancelAppointment(patientID, oldAppointmentID);
            if (!logCancel.contains(Constants.APPOINTMENT_NOT_EXIST)){
                log = time + Constants.SWAP_APPOINTMENT + Constants.REQUEST_PARAMETERS + patientID + Constants.SPACE + oldAppointmentID + Constants.SPACE + oldAppointmentType + Constants.SPACE + newAppointmentID + Constants.SPACE + newAppointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.SUCCESS;
                returnVal = Constants.SUCCESS;
            }else{
                cancelAppointment(patientID, newAppointmentID);
                log = time + Constants.SWAP_APPOINTMENT + Constants.REQUEST_PARAMETERS + patientID + Constants.SPACE + oldAppointmentID + Constants.SPACE + oldAppointmentType + Constants.SPACE + newAppointmentID + Constants.SPACE + newAppointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.APPOINTMENT_NOT_EXIST;
                returnVal = Constants.APPOINTMENT_NOT_EXIST;
            }
        }else{
            log = time + Constants.SWAP_APPOINTMENT + Constants.REQUEST_PARAMETERS + patientID + Constants.SPACE + oldAppointmentID + Constants.SPACE + oldAppointmentType + Constants.SPACE + newAppointmentID + Constants.SPACE + newAppointmentType + Constants.REQUEST_SUCCESS + Constants.RESPONSE + Constants.NO_CAPACITY;
            returnVal = Constants.NO_CAPACITY;
        }
        writeLog(log);
        return returnVal;
    }
    public void changeRecordData(){
        String filePath = Constants.DATA_RECORD + Constants.SHERBROOKE_TXT;
        try{
            PrintWriter writer = new PrintWriter(filePath);
            writer.print("");
            writer.close();
            FileWriter fileWriter = new FileWriter(filePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (String record : recordList){
                bufferedWriter.write(record);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void changeAppointmentData(){
        String filePath = Constants.DATA_APPOINTMENT + Constants.SHERBROOKE_TXT;
        try{
            PrintWriter writer = new PrintWriter(filePath);
            writer.print("");
            writer.close();
            FileWriter fileWriter = new FileWriter(filePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (String appointmentType : appointmentOuter.keySet()){
                for (String appointmentID : appointmentOuter.get(appointmentType).keySet()){
                    bufferedWriter.write(appointmentType + " " + appointmentID + " " + appointmentOuter.get(appointmentType).get(appointmentID));
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public String getTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Constants.TIME_FORMAT);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    public void writeLog (String log){
        String path = Constants.LOG_FILE_PATH + Constants.SHERBROOKE_TXT;
        try{
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(path, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(log);
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();
            System.out.println(log);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public String getNextAppointment(String appointmentType, String appointmentID){
        ConcurrentHashMap<String, Integer> appointmentInner = appointmentOuter.get(appointmentType);
        if(appointmentInner != null){
            for (String key : appointmentInner.keySet()){
                char slot = key.charAt(3);
                int day = Integer.parseInt(key.substring(4,6));
                int month = Integer.parseInt(key.substring(6,8));
                int year = Integer.parseInt(key.substring(8,10));
                char previousSlot = appointmentID.charAt(3);
                int previousDay = Integer.parseInt(appointmentID.substring(4,6));
                int previousMonth = Integer.parseInt(appointmentID.substring(6,8));
                int previousYear = Integer.parseInt(appointmentID.substring(8,10));
                if ((day == previousDay && month == previousMonth && year == previousYear) && ((previousSlot == 'M' && (slot == 'A'|| slot == 'E')||(previousSlot == 'A' && (slot == 'E'))))){
                    return key;
                }else if (day > previousDay && month == previousMonth && year == previousYear){
                    return key;
                }else if (month > previousMonth && year == previousYear){
                    return key;
                }else if (year > previousYear){
                    return key;
                }else{
                    return Constants.NOT_AVAILABLE;
                }
            }
        }
        return Constants.NOT_AVAILABLE;
    }
    public Map<String, Integer> getOtherAppointment(String appointmentType, String portNum){
        Map<String, Integer> appointmentOther = new HashMap<>();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(Constants.LOCALHOST);
            byte[] sendBuffer = appointmentType.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, Integer.parseInt(portNum));
            socket.send(sendPacket);
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            String receiveData = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if (!receiveData.equals(Constants.NOT_AVAILABLE)){
                String receiveDataTrim = receiveData.replaceAll("[{}\\s]", "");
                String [] appointments = receiveDataTrim.split(",");
                for (String appointment : appointments){
                    String [] appointmentSplit = appointment.split("=");
                    appointmentOther.put(appointmentSplit[0], Integer.parseInt(appointmentSplit[1]));
                }
            }
            return appointmentOther;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(socket != null){
                socket.close();
            }
        }
    }
    public String bookCancelOtherAppointment (String patientID, String appointmentID, String appointmentType, String portNum, String bookCancel){
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(Constants.LOCALHOST);
            String sendData = bookCancel + Constants.SPACE + patientID + Constants.SPACE + appointmentID + Constants.SPACE + appointmentType;
            byte[] sendBuffer = sendData.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, Integer.parseInt(portNum));
            socket.send(sendPacket);
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            String receiveData = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if (!receiveData.equals(Constants.NOT_AVAILABLE)) {
                return receiveData;
            }
        } catch(IOException e){
            e.printStackTrace();
        } finally{
            if(socket != null){
                socket.close();
            }
        }
        return null;
    }
    public boolean checkThreeOtherAppointment(String patientID){
        int earliestDay = Integer.parseInt(recordOtherCities.get(0).split(Constants.SPACE)[1].substring(4,6));
        int earliestMonth = Integer.parseInt(recordOtherCities.get(0).split(Constants.SPACE)[1].substring(6,8));
        int earliestYear = 2000 + Integer.parseInt(recordOtherCities.get(0).split(Constants.SPACE)[1].substring(8,10));
        Calendar earliestDate = Calendar.getInstance();
        earliestDate.set(earliestYear, earliestMonth - 1 , earliestDay, 0, 0);
        for (String record : recordOtherCities){
            String [] recordSplit = record.split(Constants.SPACE);
            if (recordSplit[0].equals(patientID)){
                int day = Integer.parseInt(recordSplit[1].substring(4,6));
                int month = Integer.parseInt(recordSplit[1].substring(6,8));
                int year = Integer.parseInt(recordSplit[1].substring(8,10));
                if (year < earliestYear){
                    earliestYear = year;
                    earliestMonth = month;
                    earliestDay = day;
                }else if (year == earliestYear && month < earliestMonth){
                    earliestMonth = month;
                    earliestDay = day;
                }else if (year == earliestYear && month == earliestMonth && day < earliestDay){
                    earliestDay = day;
                }
            }
        }
        int count = 0;
        for (String record : recordOtherCities){
            String [] recordSplit = record.split(Constants.SPACE);
            if (recordSplit[0].equals(patientID)){
                int day = Integer.parseInt(recordSplit[1].substring(4,6));
                int month = Integer.parseInt(recordSplit[1].substring(6,8));
                int year = Integer.parseInt(recordSplit[1].substring(8,10));
                Calendar tempDate = Calendar.getInstance();
                tempDate.set(year, month - 1 , day, 0, 0);
                long diff = tempDate.getTimeInMillis() - earliestDate.getTimeInMillis();
                long diffDays = diff / (24 * 60 * 60 * 1000);
                if (diffDays <= 7){
                    count++;
                }
            }
        }
        if (count > 3){
            return true;
        }
        return false;
    }
    public List<String> getAllRecordList(){
        List<String> recordListAll = new LinkedList<>();
        if (recordList != null && recordList.size() > 0){
            recordListAll.addAll(recordList);
        }
        List<String> queRecord = getOtherRecord(Constants.QUE_RECORD_PORT);
        if (queRecord != null && queRecord.size() > 0){
            recordListAll.addAll(queRecord);
        }
        List<String> mtlRecord = getOtherRecord(Constants.MTL_RECORD_PORT);
        if (mtlRecord != null && mtlRecord.size() > 0){
            recordListAll.addAll(mtlRecord);
        }
        return recordListAll;
    }
    public List<String> getOtherRecord(String portNum){
        List<String> recordListOther = new LinkedList<>();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(Constants.LOCALHOST);
            byte[] sendBuffer = Constants.RECORD.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, Integer.parseInt(portNum));
            socket.send(sendPacket);
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            String receiveData = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if (!receiveData.equals(Constants.NOT_AVAILABLE)) {
                String receiveDataTrim = receiveData.replaceAll("\\[", "").replaceAll("\\]", "");
                String [] records = receiveDataTrim.split(",");
                for (String record : records){
                    recordListOther.add(record.replaceFirst("^\\s*", ""));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(socket != null){
                socket.close();
            }
        }
        return recordListOther;
    }
    public static List<String> getRecordList(){
        String filePath = Constants.DATA_RECORD + Constants.SHERBROOKE_TXT;
        List<String> recordList = new ArrayList<>();
        try{
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.equals("")){
                recordList.add(line);
            }
            bufferedReader.close();
            fileReader.close();
            return recordList;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public static Map<String, Map<String, Integer>> getAppointment() {
        String filePath = Constants.DATA_APPOINTMENT + Constants.SHERBROOKE_TXT;
        Map <String, Map<String, Integer>> appointment = new HashMap<>();
        Map <String, Integer> physician = new HashMap<>();
        Map <String, Integer> surgeon = new HashMap<>();
        Map <String, Integer> dental = new HashMap<>();
        appointment.put(Constants.PHYSICIAN, physician);
        appointment.put(Constants.SURGEON, surgeon);
        appointment.put(Constants.DENTAL, dental);
        try{
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.equals("")){
                String [] lineSplit = line.split(Constants.SPACE);
                String appointmentType = lineSplit[0];
                String appointmentID = lineSplit[1];
                int capacity = Integer.parseInt(lineSplit[2]);
                appointment.get(appointmentType).put(appointmentID, capacity);
            }
            bufferedReader.close();
            fileReader.close();
            return appointment;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) throws SocketException {
        ReplyAppointment replyAppointment = new ReplyAppointment(Constants.SHE_APPOINTMENT_PORT);
        replyAppointment.start();
        ReplyRecord replyRecord = new ReplyRecord(Constants.SHE_RECORD_PORT);
        replyRecord.start();
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(Constants.SHE_BOOK_CANCEL_PORT));
        try{
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket;
            while(true){
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                InetAddress addressBook = receivePacket.getAddress();
                int portBook = receivePacket.getPort();
                String [] bookData = new String(receivePacket.getData(),0, receivePacket.getLength()).split(Constants.SPACE);
                String bookCancel = bookData[0];
                String patientID = bookData[1];
                String appointmentID = bookData[2];
                URL url = new URL("http://localhost:8080/appointment/she?wsdl");
                QName qname = new QName("http://dhms.service.com/", "SherbrookeServerService");
                Service service = Service.create(url, qname);
                HospitalWS she = service.getPort(HospitalWS.class);
                if (bookCancel.equals(Constants.BOOK)){
                    String appointmentType = bookData[3];
                    String log = she.bookAppointment(patientID, appointmentID, appointmentType);
                    DatagramPacket replyPacketBook = new DatagramPacket(log.getBytes(), log.length(), addressBook, portBook);
                    socket.send(replyPacketBook);
                }else if (bookCancel.equals(Constants.CANCEL)){
                    String log = she.cancelAppointment(patientID, appointmentID);
                    DatagramPacket replyPacketBook = new DatagramPacket(log.getBytes(), log.length(), addressBook, portBook);
                    socket.send(replyPacketBook);
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    public void recoverFromLog(){
        String filePath = Constants.LOG_FILE_PATH + Constants.SHERBROOKE_TXT;
        try {
            // Recover from log
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String lineInFile;
            ArrayList<String> logs = new ArrayList<>();
            while ((lineInFile = bufferedReader.readLine()) != null) {
                logs.add(lineInFile);
            }
            PrintWriter writer = new PrintWriter(filePath);
            writer.print("");
            writer.close();
            for (String line : logs) {
                if (line.contains(Constants.ADD_APPOINTMENT)){
                    String [] lineSplit = line.split(Constants.SPACE);
                    int parameterIndex = Arrays.asList(lineSplit).indexOf("parameters:");
                    String appointmentID = lineSplit[parameterIndex + 1];
                    String appointmentType = lineSplit[parameterIndex + 2];
                    int capacity = Integer.parseInt(lineSplit[parameterIndex + 3]);
                    addAppointment(appointmentID, appointmentType, capacity);
                }else if (line.contains(Constants.REMOVE_APPOINTMENT)){
                    String [] lineSplit = line.split(Constants.SPACE);
                    int parameterIndex = Arrays.asList(lineSplit).indexOf("parameters:");
                    String appointmentID = lineSplit[parameterIndex + 1];
                    String appointmentType = lineSplit[parameterIndex + 2];
                    removeAppointment(appointmentID, appointmentType);
                }else if (line.contains(Constants.BOOK_APPOINTMENT)){
                    String [] lineSplit = line.split(Constants.SPACE);
                    int parameterIndex = Arrays.asList(lineSplit).indexOf("parameters:");
                    String patientID = lineSplit[parameterIndex + 1];
                    String appointmentID = lineSplit[parameterIndex + 2];
                    String appointmentType = lineSplit[parameterIndex + 3];
                    bookAppointment(patientID, appointmentID, appointmentType);
                }else if (line.contains(Constants.CANCEL_APPOINTMENT)){
                    String [] lineSplit = line.split(Constants.SPACE);
                    int parameterIndex = Arrays.asList(lineSplit).indexOf("parameters:");
                    String patientID = lineSplit[parameterIndex + 1];
                    String appointmentID = lineSplit[parameterIndex + 2];
                    cancelAppointment(patientID, appointmentID);
                }else if (line.contains(Constants.SWAP_APPOINTMENT)){
                    String [] lineSplit = line.split(Constants.SPACE);
                    int parameterIndex = Arrays.asList(lineSplit).indexOf("parameters:");
                    String patientID = lineSplit[parameterIndex + 1];
                    String oldAppointmentID = lineSplit[parameterIndex + 2];
                    String oldAppointmentType = lineSplit[parameterIndex + 3];
                    String newAppointmentID = lineSplit[parameterIndex + 4];
                    String newAppointmentType = lineSplit[parameterIndex + 5];
                    swapAppointment(patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
                }
            }
            fileReader.close();
            System.out.println("Finished recovering from log");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
