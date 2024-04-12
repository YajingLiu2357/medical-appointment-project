# Client
This file shows how to connect to different replica web services in client side.
## Replica 1
```java
try{
  String ip = "172.30.123.7";
  InetAddress address = InetAddress.getByName(ip);

  URL urlCenter = new URL("http://"+ip+":8080/center");
  QName qnameCenter = new QName("http://webservice.example.com.Replica1/", "CenterImplService");
  Service serviceCenter = Service.create(urlCenter, qnameCenter);
  QName qnameCenter2 = new QName("http://webservice.example.com.Replica1/", "CenterImplPort");
  Appointment center = serviceCenter.getPort(qnameCenter2, Appointment.class);
  Appointment mtl = serviceCenter.getPort(qnameCenter2, Appointment.class);
  Appointment que = serviceCenter.getPort(qnameCenter2, Appointment.class);
  Appointment she = serviceCenter.getPort(qnameCenter2, Appointment.class);

// URL urlMTL = new URL("http://"+ip+":8081/MTL?wsdl");
// QName qnameMTL = new QName("http://webservice.example.com.Replica1/", "HospitalImplService");
// Service serviceMTL = Service.create(urlMTL, qnameMTL);
// QName qnameMTL2 = new QName("http://webservice.example.com.Replica1/", "HospitalImplPort");
// mtl = serviceMTL.getPort(qnameMTL2, Appointment.class);
// // mtl.addAppointment(appointmentID, appointmentType, capacity);
//
// URL urlQUE = new URL("http://"+ip+":8082/QUE?wsdl");
// QName qnameQUE = new QName("http://webservice.example.com.Replica1/", "HospitalImplService");
// Service serviceQUE = Service.create(urlQUE, qnameQUE);
// QName qnameQUE2 = new QName("http://webservice.example.com.Replica1/", "HospitalImplPort");
// que = serviceQUE.getPort(qnameQUE2, Appointment.class);
//
// URL urlSHE = new URL("http://"+ip+":8083/SHE?wsdl");
// QName qnameSHE = new QName("http://webservice.example.com.Replica1/", "HospitalImplService");
// Service serviceSHE = Service.create(urlSHE, qnameSHE);
// QName qnameSHE2 = new QName("http://webservice.example.com.Replica1/", "HospitalImplPort");
// she = serviceSHE.getPort(qnameSHE2, Appointment.class);
}
```
## Replica 2

```java
try{
  String ip = ""; // Need input replica 2 ip address
  InetAddress address = InetAddress.getByName(ip);
  
  URL urlMTL = new URL("http://"+ip+":8080/appointment/mtl?wsdl");
  QName qnameMTL = new QName("http://dhms.service.com.Replica2/", "MontrealServerService");
  Service serviceMTL = Service.create(urlMTL, qnameMTL);
  QName qnameMTL2 = new QName("http://dhms.service.com.Replica2/", "MontrealServerPort");
  Appointment mtl = serviceMTL.getPort(qnameMTL2, Appointment.class);
  // mtl.addAppointment(appointmentID, appointmentType, capacity);
  
  URL urlQUE = new URL("http://"+ip+":8080/appointment/que?wsdl");
  QName qnameQUE = new QName("http://dhms.service.com.Replica2/", "QuebecServerService");
  Service serviceQUE = Service.create(urlQUE, qnameQUE);
  QName qnameQUE2 = new QName("http://dhms.service.com.Replica2/", "QuebecServerPort");
  Appointment que = serviceQUE.getPort(qnameQUE2, Appointment.class);
  
  URL urlSHE = new URL("http://"+ip+":8080/appointment/she?wsdl");
  QName qnameSHE = new QName("http://dhms.service.com.Replica2/", "SherbrookeServerService");
  Service serviceSHE = Service.create(urlSHE, qnameSHE);
  QName qnameSHE2 = new QName("http://dhms.service.com.Replica2/", "SherbrookeServerPort");
  Appointment she = serviceSHE.getPort(qnameSHE2, Appointment.class);
}
```

## Replica 3

```java
try{
  String ip = ""; // Need input replica 3 ip address
  InetAddress address = InetAddress.getByName(ip);
  
  URL urlMTL = new URL("http://"+ip+":8080/appointment/mtl?wsdl");
  QName qnameMTL = new QName("http://servers.Replica3/", "HospitalMTLService");
  Service serviceMTL = Service.create(urlMTL, qnameMTL);
  QName qnameMTL2 = new QName("http://servers.Replica3/", "HospitalMTLPort");
  Appointment mtl = serviceMTL.getPort(qnameMTL2, Appointment.class);
  // mtl.addAppointment(appointmentID, appointmentType, capacity);
  
  URL urlQUE = new URL("http://"+ip+":8080/appointment/que?wsdl");
  QName qnameQUE = new QName("http://servers.Replica3/", "HospitalQUEService");
  Service serviceQUE = Service.create(urlQUE, qnameQUE);
  QName qnameQUE2 = new QName("http://servers.Replica3/", "HospitalQUEPort");
  Appointment que = serviceQUE.getPort(qnameQUE2, Appointment.class);
  
  URL urlSHE = new URL("http://"+ip+":8080/appointment/she?wsdl");
  QName qnameSHE = new QName("http://servers.Replica3/", "HospitalSHEService");
  Service serviceSHE = Service.create(urlSHE, qnameSHE);
  QName qnameSHE2 = new QName("http://servers.Replica3/", "HospitalSHEPort");
  Appointment she = serviceSHE.getPort(qnameSHE2, Appointment.class);
}
```

## Replica 4

```java
try{
  String ip = ""; // Need input replica 4 ip address
  InetAddress address = InetAddress.getByName(ip);
  
  URL urlMTL = new URL("http://"+ip+":8080/appointment/mtl?wsdl");
  QName qnameMTL = new QName("http://main.Replica4/", "ServerMTLService");
  Service serviceMTL = Service.create(urlMTL, qnameMTL);
  QName qnameMTL2 = new QName("http://main.Replica4/", "ServerMTLPort");
  Appointment mtl = serviceMTL.getPort(qnameMTL2, Appointment.class);
  // mtl.addAppointment(appointmentID, appointmentType, capacity);
  
  URL urlQUE = new URL("http://"+ip+":8080/appointment/que?wsdl");
  QName qnameQUE = new QName("http://main.Replica4/", "ServerQUEService");
  Service serviceQUE = Service.create(urlQUE, qnameQUE);
  QName qnameQUE2 = new QName("http://main.Replica4/", "ServerQUEPort");
  Appointment que = serviceQUE.getPort(qnameQUE2, Appointment.class);
  
  URL urlSHE = new URL("http://"+ip+":8080/appointment/she?wsdl");
  QName qnameSHE = new QName("http://main.Replica4/", "ServerSHEService");
  Service serviceSHE = Service.create(urlSHE, qnameSHE);
  QName qnameSHE2 = new QName("http://main.Replica4/", "ServerSHEPort");
  Appointment she = serviceSHE.getPort(qnameSHE2, Appointment.class);
}
```

