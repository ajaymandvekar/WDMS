package net.wdmsfunc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.bind.ParseConversionEvent;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class DocumentReadCheck extends AbstractInterceptor {

	private static final String USER_KEY = "logged-in";
	private static final String USER_PREVILIGE = "previlige";

	public String intercept(ActionInvocation invocation) throws Exception {
		Map session = invocation.getInvocationContext().getSession();
		if(session.get(USER_KEY) == null) {
			addActionError(invocation, "You must be authenticated to access this page");
			return "login";
		}
		else 
		{
			//check if user has read privileges
			//check if document is present in document table and user is the owner, then he has read privilege
			//else check if document is present in shared document table and user has read privilege for it 
			//else return error
			
			String url = "jdbc:mysql://localhost:3306/";
			String dbName = "wdms";
			String driverName = "org.gjt.mm.mysql.Driver";
			String userName = "root";
			String passd = "aj";
			Connection con=null;
			Statement stmt=null;
			String query = null;
			ResultSet rs = null;
			String department_query = "";
			
			try
			{  
				Class.forName(driverName).newInstance();
				con = DriverManager.getConnection(url+dbName, userName,passd);
				stmt = con.createStatement();

				if(stmt !=null)
				{	
				    Map parameters = invocation.getInvocationContext().getParameters();
			        if(parameters.containsKey("documentid"))
			        {
				        String[] v =  (String[]) parameters.get("documentid");
				        int documentid = Integer.parseInt(v[0].toString());
				    	int userid = Integer.parseInt(session.get("userid").toString());
						String departmentids = "";
						
				    	//Check if account is disabled by admin
				    	query = "Select * from user where userid="+userid;
			    		rs = stmt.executeQuery(query);
			    		// iterate through the java resultset
						rs.last();
						if(rs.getRow() > 0)
						{
							departmentids = rs.getString("department_ids");
						}
						else
						{
							stmt.close();
							addActionError(invocation, "User Account disabled by administrator");
							return "login";
						}
						
						stmt.close();
						
				    	if((Integer.parseInt(session.get(USER_PREVILIGE).toString()) >= 4) && (Integer.parseInt(session.get(USER_PREVILIGE).toString()) <= 5))
				    	{
				    			ArrayList<Integer> deptids = new ArrayList<Integer>();
				    			if(!departmentids.isEmpty())
								{
									String delimiter = ",";
									String[] temp;
									/* given string will be split by the argument delimiter provided. */
									temp = departmentids.split(delimiter);
									/* print substrings */
									for(int i =0; i < temp.length ; i++)
									{
										deptids.add(Integer.parseInt(temp[i]));
									}
									
									ListIterator<Integer> litr = deptids.listIterator();
									String id = "";
								    while (litr.hasNext()) {
								      id = litr.next().toString();
								      
								      department_query = department_query + " department_ids="+ id + " or department_ids like '%," + id + "' or department_ids like '"+ id + ",%' or department_ids like '%," + id + ",%'";
								      if(litr.hasNext())
								      {
								    	  department_query = department_query + " or";
								      }
								    }
								}
				    			
				    			if(deptids.size()>0)
				    			{
				    				stmt = con.createStatement();
				    				query = "Select count(*) from document where "+ department_query;
				    				rs = stmt.executeQuery(query);
				    				int count = 0;
				    				while(rs.next())
									{
				    					count = rs.getInt(1);
									}
				    				stmt.close();
				    				if(count == 0) //Not in his department
				    				{
				    					//user is not the owner of the specified document
										//check if document is shared with him with read privilege
										Statement stmt1 = con.createStatement();
										query = "Select * from shared_documents where shared_userid ="+userid+" and doc_id = "+documentid+" and read_doc = 1";
										rs = stmt1.executeQuery(query);
				
										// iterate through the java resultset
										rs.last();
										if(rs.getRow() == 0)
										{
											//user does not have read privilege
											addActionError(invocation, "You do not have read privilege for this document");
											return Action.ERROR;	
										}
										stmt1.close();
				    				}
				    			}
				    			else
				    			{
				    				stmt.close();
				    				//user does not have read privilege
									addActionError(invocation, "You do not have read privilege for this document");
									return Action.ERROR;
				    			}
				    		
				    	}
				    	else
				    	{
				    		stmt = con.createStatement();
							query = "Select * from document where docid="+documentid+" and doc_userid="+userid;
							// execute the query, and get a java resultset
							rs = stmt.executeQuery(query);
		
							// iterate through the java resultset
							rs.last();
							if(rs.getRow() == 0)
							{
								//user is not the owner of the specified document
								//check if document is shared with him with read privilege
								Statement stmt1 = con.createStatement();
								query = "Select * from shared_documents where shared_userid ="+userid+" and doc_id = "+documentid+" and read_doc = 1";
								rs = stmt1.executeQuery(query);
		
								// iterate through the java resultset
								rs.last();
								if(rs.getRow() == 0)
								{
									
									stmt1.close();
									stmt.close();
									//user does not have read privilege
									addActionError(invocation, "You do not have read privilege for this document");
									return Action.ERROR;	
								}
								stmt1.close();
							}
							stmt.close();
						}
					}
			        else
			        {
			        	//user does not have read privilege
						addActionError(invocation, "You do not have read privilege for this document");
						return Action.ERROR;
			        }
				}
			}
			catch (Exception e) 
			{
				return Action.ERROR;
			}	

		}
		return invocation.invoke();
	}

	private void addActionError(ActionInvocation invocation, String message) {
		Object action = invocation.getAction();
		if(action instanceof ValidationAware) {
			((ValidationAware) action).addActionError(message);
		}
	}
}