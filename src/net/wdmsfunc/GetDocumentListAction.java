package net.wdmsfunc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;

public class GetDocumentListAction  extends ActionSupport implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2020792494443675744L;
	private Map session;
	private ArrayList<DocumentObject>viewdocumentlist = new ArrayList<DocumentObject>();
	private ArrayList<DocumentObject>shareddocumentlist = new ArrayList<DocumentObject>();
	
	public String execute() throws Exception {

		if(!session.containsKey("userid"))
		{
			return ERROR;
		}
		
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
		ResultSet rs = null;
		String dpartidsstr = null;
		
		try
		{
			Class.forName(driverName).newInstance();
			con = DriverManager.getConnection(url+dbName, userName,passd);
			stmt = con.createStatement();
		}
		catch (Exception e) {
			System.out.println(e.toString());
			return ERROR;
		}

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
				
				query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid  +  "  and document.docid=share.doc_id";
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
			
			return "doc_view_corporate_success";
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
				
				query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid  +  "  and document.docid=share.doc_id";
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
			
			return "doc_view_department_success";
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
				
				
				query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid +  "  and document.docid=share.doc_id;";
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
			
			return "doc_view_regular_success";
		}
	
		if(previlige == 2) //Guest User //Get list of documents shared with him
		{
			try
			{				
				query = "Select document.docid,document.doc_name,document.doc_size,document.doc_userid,document.doc_author,document.creation_time,document.last_access_time,document.modification_time,document.doc_type,document.checkin_status,document.department_ids,document.doc_isencrypted,document.doc_checked_userid,share.read_doc,share.update_doc,share.check_in from document,shared_documents as share where share.shared_userid="+ userid  +  "  and document.docid=share.doc_id and share.read_doc=1";
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
			
			return "doc_view_guest_success";
		}
		return ERROR;
	}


	@Override
	public void setSession(Map session) {
		this.session = session;
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

}
