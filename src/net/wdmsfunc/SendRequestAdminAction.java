package net.wdmsfunc;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;

import org.apache.struts2.interceptor.SessionAware;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.*;
import net.wdmsfunc.CaptchaServlet;

public class SendRequestAdminAction extends ActionSupport implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String firstname = null;
	private String lastname = null;
	private String email = null;
	private String password = null;
	@SuppressWarnings("rawtypes")
	private Map session = null;
	private String captcha_response = null;
	public String ipaddr = null;
	
	public String send_email() throws MessagingException, UnknownHostException, SocketException
	{
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("wdms.group2@gmail.com","group2!@#");
				}
			});
 
		try {
			 Enumeration e = NetworkInterface.getNetworkInterfaces();

	         while(e.hasMoreElements()) {
	            NetworkInterface ni = (NetworkInterface) e.nextElement();
	            if(ni.getName().equals("eth0"))
	            {
	            	Enumeration e2 = ni.getInetAddresses();
		            while (e2.hasMoreElements()){
		               InetAddress ip = (InetAddress) e2.nextElement();
		               ipaddr = ip.toString();
		            }
	            }
	         }
	        
			UUID uuid = UUID.randomUUID();
			String randomUUIDString = uuid.toString();
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("admin@group2.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(email));
			message.setRecipients(Message.RecipientType.BCC,
					InternetAddress.parse("ajaymandvekar@gmail.com,vishakha.vc@gmail.com,himugdha@gmail.com"));
			
			message.setSubject("Request to validate account for: Group2 WDMS project");
			message.setText("Dear "+ firstname + " "+ lastname + 
					",\n\n Welcome to Group2 Web Based Document Management Software. Please confirm the email address to validate your identity. \n\n" + 
					"https:/"+ ipaddr + ":8443/WDMS/ValidateEmail.jsp?code=" + randomUUIDString);
			Transport.send(message);
			
			System.out.println("Done");
			return randomUUIDString;
		} 
		catch (MessagingException e) 
		{
			return "";
		}	
	}
	
	public String execute() throws Exception {
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String pass = "aj";
		Connection con=null;
		Statement stmt=null;
		PreparedStatement prest = null;
		int value = 0;
		String query = null;
		
		try{
			String captcha_str = (String)session.get(CaptchaServlet.CAPTCHA_KEY) ;
			if(!captcha_response.equals(captcha_str) )
			{
			     	addActionMessage("Invalid CaptchaCode! Please try again!");
			     	return INPUT;
			}
					
			try{
				Class.forName(driverName).newInstance();
				con=DriverManager.getConnection(url+dbName, userName,pass);
				stmt=con.createStatement();
			}
			catch(Exception e){
				addActionError(e.toString());
				System.out.println(e.getMessage());
			}
			
			stmt = con.createStatement();

			if(stmt !=null)
			{
				String query1 = "Select * from user where email='"+email+"'";
				// execute the query, and get a java resultset
				ResultSet rs = stmt.executeQuery(query1);

				// iterate through the java resultset
				rs.last();
  		        int rowCount = rs.getRow();
				if(rowCount > 0)
				{
					stmt.close();
					con.close();
					addActionMessage("ERROR: User already exists with username: "+ email + " !");
					return INPUT;			
				}
			}
			
			if(stmt !=null)
			{
				String query1 = "Select * from temp_user where email='"+email+"'";
				// execute the query, and get a java resultset
				ResultSet rs = stmt.executeQuery(query1);

				// iterate through the java resultset
				rs.last();
  		        int rowCount = rs.getRow();
				if(rowCount > 0)
				{
					prest = con.prepareStatement("Update temp_user set firstname=?,lastname=?,email=?,password=SHA(?),designation=?,privilege=?,validated=? where email=?");
					prest.setString(8, email);
				}
				else
				{
					prest = con.prepareStatement("INSERT into temp_user (firstname,lastname,email,password,designation,privilege,validated) values (?,?,?,SHA(?),?,?,?)");
				}
				prest.setString(1, firstname);
				prest.setString(2, lastname);
				prest.setString(3, email);
				prest.setString(4, password);
				prest.setString(5, new String("Temporary User"));
				prest.setInt(6, 1);
				prest.setInt(7, 0);
			}
			
			value = prest.executeUpdate();
	
			if(value == 0)
			{
				stmt.close();
				con.close();
				
				addActionError("FATAL ERROR: Unable to send Request to Admin!!");
				return ERROR;
			}
			else
			{
				String code = send_email();
				if(!code.isEmpty())
				{
					addActionMessage("Please validate the email account by clicking on the link sent to the email address '" + email + "'");
					session.put("previlige","1");
					session.put("logged-in","true");  
			    	session.put("name", firstname + " " + lastname);
					query = "update temp_user set confirm_code='"+ code + "' where email='"+ email + "'" ;
					stmt.executeUpdate(query);
					stmt.close();
					con.close();
					return SUCCESS;
				}
				else
				{
					addActionMessage("Error while trying to register the account!!");
					query = "delete from temp_user where email='"+ email + "';" ;
					stmt.executeUpdate(query);
					stmt.close();
					con.close();
					return ERROR;
				}
			}
		}
		catch (Exception e) {
			addActionError(e.toString());
			return ERROR;
		}
	}

	@RequiredStringValidator(message="Please enter firstname")
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String value) {
		firstname = value;
	}

	@RequiredStringValidator(message="Please enter lastname")
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String value) 
	{
		lastname = value;
	}

	@RequiredStringValidator(message="Please enter email address")
	@EmailValidator(message="You must enter a valid email address")
	public String getEmail() 
	{
		return email;
	}

	public void setEmail(String value) 
	{
		email = value;
	}

	@RegexFieldValidator(expression="(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{8,10})$",message="Password must be between 8 and 10 characters, contain at least one digit and one alphabetic character, and must not contain special characters.")
	public String getPassword() 
	{
		return password;
	}

	public void setPassword(String value) 
	{
		password = value;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
		
	}

	public void setCaptcha_response(String captcha_response) {
		this.captcha_response = captcha_response;
	}

	public String getCaptcha_response() {
		return captcha_response;
	}
} 