package com.offcn.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.content.service.ContentService;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbContentMapper;
import com.offcn.pojo.TbContent;
import com.offcn.pojo.TbContentExample;
import com.offcn.pojo.TbContentExample.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * content服务实现层
 * @author cc
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 分页
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {

		contentMapper.insert(content);

		// 清除缓存
		redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){

		// 存储改变前广告类型ID
		Long oldCategoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		// 判断广告类型ID是否改变
		if (oldCategoryId.longValue() != content.getCategoryId().longValue()) {
			// 改变了
			// 清除改变前存储广告类型ID的缓存
			redisTemplate.boundHashOps("contentList").delete(oldCategoryId);
		}

		// 修改
		contentMapper.updateByPrimaryKey(content);

		// 清除现在的存储广告类型ID的缓存
		redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			// 查找广告类型ID
			TbContent content = contentMapper.selectByPrimaryKey(id);
			// 清除缓存
			redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());

			// 删除
			contentMapper.deleteByPrimaryKey(id);
		}

	}
	
	/**
	 * 分页+查询
	 */
	@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content != null){			
						if(content.getTitle() != null && content.getTitle().length() > 0){
				criteria.andTitleLike("%" + content.getTitle() + "%");
			}			if(content.getUrl() != null && content.getUrl().length() > 0){
				criteria.andUrlLike("%" + content.getUrl() + "%");
			}			if(content.getPic() != null && content.getPic().length() > 0){
				criteria.andPicLike("%" + content.getPic() + "%");
			}			if(content.getStatus() != null && content.getStatus().length() > 0){
				criteria.andStatusLike("%" + content.getStatus() + "%");
			}
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public List<TbContent> findByCategoryId(Long categoryId) {

		// 缓存
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("contentList").get(categoryId);

		// 判断是否有缓存
		if (contentList == null) {
			// 无缓存
			TbContentExample ex = new TbContentExample();
			Criteria c = ex.createCriteria();
			c.andCategoryIdEqualTo(categoryId);
			contentList = contentMapper.selectByExample(ex);

			// 添加缓存
			redisTemplate.boundHashOps("contentList").put(categoryId, contentList);

			System.out.println("从数据库中得到数据>>>>>>>>>>>>>>>>>>>");
		} else {
			System.out.println("从缓存中得到数据>>>>>>>>>>>>>>>>>>>");
		}


		return contentList;
    }

}
