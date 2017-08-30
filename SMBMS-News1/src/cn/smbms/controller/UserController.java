package cn.smbms.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;

@Controller
@RequestMapping("/sys/user")
public class UserController {
	private Logger logger = Logger.getLogger(UserController.class);

	@Resource
	private UserService userService;
	@Resource
	private RoleService roleService;

	

	/**
	 * 防止用户直接进入显示界面
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/main")
	public String main(HttpSession session) {
		if (session.getAttribute(Constants.USER_SESSION) == null) {
			return "login";
		}
		return "frame";
	}

	/**
	 * 异常的测试方法
	 * 
	 * @param userCode
	 * @param userPassword
	 * @return
	 */
	@RequestMapping(value = "/exlogin", method = RequestMethod.GET)
	public String exLogin(@RequestParam String userCode,
			@RequestParam String userPassword) {
		logger.debug("exLogin===========");
		User user = userService.login(userCode, userPassword);
		if (null == user) {
			throw new RuntimeException("用户名或密码不正确");
		}
		return "redirect:/user/main";
	}

	/**
	 * 局部异常处理
	 * 
	 * @param e
	 * @param req
	 * @return
	 */
	/*
	 * 显示userlist
	 * 
	 * @ExceptionHandler(value={RuntimeException.class}) public String
	 * handlerException(RuntimeException e,HttpServletRequest req){
	 * req.setAttribute("e", e); return "error"; }
	 */
	/**
	 * 显示userlist页面 查找user用户
	 * 
	 * @param model
	 * @param queryUserName
	 * @param queryUserRole
	 * @param pageIndex
	 * @return
	 */
	@RequestMapping(value = "/userlist")
	public String getUserList(
			Model model,
			@RequestParam(value = "queryname", required = false) String queryUserName,
			@RequestParam(value = "queryUserRole", required = false) String queryUserRole,
			@RequestParam(value = "pageIndex", required = false) String pageIndex) {
		int queryUserRole1 = 0;
		List<User> userList = null;
		// 设置页面容量
		int pageSize = Constants.pageSize;
		// 当前页码
		int currentPageNo = 1;
		/**
		 * http://localhost:8090/SMBMS/userlist.do ----queryUserName --NULL
		 * http://localhost:8090/SMBMS/userlist.do?queryname= --queryUserName
		 * ---""
		 */
		if (queryUserName == null) {
			queryUserName = "";
		}
		if (queryUserRole != null && !queryUserRole.equals("")) {
			queryUserRole1 = Integer.parseInt(queryUserRole);
		}

		if (pageIndex != null) {
			try {
				currentPageNo = Integer.valueOf(pageIndex);
			} catch (NumberFormatException e) {
				return "redirect:/sys/user/syserror";
			}
		}
		// 总数量（表）
		int totalCount = userService
				.getUserCount(queryUserName, queryUserRole1);
		// 总页数
		PageSupport pages = new PageSupport();
		pages.setCurrentPageNo(currentPageNo);
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);

		int totalPageCount = pages.getTotalPageCount();

		// 控制首页和尾页
		if (currentPageNo < 1) {
			currentPageNo = 1;
		} else if (currentPageNo > totalPageCount) {
			currentPageNo = totalPageCount;
		}

		userList = userService.getUserList(queryUserName, queryUserRole1,
				currentPageNo, pageSize);
		model.addAttribute("userList", userList);
		List<Role> roleList = null;

