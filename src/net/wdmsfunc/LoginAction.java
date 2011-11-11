package net.wdmsfunc;


import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.validator.annotations.*;
import org.apache.struts2.dispatcher.SessionMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;
import java.sql.*;


public class LoginAction extends ActionSupport implements SessionAware{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7219561392019888383L;
	private String username = null;
	private String password = null;
	private Map session;
	
	public String execute() throws Exception {
		  String url = "jdbc:mysql://localhost:3306/";
		  String dbName = "wdms";
		  String driverName = "org.gjt.mm.mysql.Driver";
		  String userName = "root";
		  String password = "aj";
		  Connection con=null;
		  Statement stmt=null;
		  
		  try{
			  String curr_username = getUsername();
			  String curr_password = getPassword();
			  int id = -1;
			  int privilege = 0;
			  String firstname = null;
		      String lastname = null;
			  String name = null;
			  
			  Class.forName(driverName).newInstance();
			  con = DriverManager.getConnection(url+dbName, userName,password);
			  stmt = con.createStatement();

			  if(stmt !=null)
			  {
				  String query = "Select userid,privilege,firstname,lastname from user where email='"+ curr_username + "' and password=SHA('"+ curr_password +"')";
				  // execute the query, and get a java resultset
			      ResultSet rs = stmt.executeQuery(query);
			      
			      // iterate through the java resultset
			      while (rs.next())
			      {
			        id = rs.getInt("userid");
			        privilege = rs.getInt("privilege");
			        firstname = rs.getString("firstname");
			        lastname = rs.getString("lastname");
			        name = firstname + " " + lastname;
			      }
			      stmt.close();
			      
			      if(privilege!=0)
			      {    	  
			    	  ((SessionMap)this.session).invalidate();
			    	  this.session = ActionContext.getContext().getSession();
			    	  session.put("logged-in","true");
			    	  session.put("userid", id);  
			    	  session.put("name", name);
			      }
			  }
			  
			  if(privilege == -1) //System Administrator
		      {
				  session.put("previlige","-1");
				  System.out.println("System Administrator logged In");
		    	  return "admin_user";
		      }
			  else if(privilege == 1) //Temporary User
			  {
				  session.put("previlige","1");
		    	  System.out.println("Temporary User logged In");
		    	  return "temporary_user";
			  }
			  else if(privilege == 2) //Guest User
			  {
				  session.put("previlige","2");
				  System.out.println("Guest User logged In");
		    	  return "guest_user";
			  }
			  else if( privilege == 3) //Regular Employees
			  {
				  session.put("previlige","3");
				  System.out.println("Regular Employee logged In");
		    	  return "regular_user";
			  }
			  else if(privilege == 4) //Department Manager 
			  {
				  session.put("previlige","4");
				  System.out.println("Department Manager logged In");
		    	  return "department_manager_user";
			  }
			  else if(privilege == 5) //Corporate Manager
			  {
				  session.put("previlige","5");
				  System.out.println("Corporate Manager logged In");
		    	  return "corporate_manager_user";
			  }
			  else
			  {
				  addActionError("Unauthorized User .. Please register first");
				  return ERROR;
			  }
		  }
		  catch(Exception e)
		  {
			  System.out.println(e.getMessage());
			  return "error";
		  }		  
	}
	
	@RequiredStringValidator(message="Please enter username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@RequiredStringValidator(message="Please enter password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setSession(Map session) {
		this.session = session;
	}
}
