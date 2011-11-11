package net.wdmsfunc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;
import java.util.Map;

import net.wdms.crypt.DesEncrypter;
import net.wdmsfunc.Log.DocumentOperation;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.mysql.jdbc.PreparedStatement;
import javax.servlet.http.HttpServletRequest;

public class DocumentUpdateAction extends ActionSupport implements ServletRequestAware, SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8126838201330028159L;
	private Map session;
	private String documentname;
	private File document;
	private String EncryptionKey;
	private int documentid;
	private String docname;
	private String documentContentType;
	private String documentFileName;
	private HttpServletRequest servletRequest;
	private ArrayList<DocumentObject> documents;
	private String curr_document;
	
	private ArrayList<DocumentObject>viewdocumentlist = new ArrayList<DocumentObject>();
	private ArrayList<DocumentObject>shareddocumentlist = new ArrayList<DocumentObject>();
	public String BASEDIR = "Read/";

	public boolean check_authenctication_to_update() throws SQLException{
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

		if(prev >=2 && prev <=5)
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

					if(count == 0) //Check if shared with him with update previliges
					{
						stmt = con.createStatement();
						query = "Select count(*) from shared_documents where shared_userid="+ userid + " and doc_id=" + documentid +" and update_doc=1";
						rs = stmt.executeQuery(query);
						while(rs.next())
						{
							count = rs.getInt(1);
						}
						rs.close();
						stmt.close();
					}
					
					if(count > 0) //authorized to update
					{
						return true;
					}
					else //unauthorized to update
					{
						addActionError("Unauthorized to update the document!!");
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

					if(count == 0) //Check if shared with him with update previliges
					{
						stmt = con.createStatement();
						query = "Select count(*) from shared_documents where shared_userid="+ userid + " and doc_id=" + documentid +" and update_doc=1";
						rs = stmt.executeQuery(query);
						while(rs.next())
						{
							count = rs.getInt(1);
						}
						rs.close();
						stmt.close();
					}
					
					if(count > 0) //authorized to update
					{
						return true;
					}
					else //unauthorized to update
					{
						addActionError("Unauthorized to update the document!!");
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

					if(count == 0) //Check if shared with him with update previliges
					{
						stmt = con.createStatement();
						query = "Select count(*) from shared_documents where shared_userid="+ userid + " and doc_id=" + documentid +" and update_doc=1";
						rs = stmt.executeQuery(query);
						while(rs.next())
						{
							count = rs.getInt(1);
						}
						rs.close();
						stmt.close();
					}
					
					if(count > 0) //authorized to update
					{
						return true;
					}
					else //unauthorized to update
					{
						addActionError("Unauthorized to update the document!!");
						return false;
					}
				}
				catch (Exception e) 
				{
					addActionError("Unable to process the request currently!!");
					return false;
				}
			}
			
			if(prev == 2) //Guest User
			{   
				try
				{

					//Check if shared with him with update previliges
					stmt = con.createStatement();
					query = "Select count(*) from shared_documents where shared_userid="+ userid + " and doc_id=" + documentid +" and update_doc=1";
					rs = stmt.executeQuery(query);
					while(rs.next())
					{
						count = rs.getInt(1);
					}
					rs.close();
					stmt.close();
					
					if(count > 0) //authorized to update
					{
						return true;
					}
					else //unauthorized to update
					{
						addActionError("Unauthorized to update the document!!");
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
			addActionError("Unauthorized to update the document!!");
			return false;
		}

		return false;
	}

	
	public String populate_documents() 
	{
		int userid = 0;
		int previlige = 0;
		String result = null;
		
		try 	
		{
			if(session.containsKey("logged-in"))
			{
				userid = Integer.parseInt(session.get("userid").toString());
				previlige = Integer.parseInt(session.get("previlige").toString());
			}
			else
			{
				Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_populate_list", "INPUT");
				return "login";
			}

		} 
		catch (Exception e) 
		{
			addActionError(e.getMessage());
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
		String dpartidsstr = null;

		try
		{
			Class.forName(driverName).newInstance();
			con = DriverManager.getConnection(url+dbName, userName,passd);
			stmt = con.createStatement();

			if(previlige == 5) //Corporate Manager //Check for documents belonging to his multiple departments + get the list of docs shared with him
			{
				query = "Select * from user where userid=" + userid;
				// execute the query, and get a java resultset
				rs = stmt.executeQuery(query);
				ArrayList<Integer> cdeptids = new ArrayList<Integer>();
				// iterate through the java resultset
				while (rs.next())
				{  
					dpartidsstr = rs.getString("department_ids");
					if(!dpartidsstr.isEmpty())
					{
						String delimiter = ",";
						String[] temp;
						/* given string will be split by the argument delimiter provided. */
						temp = dpartidsstr.split(delimiter);
						/* print substrings */
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
					department_query = department_query + " or department_ids="+ id + " or department_ids like '%," + id + "' or department_ids like '"+ id + ",%' or department_ids like '%," + id + ",%'";
				}
				try
				{
					if(!department_query.isEmpty())
					{
						query = "Select * from document where doc_userid="+ userid + department_query;
					}
					else
					{
						query = "Select * from document where doc_userid="+ userid;
					}

					if(stmt != null)
					{
						rs = stmt.executeQuery(query);
						while(rs.next())
						{
							DocumentObject document = new DocumentObject();
							document.setDoc_id(rs.getInt("docid"));
							document.setDoc_name(rs.getString("doc_name"));
							document.setDoc_size(rs.getInt("doc_size"));
							document.setDoc_userid(rs.getInt("doc_userid"));
							document.setDoc_author(rs.getString("doc_author"));
							document.setDoc_create_time(rs.getTimestamp("creation_time").toString());
							document.setDoc_lastaccess_time(rs.getTimestamp("last_access_time").toString());
							document.setDoc_modify_time(rs.getTimestamp("modification_time").toString());
							document.setDoc_type(rs.getString("doc_type"));
							document.setDoc_check(rs.getBoolean("checkin_status"));
							document.setDept_ids_str(rs.getString("department_ids"));
							document.setDoc_isencrypted(rs.getBoolean("doc_isencrypted"));
							document.setDoc_checked_userid(rs.getInt("doc_checked_userid"));
							if(rs.getInt("doc_userid") == userid)
							{
								document.setCan_share(1);
							}
							else
							{
								document.setCan_share(0);
							}
							ArrayList<Integer> dept_list = new ArrayList<Integer>();
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
									dept_list.add(Integer.parseInt(temp[i]));
								}
							}
							document.setDept_ids_int(dept_list);
							viewdocumentlist.add(document);


						}	
					}

					query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid  +  "  and document.docid=share.doc_id and share.update_doc=1";
					if(stmt != null)
					{
						rs = stmt.executeQuery(query);
						while(rs.next())
						{
							DocumentObject document = new DocumentObject();
							document.setDoc_id(rs.getInt(1));
							document.setDoc_name(rs.getString(2));
							document.setDoc_size(rs.getInt(3));
							document.setDoc_userid(rs.getInt(4));
							document.setDoc_author(rs.getString(5));
							document.setDoc_create_time(rs.getTimestamp(6).toString());
							document.setDoc_lastaccess_time(rs.getTimestamp(7).toString());
							document.setDoc_modify_time(rs.getTimestamp(8).toString());
							document.setDoc_type(rs.getString(9));
							document.setDoc_check(rs.getBoolean(10));
							document.setDept_ids_str(rs.getString(11));
							document.setDoc_isencrypted(rs.getBoolean(12));
							document.setDoc_checked_userid(rs.getInt(13));
							document.setRead_doc(rs.getInt(14));
							document.setUpdate_doc(rs.getInt(15));
							document.setCheck_in(rs.getInt(16));

							shareddocumentlist.add(document);
						}	
					}
				}
				catch (Exception e) 
				{
					return ERROR;
				}

				result = "doc_view_corporate_success";
			}

			if(previlige == 4) //Department Manager //Check for documents in his department + prev_level of docs <= his + get the list of docs shared with him
			{
				query = "Select * from user where userid=" + userid;
				// execute the query, and get a java resultset
				rs = stmt.executeQuery(query);

				// iterate through the java resultset
				while (rs.next())
				{  
					dpartidsstr = rs.getString("department_ids");
				}

				try
				{
					query = "Select * from document where doc_userid="+ userid + " or ((department_ids="+ dpartidsstr + " or department_ids like '%," + dpartidsstr + "' or department_ids like '"+ dpartidsstr + ",%' or department_ids like '%," + dpartidsstr + ",%') and user_previlige_level<="+previlige+")";
					if(stmt != null)
					{
						rs = stmt.executeQuery(query);
						while(rs.next())
						{
							DocumentObject document = new DocumentObject();
							document.setDoc_id(rs.getInt("docid"));
							document.setDoc_name(rs.getString("doc_name"));
							document.setDoc_size(rs.getInt("doc_size"));
							document.setDoc_userid(rs.getInt("doc_userid"));
							document.setDoc_author(rs.getString("doc_author"));
							document.setDoc_create_time(rs.getTimestamp("creation_time").toString());
							document.setDoc_lastaccess_time(rs.getTimestamp("last_access_time").toString());
							document.setDoc_modify_time(rs.getTimestamp("modification_time").toString());
							document.setDoc_type(rs.getString("doc_type"));
							document.setDoc_check(rs.getBoolean("checkin_status"));
							document.setDept_ids_str(rs.getString("department_ids"));
							document.setDoc_isencrypted(rs.getBoolean("doc_isencrypted"));
							document.setDoc_checked_userid(rs.getInt("doc_checked_userid"));
							if(rs.getInt("doc_userid") == userid)
							{
								document.setCan_share(1);
							}
							else
							{
								document.setCan_share(0);
							}

							ArrayList<Integer> dept_list = new ArrayList<Integer>();
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
									dept_list.add(Integer.parseInt(temp[i]));
								}
							}
							document.setDept_ids_int(dept_list);
							viewdocumentlist.add(document);

						}	
					}

					query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid  +  "  and document.docid=share.doc_id and share.update_doc=1";
					if(stmt != null)
					{
						rs = stmt.executeQuery(query);
						while(rs.next())
						{
							DocumentObject document = new DocumentObject();
							document.setDoc_id(rs.getInt(1));
							document.setDoc_name(rs.getString(2));
							document.setDoc_size(rs.getInt(3));
							document.setDoc_userid(rs.getInt(4));
							document.setDoc_author(rs.getString(5));
							document.setDoc_create_time(rs.getTimestamp(6).toString());
							document.setDoc_lastaccess_time(rs.getTimestamp(7).toString());
							document.setDoc_modify_time(rs.getTimestamp(8).toString());
							document.setDoc_type(rs.getString(9));
							document.setDoc_check(rs.getBoolean(10));
							document.setDept_ids_str(rs.getString(11));
							document.setDoc_isencrypted(rs.getBoolean(12));
							document.setDoc_checked_userid(rs.getInt(13));
							document.setRead_doc(rs.getInt(14));
							document.setUpdate_doc(rs.getInt(15));
							document.setCheck_in(rs.getInt(16));

							shareddocumentlist.add(document);
						}	
					}

				}
				catch (Exception e) 
				{
					return ERROR;
				}

				result = "doc_view_department_success";
			}

			if(previlige == 3) //Regular Employee //Get list of documents belonging to him + Get list of documents shared with him
			{
				try
				{
					query = "Select * from document where doc_userid="+ userid;
					if(stmt != null)
					{
						rs = stmt.executeQuery(query);
						while(rs.next())
						{
							DocumentObject document = new DocumentObject();
							document.setDoc_id(rs.getInt("docid"));
							document.setDoc_name(rs.getString("doc_name"));
							document.setDoc_size(rs.getInt("doc_size"));
							document.setDoc_userid(rs.getInt("doc_userid"));
							document.setDoc_author(rs.getString("doc_author"));
							document.setDoc_create_time(rs.getTimestamp("creation_time").toString());
							document.setDoc_lastaccess_time(rs.getTimestamp("last_access_time").toString());
							document.setDoc_modify_time(rs.getTimestamp("modification_time").toString());
							document.setDoc_type(rs.getString("doc_type"));
							document.setDoc_check(rs.getBoolean("checkin_status"));
							document.setDept_ids_str(rs.getString("department_ids"));
							document.setDoc_isencrypted(rs.getBoolean("doc_isencrypted"));
							document.setDoc_checked_userid(rs.getInt("doc_checked_userid"));

							ArrayList<Integer> dept_list = new ArrayList<Integer>();
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
									dept_list.add(Integer.parseInt(temp[i]));
								}
							}
							document.setDept_ids_int(dept_list);
							viewdocumentlist.add(document);

						}	
					}


					query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid +  "  and document.docid=share.doc_id and share.update_doc=1;";
					if(stmt != null)
					{
						rs = stmt.executeQuery(query);
						while(rs.next())
						{
							DocumentObject document = new DocumentObject();
							document.setDoc_id(rs.getInt(1));
							document.setDoc_name(rs.getString(2));
							document.setDoc_size(rs.getInt(3));
							document.setDoc_userid(rs.getInt(4));
							document.setDoc_author(rs.getString(5));
							document.setDoc_create_time(rs.getTimestamp(6).toString());
							document.setDoc_lastaccess_time(rs.getTimestamp(7).toString());
							document.setDoc_modify_time(rs.getTimestamp(8).toString());
							document.setDoc_type(rs.getString(9));
							document.setDoc_check(rs.getBoolean(10));
							document.setDept_ids_str(rs.getString(11));
							document.setDoc_isencrypted(rs.getBoolean(12));
							document.setDoc_checked_userid(rs.getInt(13));
							document.setRead_doc(rs.getInt(14));
							document.setUpdate_doc(rs.getInt(15));
							document.setCheck_in(rs.getInt(16));

							shareddocumentlist.add(document);
						}	
					}
				}
				catch (Exception e) 
				{
					return ERROR;
				}

				result = "doc_view_regular_success";
			}

			if(previlige == 2) //Guest User //Get list of documents shared with him
			{
				try
				{				
					query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid  +  "  and document.docid=share.doc_id  and share.update_doc=1";
					if(stmt != null)
					{
						rs = stmt.executeQuery(query);
						while(rs.next())
						{
							DocumentObject document = new DocumentObject();
							document.setDoc_id(rs.getInt(1));
							document.setDoc_name(rs.getString(2));
							document.setDoc_size(rs.getInt(3));
							document.setDoc_userid(rs.getInt(4));
							document.setDoc_author(rs.getString(5));
							document.setDoc_create_time(rs.getTimestamp(6).toString());
							document.setDoc_lastaccess_time(rs.getTimestamp(7).toString());
							document.setDoc_modify_time(rs.getTimestamp(8).toString());
							document.setDoc_type(rs.getString(9));
							document.setDoc_check(rs.getBoolean(10));
							document.setDept_ids_str(rs.getString(11));
							document.setDoc_isencrypted(rs.getBoolean(12));
							document.setDoc_checked_userid(rs.getInt(13));
							document.setRead_doc(rs.getInt(14));
							document.setUpdate_doc(rs.getInt(15));
							document.setCheck_in(rs.getInt(16));

							shareddocumentlist.add(document);
						}	
					}

				}
				catch (Exception e) 
				{
					return ERROR;
				}

				result = "doc_view_guest_success";
			}
		}
		catch (Exception e) 
		{
			addActionError(e.getMessage());
			Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_populate_list", "FAIL");
			return ERROR;
		}

		setDocuments(new ArrayList<DocumentObject>());
		ListIterator<DocumentObject> itr = viewdocumentlist.listIterator();
		while (itr.hasNext()) {
			DocumentObject obj = itr.next();
			if(obj.isDoc_check() == false || (obj.getDoc_checked_userid() == userid))
				documents.add(obj);
		}

		itr = shareddocumentlist.listIterator();
		while (itr.hasNext()) {
			DocumentObject obj = itr.next();
			if(((obj.isDoc_check()== false) || (obj.getDoc_checked_userid() == userid)) && obj.getUpdate_doc() == 1)
				documents.add(obj);
		}

		Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_populate_list", "SUCCESS");
		return result;
	}

	public String UpdateMetaDataDocument() throws Exception{
		int userid = Integer.parseInt(session.get("userid").toString());
		int previlige = Integer.parseInt(session.get("previlige").toString());

		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "wdms";
		String driverName = "org.gjt.mm.mysql.Driver";
		String userName = "root";
		String passd = "aj";
		Connection con=null;
		Statement stmt=null;
		String query = null;

		try
		{
			int mid= docname.lastIndexOf(".");
			String docname_prev_ext=docname.substring(mid+1,docname.length());
			int last = documentname.lastIndexOf(".");
			if(last == -1)
			{
				addActionMessage("Extension "+ docname_prev_ext + " added by default!!");
				documentname = documentname + "." + docname_prev_ext;
			}
			else
			{
				String docname_new_ext = documentname.substring(last+1,documentname.length());
				if(!(docname_new_ext != null && docname_prev_ext != null && docname_new_ext.equalsIgnoreCase(docname_prev_ext)))
				{
					addActionMessage("Unkown extension!!");
					return "error";
				}
			}
			
			Class.forName(driverName).newInstance();
			con = DriverManager.getConnection(url+dbName, userName,passd);
			stmt = con.createStatement();
			
			if(check_authenctication_to_update())
			{
				Date today = new Date();
				java.sql.Timestamp Timestamp = new java.sql.Timestamp(today.getTime());
				query = "Update document set doc_name ='"+ documentname +"',last_access_time= '"+ Timestamp +"'"+",modification_time='"+Timestamp +"'" + " where (docid=" + documentid + " and checkin_status=0) or (docid=" + documentid + " and doc_checked_userid=" + userid + " and checkin_status=1)";
				if(stmt != null)
				{
					int val = stmt.executeUpdate(query);
					if(val>0)
					{
						addActionMessage("Document metadata updated successfully!!");
						Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_metadata_operation", "SUCCESS");
						return "success";
					}
					else
					{
						addActionMessage("Document has been checked out by someone or file deleted!!");
						Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_metadata_operation", "FAIL");
						return "error";
					}
				}
				else
				{
					addActionMessage("Unable to process the request currently!!");
					return "error";
				}
			}
			else
			{
				addActionMessage("Unauthorized to update the document.. please check previliges");
				Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_metadata_operation", "FAIL");
				return "error";
			}
		}
		catch (Exception e) 
		{
			addActionMessage("Unable to process the request currently!!");
			Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_metadata_operation", "FAIL");
			return "error";
		}
	}

	private boolean Upload_document(boolean encrypt) throws SQLException, FileNotFoundException 
	{
		boolean result_value = false;
		String dbName = "wdms";
		String userName = "root";
		String passd = "aj";
		String url = "jdbc:mysql://localhost:3306/";
		PreparedStatement ps = null;
		String query = null;
		int userid = Integer.parseInt(session.get("userid").toString());
		File file = null;
		
		try 
		{
			if(documentid > 0)
			{
				/** Establish connection to database**/
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = (Connection) DriverManager.getConnection(url+dbName, userName,passd);

				Date today = new Date();
				java.sql.Timestamp Timestamp = new java.sql.Timestamp(today.getTime());
				/** Save the file in database **/
				if(encrypt == true)
				{
					file = new File(BASEDIR + "ciphertext_"+ this.documentFileName);
				}
				else
				{
					file = this.document;
				}
				
				query = "Update document set doc_name=?,doc_content=?,doc_size=?,doc_type=?,last_access_time=?,modification_time=? where (docid=" + documentid + " and checkin_status=0) or (docid=" + documentid + " and doc_checked_userid=" + userid + " and checkin_status=1)";
				ps = (PreparedStatement) con.prepareStatement(query);
				ps.setString(1, documentFileName);
				ps.setBinaryStream(2, new FileInputStream(file), (int)file.length());
				ps.setInt(3,(int)file.length());
				ps.setString(4, documentContentType);
				ps.setTimestamp(5,Timestamp);
				ps.setTimestamp(6,Timestamp);
				ps.executeUpdate();
				con.close();
				result_value = true;
			}
			else
			{
				result_value = false;
			}
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			result_value = false;
		}
		return result_value;
	}

	public String update_document_without_encryption() 
	{
		try 
		{
			if(!session.containsKey("logged-in"))
			{
				return "login";
			}

			if((this.document == null))
			{
				addActionMessage("Please provide file path");
				return "input";
			}
			
			int userid = Integer.parseInt(session.get("userid").toString());
			
			if(check_authenctication_to_update())
			{
				if(Upload_document(false))
				{
					addActionMessage("File Updated Successfully!!");
					Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_encrypted_operation", "SUCCESS");
					return "SUCCESS";
				}
				else
				{
					addActionMessage("File must have been checkedout by others..Failed to update the file!!");
					Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_encrypted_operation", "FAIL");
					return "ERROR";
				}
			}
			else
			{
				addActionError("Unauthorized User access please log-in to continue");
				Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_encrypted_operation", "FAIL");
				return "login";
			}
		} 
		catch (Exception e) 
		{
			int userid = Integer.parseInt(session.get("userid").toString());
			addActionError("Unable to process the request currently!!");
			Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_encrypted_operation", "FAIL");
			return "ERROR";
		}
	}

	public String update_document_with_encryption() {

		try 
		{
			if(!session.containsKey("logged-in"))
			{
				addActionMessage("Session Timed out");
				return "login";
			}

			
			if((EncryptionKey == null) || EncryptionKey.isEmpty())
			{
				addActionMessage("Please provide key phrase");
				return "input";
			}
			
			int userid = Integer.parseInt(session.get("userid").toString());
			
			if((this.document == null))
			{
				addActionMessage("Please provide file path");
				return "input";
			}
			
			if(check_authenctication_to_update())
			{
				//Create Encryption decryption object
				DesEncrypter encrypter = new DesEncrypter(EncryptionKey);
				// Encrypt the file
				encrypter.encrypt(new FileInputStream(this.document),new FileOutputStream( BASEDIR + "ciphertext_" + this.documentFileName));
				
				if(Upload_document(true))
				{
					addActionMessage("File Updated Successfully!!");
					//Delete the encrypted file
					File delete_file = new File( BASEDIR + "ciphertext_" + this.documentFileName);
					if(delete_file.isFile())
					{
						delete_file.delete();
					}
					Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_operation", "SUCCESS");
					return "SUCCESS";
				}
				else
				{
					addActionMessage("File must have been checkedout by others..Failed to update the file!!");
					//Delete the encrypted file
					File delete_file = new File( BASEDIR + "ciphertext_" + this.documentFileName);
					if(delete_file.isFile())
					{
						delete_file.delete();
					}
					Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_operation", "FAIL");
					return "ERROR";
				}
			}
			else
			{
				addActionError("Unauthorized User access please log-in to continue");
				Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_operation", "FAIL");
				return "login";
			}
		} 
		catch (Exception e) 
		{
			int userid = Integer.parseInt(session.get("userid").toString());
			addActionError("Unable to process the request currently!!");
			Log.document_operation(documentid, userid, DocumentOperation.UPDATE, "update_operation", "SUCCESS");
			return "ERROR";
		}
	}

	public String getDocumentFileName() {
		return documentFileName;
	}

	public void setdocumentFileName(String documentFileName) {
		this.documentFileName = documentFileName;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}



	public void setViewdocumentlist(ArrayList<DocumentObject> viewdocumentlist) {
		this.viewdocumentlist = viewdocumentlist;
	}


	public ArrayList<DocumentObject> getViewdocumentlist() {
		return viewdocumentlist;
	}

	public String getDocumentname() {
		return documentname;
	}

	public void setDocumentname(String documentname) {
		this.documentname = documentname;
	}

	public int getDocumentid() {
		return documentid;
	}

	public void setDocumentid(int documentid) {
		this.documentid = documentid;
	}

	public File getDocument() {
		return document;
	}

	public void setDocument(File document) {
		this.document = document;
	}


	public String getDocumentContentType() {
		return documentContentType;
	}

	public void setDocumentContentType(String documentContentType) {
		this.documentContentType = documentContentType;
	}

	public String getEncryptionKey() {
		return EncryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		EncryptionKey = encryptionKey;
	}
	
	@Override
	public void setSession(Map session) {
		this.session = session;

	}

	public String getDocname() {
		return docname;
	}

	public void setDocname(String docname) {
		this.docname = docname;
	}

	public void setCurr_document(String curr_document) {
		this.curr_document = curr_document;
	}

	public String getCurr_document() {
		return curr_document;
	}


	public void setDocuments(ArrayList<DocumentObject> documents) {
		this.documents = documents;
	}


	public ArrayList<DocumentObject> getDocuments() {
		return documents;
	}

}


