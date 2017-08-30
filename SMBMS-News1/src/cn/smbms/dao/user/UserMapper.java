package cn.smbms.dao.user;

import java.sql.Connection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.smbms.pojo.User;

public interface UserMapper {
	/**
	 * ͨ���û��˺Ų���
	 * @param userCode
	 * @return ����User
	 * @throws Exception
	 */
	public User getLoginUser(@Param("userCode")String userCode,@Param("userPassword")String userPassword)throws Exception;
	/**
	 * ������ʾUser��Ϣ
	 * @param userName
	 * @param userRole
	 * @param currentPageNo
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public List<User> getUserList(@Param("userName")String userName,@Param("userRole")Integer userRole,@Param("currentPageNo")Integer currentPageNo,@Param("pageSize")Integer pageSize)throws Exception;
	/**
	 * ������Ϣ������
	 * @param userName
	 * @param userRole
	 * @return
	 * @throws Exception
	 */
	public int getUserCount(@Param("userName")String userName,@Param("userRole")Integer userRole)throws Exception;
	/**
	 * ���User�û�
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public int addUser(User user)throws Exception;
	
	/**
	 * ɾ���û�
	 * @param delId
	 * @return int����
	 * @throws Exception
	 */
	public int deleteUserById(Integer delId)throws Exception;
	/**
	 * �޸� User�û���Ϣ
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public int modify(User user)throws Exception;
	/**
	 * �޸�User�û�����
	 * @param id
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public int updatePwd(@Param("id")Integer id,@Param("pwd")String pwd)throws Exception;
	/**
	 * ͨ��UserID ����User�û�
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public User getUserById(String id)throws Exception;
	
	public User getUserByUserCode(String userCode)throws Exception;
}
