package net.wdmsfunc;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.*;
import com.opensymphony.xwork2.ActionContext;
import java.util.*;

public class LoginAction extends ActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7219561392019888383L;
	private String username = null;
	private String password = null;
	
	public String execute() throws Exception {
		System.out.println("Validating login");
		if (this.username.equals("admin") && this.password.equals("admin123")) {
			Map session = ActionContext.getContext().getSession(); 
			session.put("logged-in","true");
			System.out.println("Validating login");
			return "login";
		} else {
			addActionError ("Invalid user name or password! Please try again!");
			return "error";
		}
	}

	public String logout() throws Exception {
		Map session = ActionContext.getContext().getSession(); 
		session.remove("logged-in");
		return "logout";
	}
	
	@RequiredStringValidator(message="Please enter username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@RequiredStringValidator(message="Please enter password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
