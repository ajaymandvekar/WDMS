/**
 * 
 */
package net.wdmsfunc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

import net.wdmsfunc.Log.DocumentOperation;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;


public class DocumentDeleteAction extends ActionSupport implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2950809803321188228L;
	private int documentid;
	private Map session;


	@Override
	public void setSession(Map session) {
		this.session = session;
	}

	public boolean check_authenctication_to_delete() throws SQLException{
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String password = "aj";
		Connection con=null;
		Statement stmt=null;
		String query = null;
		String dpartidsstr = null;
		ResultSet rs = null;
		int count = 0;


		
		int prev = Integer.parseInt(session.get("previlige").toString());
		int userid = Integer.parseInt(session.get("userid").toString());

		try
		{
			Class.forName(driverName).newInstance();
			con = DriverManager.getConnection(url+dbName, userName,password);
			stmt = con.createStatement();
		}
		catch (Exception e) {
			addActionError("Unable to process the request currently!!");
			return false;
		}

		if(prev >=3 && prev <=5)
		{
			if(prev == 5) //Corporate Manager
			{    
				try
				{
					query = "Select * from user where userid=" + userid;
					rs = stmt.executeQuery(query);
					ArrayList<Integer> cdeptids = new ArrayList<Integer>();
					while (rs.next())
					{  
						dpartidsstr = rs.getString("department_ids");
						if(!dpartidsstr.isEmpty())
						{
							String delimiter = ",";
							String[] temp;
							temp = dpartidsstr.split(delimiter);
							for(int i =0; i < temp.length ; i++)
							{
								cdeptids.add(Integer.parseInt(temp[i]));
							}
						}
					}

					String department_query = "";
					ListIterator<Integer> litr = cdeptids.listIterator();
					String id = "";
					while (litr.hasNext()) {
						id = litr.next().toString();
						department_query = department_query + " department_ids="+ id + " or department_ids like '%," + id + "' or department_ids like '"+ id + ",%' or department_ids like '%," + id + ",%'";
						if(litr.hasNext())
						{
							department_query = department_query + " or ";
						}
					}


					if(!department_query.isEmpty())
					{
						query = "Select count(*) from document where docid=" + documentid + " and  (doc_userid="+ userid + "  or (" + department_query + "))";
					}
					else
					{
						query = "Select count(*) from document where doc_userid="+ userid + " and docid=" + documentid;
					}

					rs = stmt.executeQuery(query);
					while(rs.next())
					{
						count = rs.getInt(1);
					}
					rs.close();
					stmt.close();

					if(count > 0) //authorized to delete
					{
						return true;
					}
					else //unauthorized to delete
					{
						addActionError("Unauthorized to delete the document!!");
						return false;
					}
				}
				catch (Exception e) 
				{
					addActionError("Unable to process the request currently!!");
					return false;
				}
			}


			if(prev == 4) //Department Manager
			{
				try
				{
					query = "Select * from user where userid=" + userid;
					// execute the query, and get a java resultset
					rs = stmt.executeQuery(query);

					// iterate through the java resultset
					while (rs.next())
					{  
						dpartidsstr = rs.getString("department_ids");
					}

					if(!dpartidsstr.isEmpty())
					{
						query = "Select count(*) from document where docid=" + documentid + " and (doc_userid="+ userid + " or ((department_ids="+ dpartidsstr + " or department_ids like '%," + dpartidsstr + "' or department_ids like '"+ dpartidsstr + ",%' or department_ids like '%," + dpartidsstr + ",%') and user_previlige_level<="+prev+"))";
					}
					else
					{
						query = "Select count(*) from document where doc_userid="+ userid + " and docid=" + documentid;
					}

					rs = stmt.executeQuery(query);
					while(rs.next())
					{
						count = rs.getInt(1);
					}
					rs.close();
					stmt.close();

					if(count > 0) //authorized to delete
					{
						return true;
					}
					else //unauthorized to delete
					{
						addActionError("Unauthorized to delete the document!!");
						return false;
					}

				}
				catch (Exception e) 
				{
					addActionError("Unable to process the request currently!!");
					return false;
				}
			}

			if(prev == 3) //Regular Employee
			{   
				try
				{
					query = "Select count(*) from document where doc_userid="+ userid + " and docid=" + documentid;
					rs = stmt.executeQuery(query);
					while(rs.next())
					{
						count = rs.getInt(1);
					}
					rs.close();
					stmt.close();

					if(count > 0) //authorized to delete
					{
						return true;
					}
					else //unauthorized to delete
					{
						addActionError("Unauthorized to delete the document!!");
						return false;
					}
				}
				catch (Exception e) 
				{
					addActionError("Unable to process the request currently!!");
					return false;
				}
			}
		}
		else
		{
			addActionError("Unauthorized to delete the document!!");
			return false;
		}

		return false;
	}
	
	public String execute() throws Exception {
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String password = "aj";
		Connection con=null;
		Statement stmt=null;
		int userid = Integer.parseInt(session.get("userid").toString());
		try{

			if(!session.containsKey("logged-in"))
			{
				return "login";
			}

			if(check_authenctication_to_delete() == false)
			{
				Log.document_operation(documentid, userid, DocumentOperation.DELETE, "delete_auth_error", "FAIL");
				return "error";
			}

			Class.forName(driverName).newInstance();
			con = DriverManager.getConnection(url+dbName, userName,password);
			stmt = con.createStatement();
			if(stmt !=null)
			{
				
				String query = "Delete from document where (docid="+documentid+" and checkin_status=0) or (docid=" + documentid + " and doc_checked_userid=" + userid + " and checkin_status=1)" ;
				// execute the query, and get a boolean value
				int val = stmt.executeUpdate(query);

				if(val>0)
				{
					String deletequery = "Delete from shared_documents where doc_id="+documentid;
					stmt.executeUpdate(deletequery);
					addActionMessage("Document deleted successfully!!");
					Log.document_operation(documentid, userid, DocumentOperation.DELETE, "delete_operation", "SUCCESS");
					return "success";
				}
				else
				{
					addActionMessage("Document has been checkout by other user.. Please be patient till it is checkin again");
					Log.document_operation(documentid, userid, DocumentOperation.DELETE, "delete_operation", "FAIL");
					return "error";
				}
			}

			addActionMessage("Unable to process the request currently!!");
			Log.document_operation(documentid, userid, DocumentOperation.DELETE, "delete_operation", "FAIL");
			return "error";
		}
		catch(Exception e)
		{
			Log.document_operation(documentid, userid, DocumentOperation.DELETE, "delete_operation_database", "FAIL");
			return "error";
		}

	}

	public int getDocumentid() {
		return documentid;
	}

	public void setDocumentid(int documentid) {
		this.documentid = documentid;
	}
}
