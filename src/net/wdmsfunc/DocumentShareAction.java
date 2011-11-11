package net.wdmsfunc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.wdmsfunc.Log.DocumentOperation;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

public class DocumentShareAction extends ActionSupport implements ServletRequestAware, SessionAware {
	/**
	 * Added Default Serial Version
	 */
	private static final long serialVersionUID = 1L;
	private HttpServletRequest servletRequest;
	private Map session;
	private List<String> privileges;
	private String curr_privilege;
	private int documentid;
	private List<String> users;
	private String curr_user;
	private String documentname;


	public String execute() 
	{
		int userid = 0;
		int previlige = 0;

		try 
		{
			if(session.containsKey("logged-in"))
			{
				userid = Integer.parseInt(session.get("userid").toString());
				previlige = Integer.parseInt(session.get("previlige").toString());
			}
			else
			{
				return "login";
			}

			if((curr_privilege == null) || (curr_user.contentEquals("-1")))
			{
				return "input";
			}

			if(previlige > 1 && previlige < 6)
			{
				// temporary user is not allowed to share documents
				String url = "jdbc:mysql://localhost:3306/";
				String dbName = "wdms";
				String driverName = "org.gjt.mm.mysql.Driver";
				String userName = "root";
				String passd = "aj";
				Connection con=null;
				Statement stmt=null;
				String query = null;
				String delimiter = ",";
				String[] temp;

				try
				{
					Class.forName(driverName).newInstance();
					con=DriverManager.getConnection(url+dbName, userName,passd);

					//Check if the logged in user is actually the owner of the document
					stmt=con.createStatement();
					if(stmt !=null)
					{
						query = "Select doc_userid from document where docid ="+documentid;
						// execute the query, and get a java resultset
						ResultSet rs = stmt.executeQuery(query);

						// iterate through the java resultset
						int orgDocAuthor = 0;
						while (rs.next())
						{  
							orgDocAuthor = rs.getInt("doc_userid");
						}
						stmt.close();

						if(orgDocAuthor != userid)
						{
							con.close();
							return "login";					
						}

					}

					//find user user id of curr_user (email id)
					stmt=con.createStatement();
					int curr_user_id = 0;
					if(stmt !=null)
					{
						query = "Select userid from user where email='"+ curr_user +"'";
						ResultSet rs = stmt.executeQuery(query);
						while (rs.next())
						{  
							curr_user_id = rs.getInt("userid");
						}
						stmt.close();
						if(curr_user_id == 0)
						{
							con.close();
							return "login";					
						}		
					}

					/* Get the current privileges for the shared user */
					temp = curr_privilege.split(delimiter);
					int read = 0,update = 0,ci = 0,str_i = 0;
					while(str_i != temp.length)
					{
						temp[str_i] = temp[str_i].trim();
						if(temp[str_i].equals("Read"))
							read = 1;
						else if(temp[str_i].equals("Update"))
							update = 1;
						else if(temp[str_i].equals("Check-in"))
							ci = 1;

						str_i++;
					}

					stmt=con.createStatement();
					if(stmt !=null)
					{
						int count_doc = 0;
						int check_st = 0;
						int check_docid = 0;
						int val = 0;

						//Check if the document has already been shared with the user
						query = "Select document.checkin_status,document.doc_checked_userid from document,shared_documents where document.docid="+documentid+" and document.docid=shared_documents.doc_id and shared_documents.shared_userid="+curr_user_id+" and shared_documents.from_userid = "+userid;
						ResultSet rs = stmt.executeQuery(query);
						while(rs.next())
						{
							check_st = rs.getInt(1);
							check_docid = rs.getInt(2);
							count_doc ++;
						}

						//If the document already has been shared with the user is true
						if(count_doc>0)
						{	
							//first update all privileges to 0
							query = "Update shared_documents set read_doc=0,update_doc=0,Check_in=0 where doc_id ="+documentid+" and shared_userid = "+curr_user_id+" and from_userid = "+userid+"";
							val = stmt.executeUpdate(query);
							if(val == 0)
							{		
								//error resetting old access privileges 
								stmt.close();
								con.close();
								Log.document_operation(documentid, userid, DocumentOperation.SHARE, "share_operation", "INPUT");
								return "input";
							}
							else
							{		
								//set new access privileges
								query = "Update shared_documents set read_doc="+read+",update_doc="+update+",Check_in="+ci+" where doc_id ="+documentid+" and shared_userid="+curr_user_id+" and from_userid="+userid;
								val = stmt.executeUpdate(query);
								if(val == 0)
								{		
									//error setting new access privileges
									stmt.close();
									con.close();
									Log.document_operation(documentid, userid, DocumentOperation.SHARE, "share_operation", "INPUT");
									return "input";
								}
								else
								{
									//Check in document of the user whose access privileges are being modified if user is revoking his access
									if(check_st==1 && check_docid==curr_user_id && ci==0)
									{
										query = "Update document set doc_checked_userid=0,checkin_status=0";
										val = stmt.executeUpdate(query);
										if(val == 0)
										{		
											//error setting new access privileges
											stmt.close();
											con.close();
											Log.document_operation(documentid, userid, DocumentOperation.SHARE, "share_operation", "INPUT");
											return "input";
										}
										else
										{
											Log.document_operation(documentid, userid, DocumentOperation.SHARE, "share_operation", "UPDATED");
											addActionMessage("User already had checked-out document.. his access privileges updated and document checked-in back");
										}
									}
								}
							}
						}
						else
						{
							//enter the information into the shared document table
							query = "Insert into shared_documents values(0,"+ documentid + "," + userid + "," + curr_user_id + "," + read + "," + update + "," + ci + ")"; 
							val = stmt.executeUpdate(query);
							if(val == 0)
							{
								//error setting new access privileges
								stmt.close();
								con.close();
								Log.document_operation(documentid, userid, DocumentOperation.SHARE, "share_operation", "INPUT");
								return "input";
							}
						}

						stmt.close();																	
					}							
					con.close();
				}
				catch(Exception e)
				{
					Log.document_operation(documentid, userid, DocumentOperation.SHARE, "share_operation", "FAIL");
					addActionError("Database connectivity Issue!!");
					return ERROR;
				}	
			}
			else
			{
				Log.document_operation(documentid, userid, DocumentOperation.SHARE, "share_operation", "FAIL");
				addActionError("No access previlige to share!!");
				return "login";
			}
		}
		catch (Exception e) 
		{
			addActionError(e.getMessage());
			return ERROR;
		}
		
		Log.document_operation(documentid, userid, DocumentOperation.SHARE, "share_operation", "SUCCESS");
		addActionMessage("Selected document has been shared with user "+curr_user+" with the "+curr_privilege+" privileges");
		return SUCCESS;
	}

