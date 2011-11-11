package net.wdmsfunc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.wdmsfunc.Log.DocumentOperation;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

public class DocumentCheckinAction extends ActionSupport implements ServletRequestAware, SessionAware {
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
			Log.document_operation(documentid, userid, DocumentOperation.CHECK_IN, "Checkin_populate_list", "FAIL");
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
			
			if(stmt!=null)
			{
				query = "select * from document where doc_checked_userid='"+userid+"' and checkin_status = 1";
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
					
					viewdocumentlist.add(document);
				}
			}
		}
		catch (Exception e) {
			Log.document_operation(documentid, userid, DocumentOperation.CHECK_IN, "Checkin_populate_list", "FAIL");
			return ERROR;
		}

		
		documents = new ArrayList<DocumentObject>();
		ListIterator<DocumentObject> itr = viewdocumentlist.listIterator();
		    while (itr.hasNext()) {
		    	DocumentObject obj = itr.next();
	    		documents.add(obj);
		    }
		    
		Log.document_operation(documentid, userid, DocumentOperation.CHECK_IN, "Checkin_populate_list", "SUCESS");
		return SUCCESS;
	}
	
	public String checkin_document()
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
					query = "update document set checkin_status = 0,doc_checked_userid=0 where docid='"+documentid+"'";
					value = stmt.executeUpdate(query);
					if(value>0)
					{
						Log.document_operation(documentid, userid, DocumentOperation.CHECK_IN, "Checkin_operation", "SUCESS");
					}
					else
					{
						Log.document_operation(documentid, userid, DocumentOperation.CHECK_IN, "Checkin_operation", "FAIL");
					}
				}
				else
				{
					Log.document_operation(documentid, userid, DocumentOperation.CHECK_IN, "database_connection", "FAIL");
					return ERROR;
				}
			
			
			}
			catch (Exception e) {
				Log.document_operation(documentid, userid, DocumentOperation.CHECK_IN, e.toString(), "FAIL");
				return ERROR;
			}
					
		}
		catch (Exception e) 
		{
			Log.document_operation(documentid, userid, DocumentOperation.CHECK_IN, e.toString(), "FAIL");
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


	public int getDocumentid() {
		return documentid;
	}

	public void setDocumentid(int documentid) {
		this.documentid = documentid;
	}


}