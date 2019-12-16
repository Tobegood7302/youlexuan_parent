package com.offcn.user.service;



import com.offcn.entity.PageResult;
import com.offcn.pojo.TbUser;

import java.util.List;

/**
 * user服务层接口
 * @author cc
 *
 */
public interface UserService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbUser> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	public void add(TbUser user);


	/**
	 * 修改
	 */
	public void update(TbUser user);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbUser findOne(Long id);


	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbUser user, int pageNum, int pageSize);

	/**
	 * 生成短信验证码
	 * @param phone
	 */
	public void sendCode(String phone);

	/**
	 * 验证 验证码是否一致
	 * @param phone
	 * @param smsCode
	 * @return
	 */
    boolean checkSmsCode(String phone, String smsCode);

    void removeCode(String phone);
}
