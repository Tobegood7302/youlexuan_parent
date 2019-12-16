package com.offcn.user.service.impl;

import com.alibaba.druid.filter.AutoLoad;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbUserMapper;
import com.offcn.pojo.TbUser;
import com.offcn.pojo.TbUserExample;
import com.offcn.pojo.TbUserExample.Criteria;
import com.offcn.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * user服务实现层
 * @author cc
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private RedisTemplate redisTemplate;

	@Value("${sign}")
	private String sign;

	@Value("${templateCode}")
	private String templateCode;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 分页
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page = (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {

		// 创建日期
		user.setCreated(new Date());
		// 修改日期
		user.setUpdated(new Date());
		// 密码加密
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));

		userMapper.insert(user);

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	/**
	 * 分页+查询
	 */
	@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		if(user != null){			
						if(user.getUsername() != null && user.getUsername().length() > 0){
				criteria.andUsernameLike("%" + user.getUsername() + "%");
			}			if(user.getPassword() != null && user.getPassword().length() > 0){
				criteria.andPasswordLike("%" + user.getPassword() + "%");
			}			if(user.getPhone() != null && user.getPhone().length() > 0){
				criteria.andPhoneLike("%" + user.getPhone() + "%");
			}			if(user.getEmail() != null && user.getEmail().length() > 0){
				criteria.andEmailLike("%" + user.getEmail() + "%");
			}			if(user.getSourceType() != null && user.getSourceType().length() > 0){
				criteria.andSourceTypeLike("%" + user.getSourceType() + "%");
			}			if(user.getNickName() != null && user.getNickName().length() > 0){
				criteria.andNickNameLike("%" + user.getNickName() + "%");
			}			if(user.getName() != null && user.getName().length() > 0){
				criteria.andNameLike("%" + user.getName() + "%");
			}			if(user.getStatus() != null && user.getStatus().length() > 0){
				criteria.andStatusLike("%" + user.getStatus() + "%");
			}			if(user.getHeadPic() != null && user.getHeadPic().length() > 0){
				criteria.andHeadPicLike("%" + user.getHeadPic() + "%");
			}			if(user.getQq() != null && user.getQq().length() > 0){
				criteria.andQqLike("%" + user.getQq() + "%");
			}			if(user.getIsMobileCheck() != null && user.getIsMobileCheck().length() > 0){
				criteria.andIsMobileCheckLike("%" + user.getIsMobileCheck() + "%");
			}			if(user.getIsEmailCheck() != null && user.getIsEmailCheck().length() > 0){
				criteria.andIsEmailCheckLike("%" + user.getIsEmailCheck() + "%");
			}			if(user.getSex() != null && user.getSex().length() > 0){
				criteria.andSexLike("%" + user.getSex() + "%");
			}
		}
		
		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void sendCode(String phone) {

		// 生成6位随机数
		String code = (long) (Math.random() * 899999 + 100000) + "";
		System.out.println("验证码:" + code);

		// 将验证码存入缓存中, 注册时做验证
		redisTemplate.boundHashOps("phoneCode").put(phone, code);

		// 发送短信验证码

		Map<String, String> map = new HashMap<>();

		map.put("phone", phone);
		map.put("sign", sign);
		map.put("code", code);
		map.put("templateCode", templateCode);

		jmsTemplate.convertAndSend("offcn_sms", map);

	}

	@Override
	public boolean checkSmsCode(String phone, String smsCode) {

		String code = (String) redisTemplate.boundHashOps("phoneCode").get(phone);

		if (code.equals(smsCode)) {
			return true;
		}
		return false;
	}

	@Override
	public void removeCode(String phone) {

		redisTemplate.boundHashOps("phoneCode").delete(phone);

	}

}
