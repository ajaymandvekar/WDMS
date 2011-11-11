package net.wdmsfunc;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

import java.sql.*;
import java.util.Map;

import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;

import org.apache.struts2.interceptor.SessionAware;

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
	
	public String execute() throws Exception {
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String pass = "aj";
		Connection con=null;
		Statement stmt=null;
		int value = 0;
		String query = null;
		
		try{
			
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
					addActionMessage("ERROR: User already exists with username: "+ email + " !");
					return INPUT;			
				}
			}
				
			stmt = con.createStatement();
			query = "INSERT into user VALUES (0,'" + firstname + "','" + lastname + "','" + email + "',SHA('" + password + "'),'Temporary User',1,'')" ;
			if(stmt !=null)
				value = stmt.executeUpdate(query);
	
			if(value == 0)
			{
				addActionError("FATAL ERROR: Unable to send Request to Admin!!");
				return ERROR;
			}
			else
			{
				addActionMessage("Request Sent to Admin for approval!!.. Please have patience until request approved!!");
				session.put("name",firstname+ " " +lastname);
				return SUCCESS;
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
} 