package Sequencer.Sequencer;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class test {

    static public List<String> SplitMessage(String mess, Type.AppointmentType appType){
        String typeStr1 = "PHYS";
        String typeStr2 = "SURG";
        String typeStr3 = "DENT";
        String curType = typeStr1;
        if(appType == Type.AppointmentType.SURG){
            curType = typeStr2;
        }
        else if(appType == Type.AppointmentType.DENT){
            curType = typeStr3;
        }
        List<String> ret = new ArrayList<>();
        mess = mess.substring(1, mess.length()-1);
        //System.out.println(mess.length());
        if(mess.length() == 0) return ret;
        String[] clips = mess.split(",");
        for(int i = 0; i < clips.length; i++) {
            String tmp = curType + ":" + clips[i].trim();
            ret.add(tmp);
        }
        return ret;
    }

    static public String MergeAllResults(String phys, String surg, String dent){
        List<String> physicsStr = SplitMessage(phys, Type.AppointmentType.PHYS);
        List<String> surgenStr = SplitMessage(surg, Type.AppointmentType.SURG);
        List<String> dentsStr = SplitMessage(dent, Type.AppointmentType.DENT);

        String res = "{";
        for(int i = 0; i < physicsStr.size(); i++) {
            res += physicsStr.get(i);
            if(!(i == physicsStr.size() - 1 && (surgenStr.size() == 0) && (dentsStr.size() == 0)))
                res += ", ";
        }
        for(int i = 0; i < surgenStr.size(); i++) {
            res += surgenStr.get(i);
            if(!(i == physicsStr.size() - 1 && (dentsStr.size() == 0)))
                res += ", ";
        }
        for(int i = 0; i < dentsStr.size(); i++) {
            res += dentsStr.get(i);
            if(i != dentsStr.size()-1) {
                res += ", ";
            }
        }
        res += "}";
        return res;
    }

    public static void main(String[] args) throws MalformedURLException {
        String mess1 = "{MTLM010124=10}";
        String mess2 = "{MTLM020124=10}";
        String mess3 = "{}";

        String res = MergeAllResults(mess1, mess2, mess3);
        System.out.println(res);


    }
}
