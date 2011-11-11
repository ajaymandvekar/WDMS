package net.wdmsfunc;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;

public class AdminHomePageAction extends ActionSupport implements SessionAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7250466858560321049L;
	private Map session;
	
	public String execute() throws Exception {	
		try{
			String logged_in = session.get("logged-in").toString();
			if(logged_in.equals("true"))
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

	@Override
	public void setSession(Map session) {
		this.session = session;
	}
}
