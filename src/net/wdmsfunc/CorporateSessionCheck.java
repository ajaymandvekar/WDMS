package net.wdmsfunc;

import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class CorporateSessionCheck extends AbstractInterceptor {

	private static final String USER_KEY = "logged-in";
	private static final String USER_PREVILIGE = "previlige";

	public String intercept(ActionInvocation invocation) throws Exception {
		Map session = invocation.getInvocationContext().getSession();
		if(session.get(USER_KEY) == null) {
			addActionError(invocation, "You must be authenticated to access this page");
			return "login";
		}
		else if(session.get(USER_PREVILIGE) != "5")
		{
			addActionError(invocation, "You must be authenticated to access this page");
			return "login";
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