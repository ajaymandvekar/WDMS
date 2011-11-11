package net.wdmsfunc;

import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.StrutsStatics;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class NoCachingHeadersInterceptor extends AbstractInterceptor {
	public String intercept(ActionInvocation invocation) throws Exception {
		final ActionContext context = invocation.getInvocationContext();
		final HttpServletResponse response = (HttpServletResponse)
		context.get(StrutsStatics.HTTP_RESPONSE);
		if(response!=null) {
			response.setHeader("Cache-Control","no-cache, no-store, must-revalidate, proxy-revalidate, cached-pages");
			response.setHeader("Pragma","no-cache");
			response.setHeader("Expires","0");
		}
		return invocation.invoke();
	}
}
