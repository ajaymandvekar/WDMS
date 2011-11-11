package net.wdmsfunc;
import java.util.Map;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class DocumentUpdateCheck extends AbstractInterceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String USER_KEY = "logged-in";
	private static final String USER_PREVILIGE = "previlige";
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Map session = invocation.getInvocationContext().getSession();
		Object doe = invocation.getAction();
		if(session.get(USER_KEY) == null) {
			addActionError(invocation, "You must be authenticated to access this page");
			return Action.ERROR;
		}
		else if(session.get(USER_PREVILIGE) != "-1")
		{
			addActionError(invocation, "You must be authenticated to access this page");
			return Action.ERROR;
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
