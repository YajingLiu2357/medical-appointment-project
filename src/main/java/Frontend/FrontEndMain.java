package Frontend;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;

public class FrontEndMain {
    public static void main(String args[]){
        try {

            FrontEndHelper.inputIP();

            new UserInterface("Distributed Health Care Management System (DHMS) Client");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
