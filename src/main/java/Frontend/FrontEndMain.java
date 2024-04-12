package Frontend;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;

public class FrontEndMain {
    public static void main(String args[]){
        try {
            //initialize the front-end helper
            List<String> IPs = new ArrayList<String>();
            IPs.add("127.0.0.1"); //replica1
            IPs.add("127.0.0.1"); //replica2
            IPs.add("127.0.0.1"); //replica3
            IPs.add("127.0.0.1"); //replica4

            List<Integer> Ports = new ArrayList<>();
            Ports.add(8080); //replica1
            Ports.add(8080); //replica2
            Ports.add(8080); //replica3
            Ports.add(8080); //replica4

            FrontEndHelper.setReplicaManagerIPs(IPs);
            FrontEndHelper.setReplicaManagerPorts(Ports);

            new UserInterface("Distributed Health Care Management System (DHMS) Client");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
