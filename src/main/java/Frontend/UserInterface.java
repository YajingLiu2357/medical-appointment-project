package com.example.webservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;

public class UserInterface extends JFrame{
    private static StartInterfacePanel startInterfacePanel = new StartInterfacePanel();
    private static CreateUserInterface createUserIDInterface = new CreateUserInterface();
    private static OperationPanel patientPanel = new OperationPanel("patient");
    private static OperationPanel adminPanel = new OperationPanel("admin");
    private static BookAppointmentPanel bookPanel;

    static {
        try {
            bookPanel = new BookAppointmentPanel();
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private static final SwapAppointmentPanel swapPanel;

    static {
        try {
            swapPanel = new SwapAppointmentPanel();
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private static CancelAppointmentPanel cancelPanel;

    static {
        try {
            cancelPanel = new CancelAppointmentPanel();
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private static AddAppointmentPanel addPanel = new AddAppointmentPanel();
    private static RemoveAppointmentPanel removePanel = new RemoveAppointmentPanel();
    private static ViewAppointmentPanel viewPanel = new ViewAppointmentPanel();


    public UserInterface(String title) throws RemoteException, NotBoundException {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setSize(600,400);
        setVisible(true);

        startInterfacePanel.setVisible(true);
        createUserIDInterface.setVisible(false);
        patientPanel.setVisible(false);
        adminPanel.setVisible(false);
        bookPanel.setVisible(false);
        cancelPanel.setVisible(false);
        addPanel.setVisible(false);
        removePanel.setVisible(false);
        viewPanel.setVisible(false);
        swapPanel.setVisible(false);

        add(startInterfacePanel);
        add(createUserIDInterface);
        add(patientPanel);
        add(adminPanel);
        add(bookPanel);
        add(swapPanel);
        add(cancelPanel);
        add(addPanel);
        add(removePanel);
        add(viewPanel);
    }

    public static class StartInterfacePanel extends JPanel {
        public StartInterfacePanel(){
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBounds(150, 20, 300, 400);
            setLayout(new GridLayout(10, 1));

            JLabel label0 =new JLabel("Distributed Health Care Management System");
            JLabel label1=new JLabel("Input User ID:");
            JTextField txtfield1=new JTextField();
            txtfield1.setText("User ID");
            JButton loginButton = new JButton("Login");

            JLabel labelInfor = new JLabel("");
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String currentInputUserID = txtfield1.getText();
                    try {
                        if(!ClientData.getInstance().IsValidUserName(currentInputUserID)){
                            labelInfor.setText("Error user name, please input again!");
                        }
                        else{
                            ClientData.getInstance().Initialize(currentInputUserID);
                            if(ClientData.getInstance().IsPatient()){
                                startInterfacePanel.setVisible(!startInterfacePanel.isVisible());
                                patientPanel.setVisible(!patientPanel.isVisible());
                                patientPanel.labelUser.setText("User ID:" + ClientData.getInstance().userID);
                            }
                            else{
                                startInterfacePanel.setVisible(!startInterfacePanel.isVisible());
                                adminPanel.setVisible(!adminPanel.isVisible());
                                adminPanel.labelUser.setText("User ID:" + ClientData.getInstance().userID);
                            }
                        }
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            JButton registerButton = new JButton("Register");
            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startInterfacePanel.setVisible(!startInterfacePanel.isVisible());
                    createUserIDInterface.setVisible(!createUserIDInterface.isVisible());
                }
            });

            add(label0);
            add(label1);
            add(txtfield1);
            add(loginButton);
            add(registerButton);
            add(labelInfor);
        }
    }

    public static class CreateUserInterface extends JPanel {
        public CreateUserInterface(){
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBounds(150, 20, 300, 400);
            setLayout(new GridLayout(10, 1));

            JLabel label1=new JLabel("Input User Information:");
            JLabel label2=new JLabel("Choose Your City:");
            JComboBox cmb=new JComboBox();
            cmb.addItem("Choose Your City");
            cmb.addItem("MTL");
            cmb.addItem("QUE");
            cmb.addItem("SHE");
            JLabel label3 = new JLabel("Choose Your User Type:");
            JComboBox cmb2=new JComboBox();
            cmb2.addItem("Choose Your User Type");
            cmb2.addItem("P");
            cmb2.addItem("A");
            JButton btnSubmit = new JButton("Submit");
            btnSubmit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String city = cmb.getSelectedItem().toString();
                    String userType = cmb2.getSelectedItem().toString();
                    try {
                        ClientData.getInstance().Initialize(city, userType);
                        createUserIDInterface.setVisible(false);
                        if(ClientData.getInstance().IsPatient()){
                            patientPanel.setVisible(true);
                            patientPanel.labelUser.setText(ClientData.getInstance().userID);
                        }
                        else{
                            adminPanel.setVisible(true);
                            adminPanel.labelUser.setText(ClientData.getInstance().userID);
                        }
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            JButton btnBack = new JButton("Back");
            btnBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startInterfacePanel.setVisible(!startInterfacePanel.isVisible());
                    createUserIDInterface.setVisible(!createUserIDInterface.isVisible());
                }
            });
            add(label1);
            add(label2);
            add(cmb);
            add(label3);
            add(cmb2);
            add(btnSubmit);
            add(btnBack);
        }
    }

    public static class OperationPanel extends JPanel {
        public JLabel labelUser = new JLabel("User ID:");
        public OperationPanel(String panelType){
            String operation1 = "Book Appointment";
            String operation2 = "Cancel Appointment";
            String operation3 = "View Booked Appointment";
            String operation4 = "Add Appointment";
            String operation5 = "Remove Appointment";
            String operation6 = "View Availiable Appointment";
            String operation7 = "Swap Appointment";


            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBounds(150, 20, 300, 400);
            setLayout(new GridLayout(10, 1));

            JButton btnBookAppointment = new JButton(operation1);
            btnBookAppointment.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    adminPanel.setVisible(false);
                    patientPanel.setVisible(false);
                    bookPanel.setVisible(true);
                }
            });
            JButton btnCancelAppointment = new JButton(operation2);
            btnCancelAppointment.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    adminPanel.setVisible(false);
                    patientPanel.setVisible(false);
                    cancelPanel.setVisible(true);
                }
            });
            JButton btnViewAppointment = new JButton(operation3);
            btnViewAppointment.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    adminPanel.setVisible(false);
                    patientPanel.setVisible(false);
                    try {
                        String[] ret = ClientData.getInstance().ViewBookedAppointments();
                        viewPanel.SetData(ret);
                        viewPanel.setVisible(true);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    }

                    String[] itemArray = new String[10];
                }
            });
            JButton btnAddAppointment = new JButton(operation4);
            btnAddAppointment.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    adminPanel.setVisible(false);
                    patientPanel.setVisible(false);
                    addPanel.setVisible(true);
                }
            });
            JButton btnRemoveAppointment = new JButton(operation5);
            btnRemoveAppointment.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    adminPanel.setVisible(false);
                    patientPanel.setVisible(false);
                    removePanel.setVisible(true);
                }
            });
            JButton btnViewBookedAppointment = new JButton(operation6);
            btnViewBookedAppointment.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    adminPanel.setVisible(false);
                    patientPanel.setVisible(false);
                    try {
                        String[] ret = ClientData.getInstance().ViewAvailableAppointments();
                        viewPanel.SetData(ret);
                        viewPanel.setVisible(true);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    }

                    String[] itemArray = new String[10];
                }
            });
            JButton btnSwapAppointment = new JButton(operation7);
            btnSwapAppointment.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    adminPanel.setVisible(false);
                    patientPanel.setVisible(false);
                    swapPanel.setVisible(true);
                }
            });

            add(labelUser);
            if(panelType == "admin"){
                add(btnBookAppointment);
                add(btnCancelAppointment);
                add(btnViewAppointment);
                add(btnAddAppointment);
                add(btnRemoveAppointment);
                add(btnViewBookedAppointment);
            }
            else{
                add(btnBookAppointment);
                add(btnCancelAppointment);
                add(btnViewAppointment);
                add(btnSwapAppointment);
            }
        }
    }

    public static class BookAppointmentPanel extends JPanel {
        JLabel labelInfor = new JLabel(" ");
        public BookAppointmentPanel() throws NotBoundException, RemoteException {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBounds(150, 0, 300, 400);
            setLayout(new GridLayout(10, 2));

            JLabel label1=new JLabel("User ID:");
            JTextField userIDtf = new JTextField(ClientData.getInstance().userID);
            userIDtf.setText(ClientData.getInstance().userID);
            JLabel label2=new JLabel("Choose Your City:");
            JComboBox cmb=new JComboBox();
            cmb.addItem("Choose Your City");
            cmb.addItem("MTL");
            cmb.addItem("QUE");
            cmb.addItem("SHE");
            JLabel label3 = new JLabel("Input Your Time Type:");
            JComboBox cmb2=new JComboBox();
            cmb2.addItem("M");
            cmb2.addItem("A");
            cmb2.addItem("E");
            JLabel label4 = new JLabel("Input Your Date:");
            JComboBox cmb3=new JComboBox();
            cmb3.addItem("01");
            cmb3.addItem("02");
            cmb3.addItem("03");
            cmb3.addItem("04");
            cmb3.addItem("05");
            cmb3.addItem("06");
            cmb3.addItem("07");
            cmb3.addItem("08");
            cmb3.addItem("09");
            cmb3.addItem("10");
            cmb3.addItem("11");
            cmb3.addItem("12");
            cmb3.addItem("13");
            cmb3.addItem("14");
            cmb3.addItem("15");
            cmb3.addItem("16");
            cmb3.addItem("17");
            cmb3.addItem("18");
            cmb3.addItem("19");
            cmb3.addItem("20");
            cmb3.addItem("21");
            cmb3.addItem("22");
            cmb3.addItem("23");
            cmb3.addItem("24");
            cmb3.addItem("25");
            cmb3.addItem("26");
            cmb3.addItem("27");
            cmb3.addItem("28");
            cmb3.addItem("29");
            cmb3.addItem("30");
            cmb3.addItem("31");
            JLabel label5 = new JLabel("Input Your Month:");
            JComboBox cmb4=new JComboBox();
            cmb4.addItem("01");
            cmb4.addItem("02");
            cmb4.addItem("03");
            cmb4.addItem("04");
            cmb4.addItem("05");
            cmb4.addItem("06");
            cmb4.addItem("07");
            cmb4.addItem("08");
            cmb4.addItem("09");
            cmb4.addItem("10");
            cmb4.addItem("11");
            cmb4.addItem("12");
            JLabel label6 = new JLabel("Input Your Year:");
            JComboBox cmb5=new JComboBox();
            cmb5.addItem("24");
            cmb5.addItem("25");
            cmb5.addItem("26");
            cmb5.addItem("27");
            cmb5.addItem("28");
            cmb5.addItem("29");
            cmb5.addItem("30");
            JLabel label7 = new JLabel("Input Appointment Type:");
            JComboBox cmb6=new JComboBox();
            cmb6.addItem("PHYS");
            cmb6.addItem("SURG");
            cmb6.addItem("DENT");



            JButton btnSubmit = new JButton("Submit");
            btnSubmit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String userID = userIDtf.getText();
                    String city = cmb.getSelectedItem().toString();
                    String time = cmb2.getSelectedItem().toString();
                    String date = cmb3.getSelectedItem().toString();
                    String month = cmb4.getSelectedItem().toString();
                    String year = cmb5.getSelectedItem().toString();
                    String appointT = cmb6.getSelectedItem().toString();
                    try {
                        String res = ClientData.getInstance().BookAppointment(userID, city, time, date, month, year, appointT);
                        labelInfor.setText(res);
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            JButton btnBack = new JButton("Back");
            btnBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    bookPanel.setVisible(false);
                    try {
                        if(ClientData.getInstance().IsPatient()){
                            patientPanel.setVisible(true);
                        }
                        else{
                            adminPanel.setVisible(true);
                        }
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            add(label1);
            add(userIDtf);
            add(label2);
            add(cmb);
            add(label3);
            add(cmb2);
            add(label4);
            add(cmb3);
            add(label5);
            add(cmb4);
            add(label6);
            add(cmb5);
            add(label7);
            add(cmb6);

            add(btnSubmit);
            add(btnBack);
            add(labelInfor);
        }
    }

    public static class SwapAppointmentPanel extends JPanel {
        JLabel labelInfor = new JLabel(" ");
        public SwapAppointmentPanel() throws NotBoundException, RemoteException {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBounds(150, 0, 300, 400);
            setLayout(new GridLayout(10, 2));
            JLabel label1=new JLabel("Input User ID:");
            JTextField userTxtfield=new JTextField();
            JLabel label0=new JLabel("Choose Your City:");
            JComboBox cmb0=new JComboBox();
            cmb0.addItem("Choose Your City");
            cmb0.addItem("MTL");
            cmb0.addItem("QUE");
            cmb0.addItem("SHE");
            userTxtfield.setText(ClientData.getInstance().userID);
            JLabel label2=new JLabel("Old Appointment ID:");
            JTextField txtfield1=new JTextField();
            JLabel label3 = new JLabel("Old Appointment Type:");
            JComboBox cmb2=new JComboBox();
            cmb2.addItem("PHYS");
            cmb2.addItem("SURG");
            cmb2.addItem("DENT");
            JLabel label4=new JLabel("New Appointment ID:");
            JTextField txtfield3 =new JTextField();
            JLabel label5 = new JLabel("New Appointment Type:");
            JComboBox cmb4=new JComboBox();
            cmb4.addItem("PHYS");
            cmb4.addItem("SURG");
            cmb4.addItem("DENT");

            JButton btnSubmit = new JButton("Submit");
            btnSubmit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String cityType = cmb0.getSelectedItem().toString();
                    String userID = userTxtfield.getText();
                    String oldAppointmentID = txtfield1.getText();
                    String oldAppointmentType = cmb2.getSelectedItem().toString();
                    String newAppointmentID = txtfield3.getText();
                    String newAppointmentType = cmb4.getSelectedItem().toString();
                    try {
                        String res = ClientData.getInstance().SwapAppointment(cityType, userID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
                        labelInfor.setText(res);
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            JButton btnBack = new JButton("Back");
            btnBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    swapPanel.setVisible(false);
                    try {
                        if(ClientData.getInstance().IsPatient()){
                            patientPanel.setVisible(true);
                        }
                        else{
                            adminPanel.setVisible(true);
                        }
                    } catch (NotBoundException | RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            add(label0);
            add(cmb0);
            add(label1);
            add(userTxtfield);
            add(label2);
            add(txtfield1);
            add(label3);
            add(cmb2);
            add(label4);
            add(txtfield3);
            add(label5);
            add(cmb4);

            add(btnSubmit);
            add(btnBack);
            add(labelInfor);
        }
    }

    public static class CancelAppointmentPanel extends JPanel {
        JLabel labelInfor = new JLabel(" ");
        public CancelAppointmentPanel() throws NotBoundException, RemoteException {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBounds(150, 0, 300, 400);
            setLayout(new GridLayout(10, 2));
            JLabel label1=new JLabel("Input User ID:");
            JTextField userTxtfield=new JTextField();
            userTxtfield.setText(ClientData.getInstance().userID);
            JLabel label2=new JLabel("Input Appointment ID:");
            JTextField txtfield1=new JTextField();

            JButton btnSubmit = new JButton("Submit");
            btnSubmit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String appointmentID = txtfield1.getText();
                    try {
                        String res = ClientData.getInstance().CancelAppointment(appointmentID);
                        labelInfor.setText(res);
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            JButton btnBack = new JButton("Back");
            btnBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cancelPanel.setVisible(false);
                    try {
                        if(ClientData.getInstance().IsPatient()){
                            patientPanel.setVisible(true);
                        }
                        else{
                            adminPanel.setVisible(true);
                        }
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            add(label1);
            add(userTxtfield);
            add(label2);
            add(txtfield1);

            add(btnSubmit);
            add(btnBack);
            add(labelInfor);
        }
    }

    public static class AddAppointmentPanel extends JPanel {
        JLabel labelInfor = new JLabel(" ");
        public AddAppointmentPanel(){
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBounds(150, 0, 300, 400);
            setLayout(new GridLayout(10, 2));

            JLabel label2=new JLabel("Choose Your City:");
            JComboBox cmb=new JComboBox();
            cmb.addItem("Choose Your City");
            cmb.addItem("MTL");
            cmb.addItem("QUE");
            cmb.addItem("SHE");
            JLabel label3 = new JLabel("Input Your Time Type:");
            JComboBox cmb2=new JComboBox();
            cmb2.addItem("M");
            cmb2.addItem("A");
            cmb2.addItem("E");
            JLabel label4 = new JLabel("Input Your Date:");
            JComboBox cmb3=new JComboBox();
            cmb3.addItem("01");
            cmb3.addItem("02");
            cmb3.addItem("03");
            cmb3.addItem("04");
            cmb3.addItem("05");
            cmb3.addItem("06");
            cmb3.addItem("07");
            cmb3.addItem("08");
            cmb3.addItem("09");
            cmb3.addItem("10");
            cmb3.addItem("11");
            cmb3.addItem("12");
            cmb3.addItem("13");
            cmb3.addItem("14");
            cmb3.addItem("15");
            cmb3.addItem("16");
            cmb3.addItem("17");
            cmb3.addItem("18");
            cmb3.addItem("19");
            cmb3.addItem("20");
            cmb3.addItem("21");
            cmb3.addItem("22");
            cmb3.addItem("23");
            cmb3.addItem("24");
            cmb3.addItem("25");
            cmb3.addItem("26");
            cmb3.addItem("27");
            cmb3.addItem("28");
            cmb3.addItem("29");
            cmb3.addItem("30");
            cmb3.addItem("31");
            JLabel label5 = new JLabel("Input Your Month:");
            JComboBox cmb4=new JComboBox();
            cmb4.addItem("01");
            cmb4.addItem("02");
            cmb4.addItem("03");
            cmb4.addItem("04");
            cmb4.addItem("05");
            cmb4.addItem("06");
            cmb4.addItem("07");
            cmb4.addItem("08");
            cmb4.addItem("09");
            cmb4.addItem("10");
            cmb4.addItem("11");
            cmb4.addItem("12");
            JLabel label6 = new JLabel("Input Your Year:");
            JComboBox cmb5=new JComboBox();
            cmb5.addItem("24");
            cmb5.addItem("25");
            cmb5.addItem("26");
            cmb5.addItem("27");
            cmb5.addItem("28");
            cmb5.addItem("29");
            cmb5.addItem("30");
            JLabel label7 = new JLabel("Input Appointment Type:");
            JComboBox cmb6=new JComboBox();
            cmb6.addItem("PHYS");
            cmb6.addItem("SURG");
            cmb6.addItem("DENT");
            JLabel label8 = new JLabel("Input the Capacity:");
            TextField textCapacity = new TextField();


            JButton btnSubmit = new JButton("Submit");
            btnSubmit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String city = cmb.getSelectedItem().toString();
                    String time = cmb2.getSelectedItem().toString();
                    String date = cmb3.getSelectedItem().toString();
                    String month = cmb4.getSelectedItem().toString();
                    String year = cmb5.getSelectedItem().toString();
                    String appointT = cmb6.getSelectedItem().toString();
                    int capacity = Integer.parseInt(textCapacity.getText());
                    try {
                        String res = ClientData.getInstance().AddAppointment(city, time, date, month, year, appointT, capacity);
                        labelInfor.setText(res);
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            JButton btnBack = new JButton("Back");
            btnBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addPanel.setVisible(false);
                    try {
                        if(ClientData.getInstance().IsPatient()){
                            patientPanel.setVisible(true);
                        }
                        else{
                            adminPanel.setVisible(true);
                        }
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            add(label2);
            add(cmb);
            add(label3);
            add(cmb2);
            add(label4);
            add(cmb3);
            add(label5);
            add(cmb4);
            add(label6);
            add(cmb5);
            add(label7);
            add(cmb6);
            add(label8);
            add(textCapacity);

            add(btnSubmit);
            add(btnBack);
            add(labelInfor);
        }
    }

    public static class RemoveAppointmentPanel extends JPanel {
        JLabel labelInfor = new JLabel(" ");
        public RemoveAppointmentPanel(){
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBounds(150, 0, 300, 400);
            setLayout(new GridLayout(10, 2));

            JLabel label2=new JLabel("Input Appointment ID:");
            JTextField txtfield1=new JTextField();
            JLabel label7 = new JLabel("Input Appointment Type:");
            JComboBox cmb6=new JComboBox();
            cmb6.addItem("PHYS");
            cmb6.addItem("SURG");
            cmb6.addItem("DENT");

            JButton btnSubmit = new JButton("Submit");
            btnSubmit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String appointmentID = txtfield1.getText();
                    try {
                        String res = ClientData.getInstance().RemoveAppointment(appointmentID, Objects.requireNonNull(cmb6.getSelectedItem()).toString());
                        labelInfor.setText(res);
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            JButton btnBack = new JButton("Back");
            btnBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removePanel.setVisible(false);
                    try {
                        if(ClientData.getInstance().IsPatient()){
                            patientPanel.setVisible(true);
                        }
                        else{
                            adminPanel.setVisible(true);
                        }
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            add(label2);
            add(txtfield1);
            add(label7);
            add(cmb6);

            add(btnSubmit);
            add(btnBack);
            add(labelInfor);
        }
    }



    public static class ViewAppointmentPanel extends JPanel {
        JList<String> listInfor=new JList<String>();
        public ViewAppointmentPanel(){
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBounds(200, 20, 300, 500);
            //setLayout(new GridLayout(2, 3));

            JButton btnBack = new JButton("Back");
            //btnBack.setBounds(150,100,300,100);
            btnBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewPanel.setVisible(false);
                    try {
                        if(ClientData.getInstance().IsPatient()){
                            patientPanel.setVisible(true);
                        }
                        else{
                            adminPanel.setVisible(true);
                        }
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            JScrollPane s = new JScrollPane(listInfor);
            s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            s.setBounds(150, 20, 400, 400);
            add(s);
            add(btnBack);
        }

        public void SetData(String[] data){
            listInfor.setListData(data);
        }
    }
}
