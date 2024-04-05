package com.example.webservice;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Type {

    public enum AppointmentType {
        PHYS,
        SURG,
        DENT,
    }

    static public String ExchangeAppointTypeCompatibility(AppointmentType type){
        if(type == AppointmentType.PHYS) return "Physician";
        else if(type == AppointmentType.SURG) return "Surgeon";
        else if(type == AppointmentType.DENT) return "Dental";
        return "Physician"; //default value
    }

    static public AppointmentType ExchangeStringCompatibility(String val){
        if(val.equals("Physician")) return AppointmentType.PHYS;
        else if(val.equals("Surgeon")) return AppointmentType.SURG;
        else if(val.equals("Dental")) return AppointmentType.DENT;
        return AppointmentType.PHYS; //default value
    }

    public static short GetAppTypeIndex(AppointmentType type){
        if(type == AppointmentType.PHYS) return 0;
        else if(type == AppointmentType.SURG) return 1;
        else return 2;
    }

    public static short GetAppTypeIndex(String appType){
        AppointmentType type = AppointmentType.valueOf(appType);
        return GetAppTypeIndex(type);
    }


    public enum CityType
    {
        MTL, //Montreal
        QUE, //Qubec
        SHE, //Sherbrooke
    }

    public enum TimeSlot
    {
        M, //moring
        A, //afternoon
        E, //evening
    }

    public enum UserType
    {
        P, //patient
        A, //admin
    }

    public static String AlignStr(String input, int alignSize)
    {
        String res = "";
        int inputLength = input.length();
        for(int i = inputLength; i < alignSize; ++i){
            res = res + "0";
        }
        res += input;
        return res;
    }

    public static String MarshallingHashMap(HashMap<String, Integer> v){
        int a = v.size();
        String ret = Type.AlignStr(String.valueOf(a), 4);
        for(String item : v.keySet()){
            Integer value = v.get(item);
            String valueAlStr = Type.AlignStr(String.valueOf(value.intValue()), 4);
            ret += item + valueAlStr;
        }
        return ret;
    }

    public static String MarshallingHashMapCompatibility(HashMap<String, Integer> v){
        return v.toString();
    }

    public static HashMap<String, Integer> UnmarshallingHashMapForListAvailableAppointments(String originStr){
        System.out.println(originStr);
        int startIndex = 0;
        String dentalNumStr = originStr.substring(startIndex, startIndex + 4);
        HashMap<String, Integer> resMap = new HashMap<String, Integer>();
        startIndex = startIndex + 4;
        System.out.println("number:" + dentalNumStr + "\n");
        Integer dentalNum = Integer.valueOf(dentalNumStr);
        for(int i = 0; i < dentalNum; ++i){
            String appointmentID = originStr.substring(startIndex, startIndex + 15);
            System.out.println("appID:" + appointmentID + "\n");
            startIndex = startIndex + 15;
            String capacityNum = originStr.substring(startIndex, startIndex + 4);
            System.out.println("capa:" + capacityNum + "\n");
            startIndex = startIndex + 4;
            resMap.put(appointmentID, Integer.valueOf(capacityNum));
        }
        return resMap;
    }

    public static HashMap<String, Integer> UnmarshallingHashMap(String originStr){
        System.out.println(originStr);
        int startIndex = 0;
        String dentalNumStr = originStr.substring(startIndex, startIndex + 4);
        HashMap<String, Integer> resMap = new HashMap<String, Integer>();
        startIndex = startIndex + 4;
        System.out.println("number:" + dentalNumStr + "\n");
        Integer dentalNum = Integer.valueOf(dentalNumStr);
        for(int i = 0; i < dentalNum; ++i){
            String appointmentID = originStr.substring(startIndex, startIndex + 10);
            System.out.println("appID:" + appointmentID + "\n");
            startIndex = startIndex + 10;
            String capacityNum = originStr.substring(startIndex, startIndex + 4);
            System.out.println("capa:" + capacityNum + "\n");
            startIndex = startIndex + 4;
            resMap.put(appointmentID, Integer.valueOf(capacityNum));
        }
        return resMap;
    }

    public static String MarshallingHashMap(ConcurrentHashMap<String, Integer> v){
        int a = v.size();
        String ret = Type.AlignStr(String.valueOf(a), 4);
        for(String item : v.keySet()){
            Integer value = v.get(item);
            String valueAlStr = Type.AlignStr(String.valueOf(value.intValue()), 4);
            ret += item + valueAlStr;
        }
        return ret;
    }

    public static String MarshallingHashMapCompatibility(ConcurrentHashMap<String, Integer> v){
        return v.toString();
    }

    public static ConcurrentHashMap<String, Integer> UnmarshallingConcurrentHashMap(String originStr){
        System.out.println(originStr);
        int startIndex = 0;
        String dentalNumStr = originStr.substring(startIndex, startIndex + 4);
        ConcurrentHashMap<String, Integer> resMap = new ConcurrentHashMap<String, Integer>();
        startIndex = startIndex + 4;
        System.out.println("number:" + dentalNumStr + "\n");
        Integer dentalNum = Integer.valueOf(dentalNumStr);
        for(int i = 0; i < dentalNum; ++i){
            String appointmentID = originStr.substring(startIndex, startIndex + 10);
            System.out.println("appID:" + appointmentID + "\n");
            startIndex = startIndex + 10;
            String capacityNum = originStr.substring(startIndex, startIndex + 4);
            System.out.println("capa:" + capacityNum + "\n");
            startIndex = startIndex + 4;
            resMap.put(appointmentID, Integer.valueOf(capacityNum));
        }
        return resMap;
    }

    public static String MarshallingAppointmentsAndTypeCompatibility(String userID, HashMap<String, Type.AppointmentType> v){
        List<String> reslist = new ArrayList<>();
        for(String item : v.keySet()){
            Type.AppointmentType value = v.get(item);
            String tmp = "";
            tmp = userID + " " + item + " " + String.valueOf(value);
            reslist.add(tmp);
        }
        return reslist.toString();
    }


    public static String MarshallingAppointmentsAndType(HashMap<String, Type.AppointmentType> v){
        int a = v.size();
        String ret = Type.AlignStr(String.valueOf(a), 4);
        for(String item : v.keySet()){
            Type.AppointmentType value = v.get(item);
            String valueAlStr = Type.AlignStr(String.valueOf(value.ordinal()), 1);
            ret += item + valueAlStr;
        }
        return ret;
    }

    public static HashMap<String, Type.AppointmentType> UnmarshallingAppointmentsAndType(String originStr){
        System.out.println(originStr);
        HashMap<String, Type.AppointmentType> res = new HashMap<>();
        int startIndex = 0;
        String dentalNumStr = originStr.substring(startIndex, startIndex + 4);
        startIndex = startIndex + 4;
        System.out.println("number:" + dentalNumStr + "\n");
        Integer dentalNum = Integer.valueOf(dentalNumStr);
        for(int i = 0; i < dentalNum; ++i){
            String appointmentID = originStr.substring(startIndex, startIndex + 10);
            System.out.println("appID:" + appointmentID + "\n");
            startIndex = startIndex + 10;
            String appointmentType = originStr.substring(startIndex, startIndex + 1);
            System.out.println("capa:" + appointmentType + "\n");
            startIndex = startIndex + 1;
            res.put(appointmentID, AppointmentType.values()[Integer.valueOf(appointmentType).intValue()]);
        }
        return res;
    }

    public static class UserEntity
    {
        public CityType city;
        public UserType user;
        public Integer index = 0;

        public String SerializeUser(){
            String cityStr = this.city.toString();
            String userStr = this.user.toString();
            int indexNum = this.index.toString().length();
            String indexStr = AlignStr(this.index.toString(), 4);
            return cityStr + userStr + indexStr;
        }

        public boolean DeserializeUser(String userEntityStr){
            String cityStr = userEntityStr.substring(0, 3);
            this.city = CityType.valueOf(cityStr);
            String userStr = userEntityStr.substring(3, 4);
            this.user = UserType.valueOf(userStr);
            String indexStr = userEntityStr.substring(4,8);
            this.index = Integer.valueOf(indexStr);
            return true;
        }
    }

    public static class AppointmentEntity
    {
        public CityType city;
        public TimeSlot time;
        public int day;
        public int month;
        public int year;
        public AppointmentEntity(){
            this.city = CityType.MTL;
            this.time = TimeSlot.A;
            this.day = 1;
            this.month = 1;
            this.year = 1;
        }
        public AppointmentEntity(CityType city, TimeSlot ts, int d, int m, int y){
            this.city = city;
            this.time = ts;
            Calendar cal=Calendar.getInstance();
            day = d;
            month = m;
            year = y;
        }

        public String SerializeAppointmentEntity(){
            return city.toString() + time.toString() + AlignStr(String.valueOf(day), 2) +
                    AlignStr(String.valueOf(month), 2) +  String.valueOf(year);
        }

        public boolean DeserializeAppointmentEntity(String appointmentEntity){
            String cityStr = appointmentEntity.substring(0, 3);
            this.city = CityType.valueOf(cityStr);
            String userStr = appointmentEntity.substring(3, 4);
            this.time = TimeSlot.valueOf(userStr);
            this.day = Integer.valueOf(appointmentEntity.substring(4, 6));
            this.month = Integer.valueOf(appointmentEntity.substring(6, 8));
            this.year = Integer.valueOf(appointmentEntity.substring(8, 10));
            return true;
        }
    }

    public static void main(String args[]){
        //test case about base types
        UserEntity userA = new UserEntity();
        userA.city = CityType.QUE;
        userA.user = UserType.P;
        userA.index = 123;
        String idA = userA.SerializeUser();
        System.out.println(idA);
        String idB = "MTLP0008";
        UserEntity userB = new UserEntity();
        userB.DeserializeUser((idB));
        System.out.println(userB.city);
        System.out.println(userB.user);
        System.out.println(userB.index);
        System.out.println(userB.SerializeUser());
        AppointmentEntity appointment = new AppointmentEntity(CityType.MTL, TimeSlot.E, 7,2,24);
        String appointmentStr = appointment.SerializeAppointmentEntity();
        System.out.println(appointmentStr);
        appointment.DeserializeAppointmentEntity("SHEE101124");
        System.out.println(appointment.day);
        System.out.println(appointment.month);
        System.out.println(appointment.year);
        System.out.println(appointment.city);
        System.out.println(appointment.time);
    }
}