		roleList = roleService.getRoleList();
		model.addAttribute("roleList", roleList);
		model.addAttribute("queryUserName", queryUserName);
		model.addAttribute("queryUserRole", queryUserRole);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("currentPageNo", currentPageNo);
		return "userlist";
	}

	/**
	 * 退出返回登录界面
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "logout")
	public String logout(HttpSession session) {
		session.removeAttribute(Constants.USER_SESSION);
		return "login";
	}

	/**
	 * 跳转到useradd页面
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "useradd", method = RequestMethod.GET)
	public String addUser(@ModelAttribute("user") User user) {
		return "useradd";
	}

	/**
	 * 单文件上传保存添加数据
	 * 
	 * @param user
	 * @param session
	 * @return
	 */
	/*
	 * @RequestMapping(value = "addusersave", method = RequestMethod.POST)
	 * public String addUserSave( User user, HttpSession session,
	 * HttpServletRequest request,
	 * 
	 * @RequestParam(value = "a_idPicPath", required = false) MultipartFile
	 * attach) { String idPicPath=null; //判断是否为空 if(!attach.isEmpty()){ String
	 * path=request.getSession().getServletContext().getRealPath("statics"+File.
	 * separator+"uploadfiles"); logger.info("uploadFile path========"+path);
	 * String oldFileName=attach.getOriginalFilename();//原文件名
	 * logger.info("uploadFile oldFileName===="+oldFileName); String
	 * prefix=FilenameUtils.getExtension(oldFileName);//原文件后缀
	 * logger.info("uploadFile prefix========"+prefix); int filesize=5000000;
	 * logger.info("uploadFile filesize======"+attach.getSize());
	 * if(attach.getSize()>filesize){ request.setAttribute("uploadFileError",
	 * "*上传大小不得超过500kb"); return "useradd"; }else
	 * if(prefix.equalsIgnoreCase("jpg") ||prefix.equalsIgnoreCase("png")
	 * ||prefix.equalsIgnoreCase("jpeg") ||prefix.equalsIgnoreCase("pneg")){
	 * String fileName=System.currentTimeMillis()+RandomUtils.nextInt(1000000)+
	 * "_Personal.jsp"; logger.info("new fileName======"+attach.getName()); File
	 * targetFile=new File(path,fileName); if(!targetFile.exists()){
	 * targetFile.mkdirs(); } try { attach.transferTo(targetFile); } catch
	 * (Exception e) { e.printStackTrace();
	 * request.setAttribute("uploadFileError","*上传失败"); return "useradd"; }
	 * idPicPath=path+File.separator+fileName; }else{
	 * request.setAttribute("uploadFileError", "*上传图片格式不正确"); return "useradd";
	 * } }
	 * 
	 * // 用session在Constants.USER_SESSION中拿取id值 user.setCreatedBy(((User)
	 * session.getAttribute(Constants.USER_SESSION)) .getId());
	 * user.setCreationDate(new Date()); user.setIdPicPath(idPicPath); if
	 * (userService.add(user)) { return "redirect:/user/userlist"; } return
	 * "useradd"; }
	 */
	/**
	 * 添加信息保存
	 * 
	 * @param user
	 * @param session
	 * @param request
	 * @param attachs
	 * @return
	 */
	@RequestMapping(value = "addusersave", method = RequestMethod.POST)
	public String addUserSave(
			User user,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam(value = "attachs", required = false) MultipartFile[] attachs) {
		String idPicPath = null;
		String workPicPath = null;
		String errorInfo = null;
		String path = request.getSession().getServletContext()
				.getRealPath("statics" + File.separator + "uploadfiles");
		logger.info("uploadFile path========" + path);
		boolean flag = true;
		for (int i = 0; i < attachs.length; i++) {
			MultipartFile attach = attachs[i];
			if (!attach.isEmpty()) {
				if (i == 0) {
					errorInfo = "uploadFileError";
				} else if (i == 1) {
					errorInfo = "uploadWpError";
				}
				String oldFileName = attach.getOriginalFilename();// 原文件名
				logger.info("uploadFile oldFileName====" + oldFileName);
				String prefix = FilenameUtils.getExtension(oldFileName);// 原文件后缀
				logger.info("uploadFile prefix========" + prefix);
				int filesize = 5000000;
				logger.info("uploadFile filesize======" + attach.getSize());
				if (attach.getSize() > filesize) {
					request.setAttribute(errorInfo, "*上传大小不得超过500kb");
					flag = false;
				} else if (prefix.equalsIgnoreCase("jpg")
						|| prefix.equalsIgnoreCase("png")
						|| prefix.equalsIgnoreCase("jpeg")
						|| prefix.equalsIgnoreCase("pneg")) {
					String fileName = System.currentTimeMillis()
							+ RandomUtils.nextInt(1000000) + "_Personal.jpg";
					logger.info("new fileName======" + attach.getName());
					File targetFile = new File(path, fileName);
					if (!targetFile.exists()) {
						targetFile.mkdirs();
					}
					// 保存errorInfo提示信息
					try {
						attach.transferTo(targetFile);
					} catch (Exception e) {
						e.printStackTrace();
						request.setAttribute(errorInfo, "*上传失败");
						flag = false;
					}
					if (i == 0) {
						idPicPath = File.separator + "statics" + File.separator
								+ "uploadfiles" + File.separator + fileName;
					} else if (i == 1) {
						workPicPath = File.separator + "statics"
								+ File.separator + "uploadfiles"
								+ File.separator + fileName;
					}
				} else {
					request.setAttribute(errorInfo, "*上传图片格式不正确");
					flag = false;
				}
			}
		}
		if (flag) {
			user.setCreatedBy(((User) session
					.getAttribute(Constants.USER_SESSION)).getId());
			user.setCreationDate(new Date());
			user.setIdPicPath(idPicPath);
			user.setWorkPicPath(workPicPath);
			if (userService.add(user)) {
				return "redirect:/sys/user/userlist";
			}
		}
		return "useradd";
	}

	/*
	 * //Spring 303添加
	 * 
	 * @RequestMapping(value="add",method=RequestMethod.GET) public String
	 * add(@ModelAttribute("user") User user){ return "add"; }
	 * 
	 * @RequestMapping(value="addsave",method=RequestMethod.POST) public String
	 * addSave(@Valid User user,BindingResult bindingResult,HttpSession
	 * session){ //用session在Constants.USER_SESSION中拿取id值
	 * if(bindingResult.hasErrors()){
	 * logger.debug("add user validated has error===="); return "add"; }
	 * user.setCreatedBy
	 * (((User)session.getAttribute(Constants.USER_SESSION)).getId());
	 * user.setCreationDate(new Date()); if(userService.add(user)){ return
	 * "redirect:/user/userlist"; } return "useradd"; }
	 */
	/**
	 * 进入修改页面
	 * 
	 * @param uid
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "usermodify", method = RequestMethod.GET)
	public String getUserById(@RequestParam String uid, Model model) {
		logger.debug("getUserById uid===========" + uid);
		List<Role> roleList = new ArrayList<Role>();
		User user = userService.getUserById(uid);
		roleList = roleService.getRoleList();
		model.addAttribute("user", user);
		model.addAttribute("roleList", roleList);
		return "usermodify";
	}

	/**
	 * 保存修改信息
	 * 
	 * @param user
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/usermodifysave", method = RequestMethod.POST)
	public String modifyUserSave(User user, HttpSession session) {
		logger.debug("modifyUserSave userid========");
		user.setModifyBy(((User) session.getAttribute(Constants.USER_SESSION))
				.getId());
		user.setModifyDate(new Date());
		if (userService.modify(user)) {
			return "redirect:/sys/user/userlist";
		}
		return "usermodify";
	}

	/**
	 * 查询显示单个信息
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/viewdan/{id}", method = RequestMethod.GET)
	public String view(@PathVariable String id, Model model) {
		logger.debug("view id ===========" + id);
		User user = userService.getUserById(id);
		model.addAttribute(user);
		return "userview";
	}

	/**
	 * user用户删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deluser")
	@ResponseBody
	public HashMap<String, String> delUser(@RequestParam String id) {
		Integer delId = 0;
		delId = Integer.parseInt(id);
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if (delId <= 0) {
			resultMap.put("delResult", "notexist");
		} else {
			if (userService.deleteUserById(delId)) {
				resultMap.put("delResult", "true");
			} else {
				resultMap.put("delResult", "flase");
			}
		}
		return resultMap;
	}

	/**
	 * 添加时判断账号是否存在
	 * 
	 * @param userCode
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/ifCode")
	public Object userCodeIsExist(String userCode) {
		HashMap<String, String> hasmap = new HashMap<String, String>();

		if (StringUtils.isNullOrEmpty(userCode)) {
			hasmap.put("userCode", "exist");
		} else {
			User user = new User();
			user = userService.selectUserCodeExist(userCode);
			if (null != user) {
				hasmap.put("userCode", "exist");
			} else {
				hasmap.put("userCode", "noexiat");
			}
		}
		return JSONArray.toJSONString(hasmap);
	}

	/**
	 * 本页面跳转显示
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET/*用来日期转换器
																 * ,produces={
																 * "application/json;charset=UTF-8"
																 * }
																 */)
	@ResponseBody
	public User view(@RequestParam String id) {
		/*
		 * String cjson=""; if(null==id||"".equals(id)){ return "nodata"; }else{
		 */
		User user = new User();
		try {
			user = userService.getUserById(id);
			// cjson=JSON.toJSONString(user);
		} catch (Exception e) {
			e.printStackTrace();
			// return "failed";
		}
		return user;
		// }
	}
	/**
	 * 跳转进入修改密码界面
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/gopwdmodify")
	public String gopwdmodify(){
		return "pwdmodify";
	}
	/**
	 * 判断密码
	 * @param oldpassword
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/panduan")
	@ResponseBody
	public HashMap<String,String> panduan(String oldpassword,HttpSession session){
		HashMap<String,String> hasmap=new HashMap<String, String>();
		String pwd=((User)session.getAttribute(Constants.USER_SESSION)).getUserPassword();
		if(null==pwd){
			hasmap.put("result","sessionerror");
		}else if(StringUtils.isNullOrEmpty(oldpassword)){
			hasmap.put("result", "error");
		}else{
			if(oldpassword.equals(pwd)){
				hasmap.put("result", "true");
			}else{
				hasmap.put("result", "false");
			}
		}
		return hasmap;
	}
	/**
	 * 保存密码
	 * @param newpassword
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/pwdmodifysave")
	public String pwdModifySave(String newpassword,HttpSession session){
		
		Integer id=((User)session.getAttribute(Constants.USER_SESSION)).getId();
		if(!StringUtils.isNullOrEmpty(newpassword)&&id!=null){
			if(userService.updatePwd(id, newpassword)){
				session.setAttribute(Constants.SYS_MESSAGE, "修改密码成功");
				session.removeAttribute(Constants.USER_SESSION);
			}
		}else{
			session.setAttribute(Constants.SYS_MESSAGE, "修改密码失败");
		}
		return "pwdmodify";
	}

}
