package cn.smbms.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;

@Controller
@RequestMapping
public class LoginController {
	@Resource
	private UserService userService;
	@Resource
	private RoleService roleService;
	
	/**
	 * 跳转到登录页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/login")
	public String login() {
		return "login";
	}

	/**
	 * 实现登录跳转
	 * 
	 * @param userCode
	 * @param userPassword
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dologin", method = RequestMethod.POST)
	public String land(@RequestParam String userCode,
			@RequestParam String userPassword, HttpSession session,
			HttpServletRequest request) {
		
		User user = userService.login(userCode, userPassword);
		if (user != null) {
			session.setAttribute(Constants.USER_SESSION, user);
			return "redirect:/sys/user/main"; // redirect重定向
		} else {
			request.setAttribute("error", "用户名或密码不正确");
			return "login";
		}
	}
}
