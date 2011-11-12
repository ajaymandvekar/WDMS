package net.wdmsfunc;

import net.wdmsfunc.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.opensymphony.xwork2.validator.annotations.EmailValidator;

public class AdminAction extends ActionSupport implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7175000579131983065L;

	private String userid = "";
	private String firstname = "";
	private String email="";
	private String lastname = "";
	private String password = "";
	private String password_confirm = "";
	private Map session;
	private List<User> newrequest = new ArrayList<User>();
	private List<SystemLogEntry> logentries = null;

	private List<String> department_names;
	private String curr_department;

	private HashMap<Integer,List<String>> user_depart_sel;
	private HashMap<Integer,String> user_desig_sel;

	private List<String> designation_names;
	private String curr_designation;

	/** Get system log file **/
	public String admin_system_log()
	{
		logentries = Log.getSystemLogEntries();
		return SUCCESS;
	}



	/** Validation Methods for Admin functions **/
	public int validate_user_already_exists() throws SQLException
	{
		//check if user already exists
		int result = 0;
		try{
			String url = "jdbc:mysql://localhost:3306/";
			String dbName = "wdms";
			String driverName = "org.gjt.mm.mysql.Driver";
			String userName = "root";
			String pass = "aj";
			Connection con=null;
			Statement stmt=null;

			try{
				Class.forName(driverName).newInstance();
				con=DriverManager.getConnection(url+dbName, userName,pass);
				stmt=con.createStatement();
			}
			catch(Exception e){
				addActionError(e.toString());
			}

			stmt = con.createStatement();

			if(stmt !=null)
			{
				String query1 = "Select * from user,temp_user where user.email='" + email + "' or temp_user.email='" +email +"'";
				// execute the query, and get a java resultset
				ResultSet rs = stmt.executeQuery(query1);
				// iterate through the java resultset
				rs.last();
				int rowCount = rs.getRow();
				if(rowCount > 0)
				{
					addActionMessage("ERROR: User already exists, or user already has sent request without validation his email account!");
					result = 1;
				}
			}

		}	
		catch (Exception e) {
			result = 1;
		}
		return result;

	}





	/** Validation Methods for Admin functions **/
	public int validate_role_selection(boolean add_account_check) throws SQLException
	{

		int num_sel_dept = 0;
		int result = 0;
		String desig = curr_designation;

		if(add_account_check)
		{
			if(firstname.isEmpty())
			{
				addActionMessage("Please enter firstname");
				result = 1;
			}
			if(lastname.isEmpty())
			{
				addActionMessage("Please enter lastname");
				result = 1;
			}
			if(email.isEmpty())
			{
				addActionMessage("Please enter email");
				result = 1;
			}
			else
			{
				// check email validation
				Pattern p = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
				Matcher m = p.matcher(email);
				boolean b = m.matches();
				if(!b)
				{
					addActionMessage("Please enter valid email address");
					result = 1;
				}
			}
			if(password.isEmpty())
			{
				addActionMessage("Please check password field");
				result = 1;
			}
			else
			{
				// check password validation
				Pattern p = Pattern.compile("(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{8,10})$");
				Matcher m = p.matcher(password);
				boolean b = m.matches();
				if(!b)
				{
					addActionMessage("Password must be between 8 and 10 characters, contain at least one digit and one alphabetic character, and must not contain special characters.");
					result = 1;
				}
			}

			if(!password_confirm.equals(password))
			{
				addActionMessage("Please check password fields");	
				addActionMessage("Please confirm password fields");
				result = 1;
			}
		}

		//Convert the departments into ids		  
		if(curr_department != null)
		{
			if(!curr_department.isEmpty())
			{
				String delimiter = ",";
				String[] temp;

				/* given string will be split by the argument delimiter provided. */
				temp = curr_department.split(delimiter);
				num_sel_dept = temp.length;
			}
		}	

		//Check if degisnation is selected for the current user
		if(!desig.equalsIgnoreCase("-1"))
		{	
			if(add_account_check)
			{
				if(desig.equalsIgnoreCase("Corporate Manager") && num_sel_dept == 0)
				{
					addActionMessage("Please select department");
					result = 1;
				}
				else if((desig.equalsIgnoreCase("Department Manager") || desig.equalsIgnoreCase("Regular Employees") || desig.equalsIgnoreCase("System Administrator")) && (num_sel_dept == 0 || num_sel_dept > 1))
				{
					addActionMessage("Please select one department only");
					result = 1;
				}
				else if(desig.equalsIgnoreCase("Temporary User") && num_sel_dept !=0 )
				{
					addActionMessage("Please dont select department");
					result = 1;
				}
				else if(desig.equalsIgnoreCase("Guest User") && num_sel_dept != 0)
				{
					addActionMessage("Please dont select department");
					result = 1;
				}
			}
			else
			{
				if(desig.equalsIgnoreCase("Corporate Manager") && num_sel_dept == 0)
				{
					addActionMessage("Please select one or more departments for the designation:Corporate Manager");
					result = 1;
				}
				else if((desig.equalsIgnoreCase("Department Manager") || desig.equalsIgnoreCase("Regular Employees") || desig.equalsIgnoreCase("System Administrator")) && (num_sel_dept == 0 || num_sel_dept > 1))
				{
					addActionMessage("Please select only one department");
					result = 1;
				}
				else if(desig.equalsIgnoreCase("Temporary User") && num_sel_dept !=0 )
				{
					addActionMessage("Temporary user doesn't has any department.. Please uncheck all departments.");
					result = 1;
				}
				else if(desig.equalsIgnoreCase("Guest User") && num_sel_dept != 0)
				{
					addActionMessage("Guest user doesn't has any department.. Please uncheck all departments.");
					result = 1;
				}
			}
		}
		else 
		{
			if(add_account_check)		
			{
				//No designation for the current user
				addActionMessage("Please select Designation.");
				if(num_sel_dept == 0)
				{
					addActionMessage("Please select Department.");
				}
			}
			else
			{
				addActionMessage("Please select designation");
			}
			result = 1;
		}

		return result;
	}

	/** Get All the users currently in the database that have been granted access **/
	public String admin_current_users() throws SQLException
	{
		if(!session.containsKey("userid"))
		{
			return ERROR;
		}

		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String passd = "aj";
		Connection con=null;
		Statement stmt=null;
		String query = null;
		ResultSet rs = null;

		HashMap<Integer, String> departments = new HashMap<Integer, String>(); 

		department_names = new ArrayList<String>();
		designation_names = new ArrayList<String>();

		try
		{  
			Class.forName(driverName).newInstance();
			con = DriverManager.getConnection(url+dbName, userName,passd);
			stmt = con.createStatement();

			if(stmt !=null)
			{	
				user_depart_sel = new HashMap<Integer, List<String>>();
				user_desig_sel = new HashMap<Integer, String>();

				query = "Select * from department";
				rs = stmt.executeQuery(query);
				while(rs.next())
				{
					departments.put(rs.getInt("deptid"), rs.getString("department_name"));
					department_names.add(rs.getString("department_name"));
				}

				query = "Select * from user where privilege > 1";
				// execute the query, and get a java resultset
				rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					ArrayList<String> user_dept_check = new ArrayList<String>();
					int userid = rs.getInt("userid");
					String fname = rs.getString("firstname");
					String lname = rs.getString("lastname");
					String email = rs.getString("email");
					String pass = rs.getString("password");
					String designation = rs.getString("designation");
					int  privilege = rs.getInt("privilege");

					ArrayList<Integer> deptids = new ArrayList<Integer>();
					String dpids_str = rs.getString("department_ids");
					if(!dpids_str.isEmpty())
					{
						String delimiter = ",";
						String[] temp;
						/* given string will be split by the argument delimiter provided. */
						temp = dpids_str.split(delimiter);
						/* print substrings */
						for(int i =0; i < temp.length ; i++)
						{
							deptids.add(Integer.parseInt(temp[i]));
							user_dept_check.add(departments.get(Integer.parseInt(temp[i])));
						}
					}

					user_depart_sel.put(userid,user_dept_check);
					user_desig_sel.put(userid,designation);

					newrequest.add(new User(userid, fname, lname, email, pass, designation,privilege,deptids));
				}

				stmt.close();
			}

			stmt = con.createStatement();
			//Fill Designation Display List
			if(stmt !=null)
			{
				query = "Select designation from previlige where prvid > 1";
				// execute the query, and get a java resultset
				rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					designation_names.add(rs.getString("designation"));
				}
				stmt.close();
			}

			return SUCCESS;
		}
		catch (Exception e) 
		{
			addActionMessage(e.toString());
			return ERROR;
		}	
	}

	/** Get All pending requests for authorization/denial **/
	public String admin_requests() throws SQLException
	{
		if(!session.containsKey("userid"))
		{
			return ERROR;
		}

		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String passd = "aj";
		Connection con=null;
		Statement stmt=null;

		department_names = new ArrayList<String>();
		designation_names = new ArrayList<String>();

		try
		{  
			Class.forName(driverName).newInstance();
			con = DriverManager.getConnection(url+dbName, userName,passd);
			stmt = con.createStatement();

			if(stmt !=null)
			{
				String query = "Select * from user where privilege=1";
				// execute the query, and get a java resultset
				ResultSet rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					int userid = rs.getInt("userid");
					String fname = rs.getString("firstname");
					String lname = rs.getString("lastname");
					String email = rs.getString("email");
					String pass = rs.getString("password");
					String designation = rs.getString("designation");
					int  privilege = rs.getInt("privilege");

					ArrayList<Integer> deptids = new ArrayList<Integer>();
					String dpids_str = rs.getString("department_ids");
					if(!dpids_str.isEmpty())
					{
						String delimiter = ",";
						String[] temp;
						/* given string will be split by the argument delimiter provided. */
						temp = dpids_str.split(delimiter);
						/* print substrings */
						for(int i =0; i < temp.length ; i++)
						{
							deptids.add(Integer.parseInt(temp[i]));
						}

					}

					newrequest.add(new User(userid, fname, lname, email, pass, designation,privilege,deptids));
				}

				stmt.close();
			}


			stmt = con.createStatement();
			//Fill Department Display List
			if(stmt !=null)
			{
				String query = "Select * from department";
				// execute the query, and get a java resultset
				ResultSet rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					String deptname = rs.getString("department_name");
					department_names.add(deptname);
				}
				stmt.close();
			}

			stmt = con.createStatement();
			//Fill Department Display List
			if(stmt !=null)
			{
				String query = "Select designation from previlige  where prvid > 1";
				// execute the query, and get a java resultset
				ResultSet rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					designation_names.add(rs.getString("designation"));
				}
				stmt.close();
			}

			return SUCCESS;
		}
		catch (Exception e) 
		{
			addActionMessage(e.toString());
			return ERROR;
		}
	}

	/** Approve a request **/
	public String approve_request() throws SQLException
	{
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String passd = "aj";
		Connection con=null;
		Statement stmt=null;
		String query = null;

		int privilege_level = 0;
		int result_validate = 0;
		int flag = 0;
		String dept_ids_str = "";

		if(!session.containsKey("userid"))
		{
			return ERROR;
		}

		try{
			//Validate User Inputs - Designation and Department List
			result_validate = validate_role_selection(false);
			if(result_validate > 0)
			{
				return "user_validation_error_view";
			}


			try{
				Class.forName(driverName).newInstance();
				con=DriverManager.getConnection(url+dbName, userName,passd);
				stmt=con.createStatement();
			}
			catch(Exception e){
				System.out.println(e.getMessage());
				addActionError("Database connectivity Issue!!");
				return ERROR;
			}

			stmt = con.createStatement();
			//Get privilege level of the user
			if(stmt !=null)
			{
				query = "Select prvid from previlige where designation='"+curr_designation+"'";
				// execute the query, and get a java resultset
				ResultSet rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					privilege_level = rs.getInt("prvid");
				}
				stmt.close();
			}

			if(privilege_level != 0 && privilege_level != 1)
			{
				query = "Select deptid from department where ";

				//Convert the departments into ids		  
				if(!curr_department.isEmpty())
				{
					String delimiter = ",";
					String[] temp;
					/* given string will be split by the argument delimiter provided. */
					temp = curr_department.split(delimiter);
					/* print substrings */

					for(int i =0; i < temp.length ; i++)
					{
						query = query + " department_name='"+ temp[i].trim() + "'";
						if(i+1 < temp.length)
						{
							query = query + " or";
						}
					}
				}	

				stmt = con.createStatement();
				//Get privilege level of the user
				if(stmt !=null)
				{
					// execute the query, and get a java resultset
					ResultSet rs = stmt.executeQuery(query);
					// iterate through the java resultset
					while (rs.next())
					{
						if(flag == 0)
						{
							dept_ids_str = dept_ids_str + String.valueOf(rs.getInt("deptid"));
							flag = 1;
						}
						else
						{
							dept_ids_str = dept_ids_str + "," + String.valueOf(rs.getInt("deptid"));
						}
					}
					stmt.close();
				}
			}
			else
			{
				dept_ids_str = "";
			}

			stmt = con.createStatement();
			int val = 0;
			if(stmt !=null)
			{
				query = "Update user set designation='" + curr_designation + "',privilege=" + privilege_level + ",department_ids='" + dept_ids_str + "' where userid=" + userid;
				val = stmt.executeUpdate(query);
			}

			if(val == 0)
			{
				return ERROR;
			}
			else
			{
				addActionMessage("User Approved Successfully!");
				return "user_approve_success";
			}
		}
		catch (Exception e) {
			addActionMessage(e.toString());
			return ERROR;
		}
	}

	/** Deny a request **/
	public String deny_request() throws SQLException
	{
		if(!session.containsKey("userid"))
		{
			return ERROR;
		}

		System.out.println("Deny Request Function");

		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String passd = "aj";
		Connection con=null;
		Statement stmt=null;
		String query = null;

		try {
			try{
				Class.forName(driverName).newInstance();
				con=DriverManager.getConnection(url+dbName, userName,passd);
				stmt=con.createStatement();
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}

			stmt = con.createStatement();

			int val = 0;
			if(stmt !=null)
			{
				query = "Delete from user where userid=" + userid;
				val = stmt.executeUpdate(query);
				stmt.close();
			}
			if(val == 0)
			{
				return ERROR;
			}
			else
			{
				addActionMessage("User Denied Successfully!");
				return "user_deny_success";
			}
		}
		catch (Exception e) {
			addActionMessage(e.toString());
			return ERROR;
		}
	}

	/** Delete user account **/
	public String delete_account() throws SQLException
	{
		if(!session.containsKey("userid"))
		{
			return ERROR;
		}

		System.out.println("Deny Request Function");

		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String passd = "aj";
		Connection con=null;
		Statement stmt=null;
		String query = null;

		try {
			try{
				Class.forName(driverName).newInstance();
				con=DriverManager.getConnection(url+dbName, userName,passd);
				stmt=con.createStatement();
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}

			stmt = con.createStatement();

			int val = 0;
			if(stmt !=null)
			{
				query = "Delete from user where userid=" + userid;
				val = stmt.executeUpdate(query);
				stmt.close();
			}
			if(val == 0)
			{
				return ERROR;
			}
			else
			{
				addActionMessage("User Deleted Successfully!");
				return "user_account_deleted_success";
			}
		}
		catch (Exception e) {
			addActionMessage(e.toString());
			return ERROR;
		}
	}

	/**Deny a existing user**/
	public String deny_existing_user() throws SQLException
	{
		if(!session.containsKey("userid"))
		{
			return ERROR;
		}

		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String passd = "aj";
		Connection con=null;
		Statement stmt=null;
		String query = null;

		try {
			try{
				Class.forName(driverName).newInstance();
				con=DriverManager.getConnection(url+dbName, userName,passd);
				stmt=con.createStatement();
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}

			stmt = con.createStatement();

			int val = 0;
			if(stmt !=null)
			{
				query = "Delete from user where userid=" + userid;
				val = stmt.executeUpdate(query);
				stmt.close();
			}
			if(val == 0)
			{
				addActionError("Error while deleting user!!");
				return "existing_user_deny_error";
			}
			else
			{
				addActionMessage("User Denied Successfully!");
				return "existing_user_deny_success";
			}
		}
		catch (Exception e) {
			addActionMessage(e.toString());
			return ERROR;
		}
	}

	/** Populate the department list and designation List**/
	public String admin_populate_list() throws SQLException{

		if(!session.containsKey("userid"))
		{
			return ERROR;
		}

		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String passd = "aj";
		Connection con=null;
		Statement stmt=null;
		String query = null;
		ResultSet rs = null;

		HashMap<Integer, String> departments = new HashMap<Integer, String>(); 

		department_names = new ArrayList<String>();
		designation_names = new ArrayList<String>();

		try
		{  
			Class.forName(driverName).newInstance();
			con = DriverManager.getConnection(url+dbName, userName,passd);
			stmt = con.createStatement();

			if(stmt !=null)
			{	
				query = "Select * from department";
				rs = stmt.executeQuery(query);
				while(rs.next())
				{
					departments.put(rs.getInt("deptid"), rs.getString("department_name"));
					department_names.add(rs.getString("department_name"));
				}

				stmt.close();
			}

			stmt = con.createStatement();
			//Fill Designation Display List
			if(stmt !=null)
			{
				query = "Select designation from previlige  where prvid > 1";
				// execute the query, and get a java resultset
				rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					designation_names.add(rs.getString("designation"));
				}
				stmt.close();
			}

			return SUCCESS;
		}
		catch (Exception e) 
		{
			addActionMessage(e.toString());
			return ERROR;
		}	
	}

	/**Add New User**/
	public String add_new_account() throws SQLException{
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String passd = "aj";
		Connection con=null;
		Statement stmt=null;
		String query = null;
		PreparedStatement prest = null;

		int privilege_level = 0;
		int result_validate = 0;
		int flag = 0;
		String dept_ids_str = "";

		if(!session.containsKey("userid"))
		{
			return ERROR;
		}

		try{
			//Validate User Inputs - Designation and Department List
			if(validate_user_already_exists() > 0)
			{
				return "user_validation_error_add";
			}
			
			if(validate_role_selection(true) > 0)
			{
				return "user_validation_error_add";
			}


			try{
				Class.forName(driverName).newInstance();
				con=DriverManager.getConnection(url+dbName, userName,passd);
				stmt=con.createStatement();
			}
			catch(Exception e){
				System.out.println(e.getMessage());
				addActionError("Database connectivity Issue!!");
				return ERROR;
			}

			stmt = con.createStatement();
			//Get privilege level of the user
			if(stmt !=null)
			{
				query = "Select prvid from previlige where designation='"+curr_designation+"'";
				// execute the query, and get a java resultset
				ResultSet rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					privilege_level = rs.getInt("prvid");
				}
				stmt.close();
			}

			if(privilege_level > 2)
			{
				query = "Select deptid from department where ";

				//Convert the departments into ids		  
				if(!curr_department.isEmpty())
				{
					String delimiter = ",";
					String[] temp;
					/* given string will be split by the argument delimiter provided. */
					temp = curr_department.split(delimiter);
					/* print substrings */

					for(int i =0; i < temp.length ; i++)
					{
						query = query + " department_name='"+ temp[i].trim() + "'";
						if(i+1 < temp.length)
						{
							query = query + " or";
						}
					}
				}	

				stmt = con.createStatement();
				//Get privilege level of the user
				if(stmt !=null)
				{
					// execute the query, and get a java resultset
					ResultSet rs = stmt.executeQuery(query);
					// iterate through the java resultset
					while (rs.next())
					{
						if(flag == 0)
						{
							dept_ids_str = dept_ids_str + String.valueOf(rs.getInt("deptid"));
							flag = 1;
						}
						else
						{
							dept_ids_str = dept_ids_str + "," + String.valueOf(rs.getInt("deptid"));
						}
					}
					stmt.close();
				}
			}
			else
			{
				dept_ids_str = "";
			}

			int val = 0;
			prest = con.prepareStatement("INSERT into user (firstname,lastname,email,password,designation,privilege,department_ids) values (?,?,?,SHA(?),?,?,?)");
			prest.setString(1, firstname);
			prest.setString(2, lastname);
			prest.setString(3, email);
			prest.setString(4, password);
			prest.setString(5, curr_designation);
			prest.setInt(6, privilege_level);
			prest.setString(7, dept_ids_str);

			val = prest.executeUpdate();

			if(val == 0)
			{
				addActionMessage("Unable to add user account!");
				return "user_validation_error_add";
			}
			else
			{
				addActionMessage("User Account Added Successfully!");
				return "user_account_add_success";
			}
		}
		catch (Exception e) {
			addActionMessage(e.toString());
			return ERROR;
		}
	}

	/** Update a user information **/
	public String update_user_info() throws SQLException
	{
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String passd = "aj";
		Connection con=null;
		Statement stmt=null;

		int privilege_level = 1;
		int result_validate = 0;
		String dept_ids_str = "";
		String query = null;
		PreparedStatement prest = null;

		try {
			//Validate User Inputs - Designation and Department List
			result_validate = validate_role_selection(false);
			if(result_validate > 0)
			{
				return "user_validation_error_modify";
			}

			try{
				Class.forName(driverName).newInstance();
				con=DriverManager.getConnection(url+dbName, userName,passd);
				stmt=con.createStatement();
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}

			stmt = con.createStatement();
			//Get privilege level of the user
			if(stmt !=null)
			{
				query = "Select prvid from previlige where designation='"+curr_designation+"'";
				// execute the query, and get a java resultset
				ResultSet rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					privilege_level = rs.getInt("prvid");
				}
				stmt.close();
			}

			if(privilege_level > 2)
			{
				query = "Select deptid from department where ";
				//Convert the departments into ids
				if(!curr_department.isEmpty())
				{
					String delimiter = ",";
					String[] temp;
					/* given string will be split by the argument delimiter provided. */
					temp = curr_department.split(delimiter);
					/* print substrings */

					for(int i =0; i < temp.length ; i++)
					{
						query = query + " department_name='"+ temp[i].trim() + "'";
						if(i+1 < temp.length)
						{
							query = query + " or";
						}
					}
				}	

				int flag = 0;

				stmt = con.createStatement();
				if(stmt !=null)
				{
					ResultSet rs = stmt.executeQuery(query);
					while (rs.next())
					{
						if(flag == 0)
						{
							dept_ids_str = dept_ids_str + String.valueOf(rs.getInt("deptid"));
							flag = 1;
						}
						else
						{
							dept_ids_str = dept_ids_str + "," + String.valueOf(rs.getInt("deptid"));
						}
					}
					stmt.close();
				}
			}

			int val = 0;
			prest = con.prepareStatement("Update user set firstname=?,lastname=?,email=?,designation=?,privilege=?,department_ids=? where userid=?");
			prest.setString(1, firstname);
			prest.setString(2, lastname);
			prest.setString(3, email);
			prest.setString(4, curr_designation);
			prest.setInt(5, privilege_level);
			prest.setString(6, dept_ids_str);
			prest.setString(7, userid);
			val = prest.executeUpdate();
			
			if(val == 0)
			{
				addActionMessage("Unsuccessfull to update user Info!");
				return INPUT;
			}
			else
			{
				addActionMessage("User Info updated Successfully!");
				return "user_update_success";
			}
		}
		catch (Exception e) {
			addActionMessage(e.toString());
			return ERROR;
		}
	}

	/** List of accounts that can be deleted **/
	public String admin_current_users_delete_op() {
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String passd = "aj";
		Connection con=null;
		Statement stmt=null;
		String query = null;
		ResultSet rs = null;

		HashMap<Integer, String> departments = new HashMap<Integer, String>(); 

		department_names = new ArrayList<String>();
		designation_names = new ArrayList<String>();

		try
		{  
			Class.forName(driverName).newInstance();
			con = DriverManager.getConnection(url+dbName, userName,passd);
			stmt = con.createStatement();

			if(stmt !=null)
			{	
				user_depart_sel = new HashMap<Integer, List<String>>();
				user_desig_sel = new HashMap<Integer, String>();

				query = "Select * from department";
				rs = stmt.executeQuery(query);
				while(rs.next())
				{
					departments.put(rs.getInt("deptid"), rs.getString("department_name"));
					department_names.add(rs.getString("department_name"));
				}

				query = "Select * from user where privilege >= 2";
				// execute the query, and get a java resultset
				rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					ArrayList<String> user_dept_check = new ArrayList<String>();
					int userid = rs.getInt("userid");
					String fname = rs.getString("firstname");
					String lname = rs.getString("lastname");
					String email = rs.getString("email");
					String pass = rs.getString("password");
					String designation = rs.getString("designation");
					int  privilege = rs.getInt("privilege");

					ArrayList<Integer> deptids = new ArrayList<Integer>();
					String dpids_str = rs.getString("department_ids");
					if(!dpids_str.isEmpty())
					{
						String delimiter = ",";
						String[] temp;
						/* given string will be split by the argument delimiter provided. */
						temp = dpids_str.split(delimiter);
						/* print substrings */
						for(int i =0; i < temp.length ; i++)
						{
							deptids.add(Integer.parseInt(temp[i]));
							user_dept_check.add(departments.get(Integer.parseInt(temp[i])));
						}
					}

					user_depart_sel.put(userid,user_dept_check);
					user_desig_sel.put(userid,designation);

					newrequest.add(new User(userid, fname, lname, email, pass, designation,privilege,deptids));
				}

				stmt.close();
			}

			stmt = con.createStatement();
			//Fill Designation Display List
			if(stmt !=null)
			{
				query = "Select designation from previlige  where prvid > 1";
				// execute the query, and get a java resultset
				rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					designation_names.add(rs.getString("designation"));
				}
				stmt.close();
			}

			return SUCCESS;
		}
		catch (Exception e) 
		{
			addActionMessage(e.toString());
			return ERROR;
		}			
	}

	public String execute() throws Exception {	
		@SuppressWarnings("rawtypes")
		Map session = null;
		try{
			session = ActionContext.getContext().getSession();
			if(Boolean.getBoolean(session.get("logged-in").toString()))
			{
				return SUCCESS;
			}
			else
			{
				return ERROR;
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return ERROR;
		}
	}

	/** Getter and Setter Methods **/

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<User> getNewrequest() {
		return this.newrequest;
	}

	public List<SystemLogEntry> getLogentries() {
		return this.logentries;
	}


	public void setDepartment_names(List<String> department_names) {
		this.department_names = department_names;
	}

	public List<String> getDepartment_names() {
		return department_names;
	}

	public void setCurr_department(String curr_department) {
		this.curr_department = curr_department;
	}

	public String getCurr_department() {
		return curr_department;
	}

	public void setDesignation_names(List<String> designation_names) {
		this.designation_names = designation_names;
	}

	public List<String> getDesignation_names() {
		return designation_names;
	}

	public void setCurr_designation(String curr_designation) {
		this.curr_designation = curr_designation;
	}

	public String getCurr_designation() {
		return curr_designation;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setUser_depart_sel(HashMap<Integer,List<String>> user_depart_sel) {
		this.user_depart_sel = user_depart_sel;
	}

	public HashMap<Integer,List<String>> getUser_depart_sel() {
		return user_depart_sel;
	}

	public void setUser_desig_sel(HashMap<Integer,String> user_desig_sel) {
		this.user_desig_sel = user_desig_sel;
	}

	public HashMap<Integer,String> getUser_desig_sel() {
		return user_desig_sel;
	}

	public void setPassword_confirm(String password_confirm) {
		this.password_confirm = password_confirm;
	}

	public String getPassword_confirm() {
		return password_confirm;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;

	}
}
