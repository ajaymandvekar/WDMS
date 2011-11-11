package net.wdmsfunc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.wdmsfunc.Log.DocumentOperation;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

public class DocumentCheckoutAction extends ActionSupport implements ServletRequestAware, SessionAware {
	/**
	 * Added Default Serial Version
	 */
	private static final long serialVersionUID = 1L;
	private HttpServletRequest servletRequest;
	private Map session;
	private ArrayList<DocumentObject> documents;
	private String curr_document;
	private int documentid;
	private ArrayList<DocumentObject>viewdocumentlist = new ArrayList<DocumentObject>();
	private ArrayList<DocumentObject>shareddocumentlist = new ArrayList<DocumentObject>();



	public String populate_documents() {
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
				return "login";
			}
		
		} 
		catch (Exception e) 
		{
			Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, e.toString(), "FAIL");
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
					
					query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid  +  "  and document.docid=share.doc_id and share.check_in=1 and document.checkin_status=0";
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
					Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, e.toString(), "FAIL");
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
					
					query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid  +  "  and document.docid=share.doc_id and share.check_in=1 and document.checkin_status=0";
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
					Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, e.toString(), "FAIL");
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
					
					
					query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid +  "  and document.docid=share.doc_id and share.check_in=1 and document.checkin_status=0";
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
					Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, e.toString(), "FAIL");
					return ERROR;
				}
				
				result = "doc_view_regular_success";
			}
		
			if(previlige == 2) //Guest User //Get list of documents shared with him
			{
				try
				{				
					query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid  +  "  and document.docid=share.doc_id and share.Check_in=1 and document.checkin_status=0";
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
					Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, e.toString(), "FAIL");
					return ERROR;
				}
				
				result = "doc_view_guest_success";
			}
		}
		catch (Exception e) {
			System.out.println(e.toString());
			Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, e.toString(), "SUCESS");
			return ERROR;
		}

		
		documents = new ArrayList<DocumentObject>();
		ListIterator<DocumentObject> itr = viewdocumentlist.listIterator();
		    while (itr.hasNext()) {
		    	DocumentObject obj = itr.next();
		    	if(obj.isDoc_check() == false)
		    		documents.add(obj);
		    }
	
		 itr = shareddocumentlist.listIterator();
		    while (itr.hasNext()) {
		    	DocumentObject obj = itr.next();
		    	if(obj.isDoc_check()== false && obj.getCheck_in() == 1)
		    		documents.add(obj);
		    }
	   
		Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, "Checkout_populate_list", "SUCESS");
		return result;
	}
	
	public String checkout_document()
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

			//TODO check authentication of documentid is checkoutable by user
			String url = "jdbc:mysql://localhost:3306/";
			String dbName = "wdms";
			String driverName = "org.gjt.mm.mysql.Driver";
			String userName = "root";
			String passd = "aj";
			Connection con=null;
			Statement stmt=null;
			String query = null;
			int value = 0;
			try
			{
				Class.forName(driverName).newInstance();
				con = DriverManager.getConnection(url+dbName, userName,passd);
				stmt = con.createStatement();
				if(stmt != null)
				{
					query = "update document set checkin_status = 1, doc_checked_userid = "+userid+" where docid="+documentid+"";
					value = stmt.executeUpdate(query);
					if(value > 0)
					{
						Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, "Checkout_operation", "SUCCESS");
					}
					else
					{
						Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, "Checkout_operation", "FAIL");
					}
				}
				else
				{
					Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, "checkout_database", "FAIL");
					return ERROR;
				}
			
			
			}
			catch (Exception e) {
				System.out.println(e.toString());
				Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, e.toString(), "FAIL");
				return ERROR;
			}
					
		}
		catch (Exception e) 
		{
			Log.document_operation(documentid, userid, DocumentOperation.CHECK_OUT, e.toString(), "FAIL");
			addActionError(e.getMessage());
			return ERROR;
		}
		
		
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

	public ArrayList<DocumentObject> getDocuments() {
		return documents;
	}

	public void setDocuments(ArrayList<DocumentObject> documents) {
		this.documents = documents;
	}

	public String getCurr_document() {
		return curr_document;
	}

	public void setCurr_document(String curr_document) {
		this.curr_document = curr_document;
	}

	public void setViewdocumentlist(ArrayList<DocumentObject> viewdocumentlist) {
		this.viewdocumentlist = viewdocumentlist;
	}


	public ArrayList<DocumentObject> getViewdocumentlist() {
		return viewdocumentlist;
	}


	public void setShareddocumentlist(ArrayList<DocumentObject> shareddocumentlist) {
		this.shareddocumentlist = shareddocumentlist;
	}


	public ArrayList<DocumentObject> getShareddocumentlist() {
		return shareddocumentlist;
	}

	public int getDocumentid() {
		return documentid;
	}

	public void setDocumentid(int documentid) {
		this.documentid = documentid;
	}


}