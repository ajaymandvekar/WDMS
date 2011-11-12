package net.wdmsfunc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

public class ValidateEmailAccount extends ActionSupport implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5400200415780362816L;
	private String code;
	String url = "jdbc:mysql://localhost:3306/";
	String dbName = "wdms";
	String driverName = "org.gjt.mm.mysql.Driver";
	String userName = "root";
	String pass = "aj";
	Connection con=null;
	Statement stmt=null;
	int value = 0;
	String query = null;
	int userid = 0;
	String nfirstname = "";
	String nlastname = "";
	String nemail = "";
	String npassword = "";
	private Map session;

	public String execute() throws Exception {
		if(getCode() == null || getCode().isEmpty())
		{
			addActionMessage("Invalid User");
			return ERROR;
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
		
		//Get the details of the user for the confirm code
		PreparedStatement prest = con.prepareStatement("Select userid,firstname,lastname,email,password from temp_user where confirm_code=?");
		prest.setString(1,getCode());
		ResultSet rs = prest.executeQuery();
		if(rs.next())
		{
			userid = rs.getInt(1);
			nfirstname = rs.getString(2);
			nlastname = rs.getString(3);
			nemail = rs.getString(4);
			npassword = rs.getString(5);
		}
		rs.close();
		prest.clearParameters();
		prest.close();

		//If the user is present for the confirm code
		if(userid > 0)
		{
			//Insert the user into the queue of the admin for assigning proper priviliges
			prest = con.prepareStatement("INSERT into user (firstname,lastname,email,password,designation,privilege,department_ids) values (?,?,?,?,?,?,?)");
			prest.setString(1, nfirstname);
			prest.setString(2, nlastname);
			prest.setString(3, nemail);
			prest.setString(4, npassword);
			prest.setString(5, "Temporary User");
			prest.setInt(6, 1);
			prest.setString(7, "");
			
			value = prest.executeUpdate();

			prest.clearParameters();
			prest.close();
			
			//If insert into queue is successfull 
			if(value>0)
			{
				//Delete the request from the user
				prest = con.prepareStatement("delete from temp_user where confirm_code=?");
				prest.setString(1, getCode());
				prest.executeUpdate();
				prest.clearParameters();
				prest.close();
				con.close();
				
				//Make session for temporary user
				session.put("previlige","1");
				session.put("logged-in","true");
				session.put("userid", userid);  
				session.put("name", nfirstname + " " + nlastname);
				
			
				addActionMessage("Thank you for validating the email account.. Please be patient till administrator assigns you a proper role!!");
				return SUCCESS;
			}
			else
			{
				con.close();
				addActionMessage("Invalid User");
				return ERROR;
			}
		}
		else
		{
			addActionMessage("Invalid User");
			return ERROR;
		}
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
