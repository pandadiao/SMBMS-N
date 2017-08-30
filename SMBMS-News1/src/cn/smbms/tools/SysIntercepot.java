package cn.smbms.tools;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.smbms.pojo.User;
/**
 * 拦截器类
 * @author Administrator
 *
 */

public class SysIntercepot extends HandlerInterceptorAdapter {
	private Logger logger = Logger.getLogger(SysIntercepot.class);

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object object) throws Exception {
		logger.debug("SysInterceptor preHandle==================");
		HttpSession session = request.getSession();

		User user = (User) session.getAttribute(Constants.USER_SESSION);

		/*if (request.getRequestURI().indexOf("login") > -1) {
			return true;
		} else*/ if (null == user) {
			response.sendRedirect(request.getContextPath() + "/401.jsp");
			return false;
		}
		return true;
	}
}