package net.wdmsfunc;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;
import java.util.*;
public class LogoutAction extends ActionSupport { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		Map session = ActionContext.getContext().getSession(); 
		session.remove("logged-in");
		return "logout"; 
     }
 }