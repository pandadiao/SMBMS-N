package cn.smbms.service.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.smbms.dao.BaseDao;
import cn.smbms.dao.user.UserMapper;
import cn.smbms.pojo.User;

/**
 * service层捕获异常，进行事务处理
 * 事务处理：调用不同dao的多个方法，必须使用同一个connection（connection作为参数传递）
 * 事务完成之后，需要在service层进行connection的关闭，在dao层关闭（PreparedStatement和ResultSet对象）
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService{
	@Resource
	private UserMapper userMapper;
/*	public UserServiceImpl(){
		userDao = new UserDaoImpl();
	}*/
	public boolean add(User user) {
		boolean flag = false;
		Connection connection = null;
		try {
			connection = BaseDao.getConnection();
			connection.setAutoCommit(false);//开启JDBC事务管理
			int updateRows = userMapper.addUser(user);
			connection.commit();
			if(updateRows > 0){
				flag = true;
				System.out.println("add success!");
			}else{
				System.out.println("add failed!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				System.out.println("rollback==================");
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			//在service层进行connection连接的关闭
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}
	public User login(String userCode, String userPassword) {
		Connection connection = null;
		User user = null;
		try {
			connection = BaseDao.getConnection();
			user = userMapper.getLoginUser(userCode, userPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		
		//匹配密码
		if(null != user){
			if(!user.getUserPassword().equals(userPassword))
				user = null;
		}
		
		return user;
	}
	public List<User> getUserList(String queryUserName,int queryUserRole,int currentPageNo, int pageSize) {
		Connection connection = null;
		List<User> userList = null;
		System.out.println("queryUserName ---- > " + queryUserName);
		System.out.println("queryUserRole ---- > " + queryUserRole);
		System.out.println("currentPageNo ---- > " + currentPageNo);
		System.out.println("pageSize ---- > " + pageSize);
		try {
			connection = BaseDao.getConnection();
			userList = userMapper.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return userList;
	}
	public User selectUserCodeExist(String userCode) {
		Connection connection = null;
		User user = null;
		try {
			connection = BaseDao.getConnection();
			user = userMapper.getUserByUserCode(userCode);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return user;
	}
	public boolean deleteUserById(Integer delId) {
		Connection connection = null;
		boolean flag = false;
		try {
			connection = BaseDao.getConnection();
			if(userMapper.deleteUserById(delId) > 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}
	public User getUserById(String id) {
		User user = null;
		Connection connection = null;
		try{
			connection = BaseDao.getConnection();
			user = userMapper.getUserById(id);
		}catch (Exception e) {
			e.printStackTrace();
			user = null;
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return user;
	}
	public boolean modify(User user) {
		Connection connection = null;
		boolean flag = false;
		try {
			connection = BaseDao.getConnection();
			if(userMapper.modify(user)> 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}
	public boolean updatePwd(int id, String pwd) {
		boolean flag = false;
		Connection connection = null;
		try{
			connection = BaseDao.getConnection();
			if(userMapper.updatePwd(id, pwd) > 0)
				flag = true;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}
	
	public int getUserCount(String queryUserName, int queryUserRole) {
		Connection connection = null;
		int count = 0;
		System.out.println("queryUserName ---- > " + queryUserName);
		System.out.println("queryUserRole ---- > " + queryUserRole);
		try {
			connection = BaseDao.getConnection();
			count = userMapper.getUserCount(queryUserName, queryUserRole);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return count;
	}
	
}
