package Replica3.quebec_server.src.ws;

import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style=Style.RPC)
public interface DhmsQuebec {
	@WebMethod
	public String hello();
	@WebMethod
	public String[] get_appointment_schedule(String patient_id);
	@WebMethod
	public String add_appointment(String appointment_id, String type, String capacity);
	@WebMethod
	public String book_appointment(String patient_id, String appointment_id, String type);
	@WebMethod
	public String remove_appointment(String appointment_id, String type);
	@WebMethod
	public String cancel_appointment(String appointment_id, String patient_id, String type);
	@WebMethod
	public String[] get_appointment_records(String appointment_type);
	@WebMethod
	public String swap_appointment(String patient_id, String old_appointment_id, String old_type, String appointment_id, String type);
}
