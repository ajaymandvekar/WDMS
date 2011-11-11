package net.wdmsfunc;

import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.dispatcher.SessionMap;
import java.util.*;

public class LogoutAction extends ActionSupport implements SessionAware{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map session;
	
	public String execute() throws Exception {
		
		this.session = ActionContext.getContext().getSession();
		session.remove("logged-in");
		session.remove("userid");
		session.remove("name");
		session.remove("previlige");
		((SessionMap)this.session).invalidate();
		return "logout"; 
     }

	@Override
	public void setSession(Map session) {
		this.session = session;
		
	}
 }