	public String populate_privileges()
	{
		int userid = 0;

		try 
		{
			if(session.containsKey("logged-in"))
			{
				userid = Integer.parseInt(session.get("userid").toString());
			}
			else
			{
				return "login";
			}

			// populate privilege list
			this.privileges = new ArrayList<String>();
			this.privileges.add("Read");
			this.privileges.add("Update");
			this.privileges.add("Check-in");

			//populate user list
			String url = "jdbc:mysql://localhost:3306/";
			String dbName = "wdms";
			String driverName = "org.gjt.mm.mysql.Driver";
			String userName = "root";
			String passd = "aj";
			Connection con=null;
			Statement stmt=null;
			String query = null;

			try{
				Class.forName(driverName).newInstance();
				con=DriverManager.getConnection(url+dbName, userName,passd);
				stmt=con.createStatement();
				//Get privilege level of the user
				if(stmt !=null)
				{
					this.users = new ArrayList<String>();
					query = "Select email from user where privilege > 1 and userid <> "+ userid +"";
					// execute the query, and get a java resultset
					ResultSet rs = stmt.executeQuery(query);

					// iterate through the java resultset
					while (rs.next())
					{  
						String email = new String(rs.getString("email"));
						this.users.add(email);
					}
					stmt.close();
				}

				stmt=con.createStatement();
				//Get documents of the user to share
				if(stmt !=null)
				{
					query = "Select doc_name from document where docid = "+documentid;
					// execute the query, and get a java resultset
					ResultSet rs = stmt.executeQuery(query);

					// iterate through the java resultset
					while (rs.next())
					{  
						this.documentname = new String(rs.getString("doc_name"));
					}
					stmt.close();
				}

				con.close();
			}
			catch(Exception e){
				System.out.println(e.getMessage());
				addActionError("Database connectivity Issue!!");
				Log.document_operation(documentid, userid, DocumentOperation.SHARE, "share_populate_list", "ERROR");
				return ERROR;
			}
		}
		catch (Exception e) 
		{
			addActionError(e.getMessage());
			Log.document_operation(documentid, userid, DocumentOperation.SHARE, "share_populate_list", "ERROR");
			return ERROR;
		}
		Log.document_operation(documentid, userid, DocumentOperation.SHARE, "share_populate_list", "SUCCESS");
		return SUCCESS;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;

	}

	public List<String> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<String> privileges) {
		this.privileges = privileges;
	}

	public String getCurr_privilege() {
		return curr_privilege;
	}

	public void setCurr_privilege(String curr_privilege) {
		this.curr_privilege = curr_privilege;
	}

	public int getDocumentid() {
		return documentid;
	}

	public void setDocumentid(int documentid) {
		this.documentid = documentid;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public String getCurr_user() {
		return curr_user;
	}

	public void setCurr_user(String curr_user) {
		this.curr_user = curr_user;
	}

	public String getDocumentname() {
		return documentname;
	}

	public void setDocumentname(String documentname) {
		this.documentname = documentname;
	}

}