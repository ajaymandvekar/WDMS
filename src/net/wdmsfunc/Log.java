package net.wdmsfunc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

public class Log {
	
	public static enum DocumentOperation
	{
		 UPLOAD,READ,DELETE,UPDATE, CHECK_IN, CHECK_OUT,SHARE
	}
	
	public static void document_operation(int docId, int currentUserId, DocumentOperation opType, String operation_description, String operation_result) 
	{
	      // find out document name and author from id
	      // user_id is the id of the current user accessing the document
	      // fills data into Log table
		
		  String url = "jdbc:mysql://localhost:3306/";
		  String dbName = "wdms";
		  String driverName = "org.gjt.mm.mysql.Driver";
		  String userName = "root";
		  String password = "aj";
		  Connection con=null;
		  Statement stmt=null;
		  
		  try{
			  Class.forName(driverName).newInstance();
			  con=DriverManager.getConnection(url+dbName, userName,password);
			  stmt=con.createStatement();
		  
		  
			  //get username from user id
			  stmt = con.createStatement();
			  String firstname=null,lastname=null,name=null;
			  if(stmt !=null)
			  {
				  String query = "Select firstname,lastname from user where userid='"+ currentUserId + "'";
				  // execute the query, and get a java resultset
			      ResultSet rs = stmt.executeQuery(query);
		      
			      // iterate through the java resultset
			      while (rs.next())
			      {
			        firstname = rs.getString("firstname");
			        lastname = rs.getString("lastname");
			        name = firstname + " " + lastname;
			      }
			
			      stmt.close();
			
			  }
			
		  
			  //get document name,author from document table
			  stmt = con.createStatement();
			  String docname=null,author=null;
			  if(stmt !=null)
			  {
				  String query = "Select doc_name,doc_author from document where docid ='"+ docId + "'";
				  // execute the query, and get a java resultset
			      ResultSet rs = stmt.executeQuery(query);
			      
			      // iterate through the java resultset
			      while (rs.next())
			      {
			        docname = rs.getString(1);
			        author = rs.getString(2);
			      }
			
			      stmt.close();
			
			  }
			
			  		  
			  // insert into log table all the values
			  String op = opType.toString();
			  stmt = con.createStatement();
			  if(stmt !=null)
			  {
				  stmt.executeUpdate("INSERT into systemlog(logid,document_name,document_author,last_access_by,operation,op_description,operation_result) VALUES (0,'"+docname+"','"+author+"','"+name+"','"+op+"','"+operation_description+"','"+operation_result+"')");
				  stmt.close();
			  }
			  con.close();
		  }
		  catch(Exception e){
			  // TODO: Handle Exception
		  }
		
	}
	
	public static List<SystemLogEntry> getSystemLogEntries() 
	{
		  String url = "jdbc:mysql://localhost:3306/";
		  String dbName = "wdms";
		  String driverName = "org.gjt.mm.mysql.Driver";
		  String userName = "root";
		  String password = "aj";
		  Connection con=null;
		  Statement stmt=null;
		  
		  try{
			  Class.forName(driverName).newInstance();
			  con=DriverManager.getConnection(url+dbName, userName,password);
			  
			  stmt = con.createStatement();
			  
			  String logid = null,doc_name = null,doc_author = null,last_access_person = null,op_type = null,op_desc = null,op_res = null,timestamp = null;
			  ArrayList<SystemLogEntry> log_entry = new ArrayList<SystemLogEntry>();
			  if(stmt != null)
			  {
				  String query = "Select * from systemlog";
				  // execute the query, and get a java resultset
			      ResultSet rs = stmt.executeQuery(query);
			      
			      // iterate through the java resultset
			      while (rs.next())
			      {
			        logid = Integer.toString(rs.getInt("logid"));
			        doc_name = rs.getString("document_name");
			        doc_author = rs.getString("document_author");
				    last_access_person = rs.getString("last_access_by");
				    timestamp = (rs.getTimestamp("access_date_time")).toString();
				    op_type = rs.getString("operation");
				    op_desc = rs.getString("op_description");
				    op_res  = rs.getString("operation_result");
				    log_entry.add(new SystemLogEntry(logid, doc_name, doc_author, last_access_person, timestamp, op_type, op_desc, op_res));  
				  }
			    
			      stmt.close();
			
			  }
			  return log_entry;
		  }
		  catch(Exception e){
			  //TODO: Handle Exception
		}
		return null;
	}
	
	
	

}