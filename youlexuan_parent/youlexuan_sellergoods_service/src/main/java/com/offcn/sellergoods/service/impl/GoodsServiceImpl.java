package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.group.Goods;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * goods服务实现层
 * @author senqi
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper sellerMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 分页
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {

		goodsMapper.insert(goods.getGoods());

		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//设置ID
		goodsDescMapper.insert(goods.getGoodsDesc());

		saveItemList(goods);


	}

	private void saveItemList(Goods goods) {
		if ("1".equals(goods.getGoods().getIsEnableSpec())) {
			// 将sku添加到tb_item中
			for (TbItem item : goods.getItemList()) {

				// 标题
				String title = goods.getGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec(), Map.class);
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);

				saveItemVal(goods, item);

				itemMapper.insert(item);

			}
		}
	}

	private void saveItemVal(Goods goods, TbItem item) {
		// 商品SPU编号
		item.setGoodsId(goods.getGoods().getId());
		// 商家编号
		item.setSellerId(goods.getGoods().getSellerId());
		// 商品分类编号
		item.setCategoryid(goods.getGoods().getCategory3Id());
		//创建日期
		item.setCreateTime(new Date());
		//修改日期
		item.setUpdateTime(new Date());
		// 添加品牌的名字，冗余设计，为了页面显示方便
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		//  添加分类的显示值，冗余设计，为了页面显示方便
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		// 添加店铺的名字，冗余设计，为了页面显示方便
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());
		// 从good_desc的图片属性中默认取第一张
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class) ;
		if(imageList.size()>0){
			item.setImage ( (String)imageList.get(0).get("url"));
		}
	}


	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){

		// 更新tbGoods
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		// 更新tbGoodsDesc
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		// 更新tbItem
		// 先删除再添加

		// 删除
		TbItemExample ex = new TbItemExample();
		TbItemExample.Criteria c = ex.createCriteria();
		c.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(ex);

		// 添加
		saveItemList(goods);

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){

		// 查询tbGoods
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		// 查询tbGoodsDesc
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		// 查询tbItem
		TbItemExample ex = new TbItemExample();
		TbItemExample.Criteria c = ex.createCriteria();
		c.andGoodsIdEqualTo(id);
		List<TbItem> itemList = itemMapper.selectByExample(ex);

		// 返回Goods
		return new Goods(tbGoods, tbGoodsDesc, itemList);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			// tb_goods
			// 逻辑删除(改变isDelete)
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);

			// tb_item 的status属性改为3
			TbItemExample ex = new TbItemExample();
			TbItemExample.Criteria c = ex.createCriteria();
			c.andGoodsIdEqualTo(goods.getId());

			List<TbItem> itemList = itemMapper.selectByExample(ex);
			for (TbItem item : itemList) {
				item.setStatus("3");
				itemMapper.updateByPrimaryKey(item);
			}


		}
	}
	
	/**
	 * 分页+查询
	 */
	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods != null){			
						if(goods.getSellerId() != null && goods.getSellerId().length() > 0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}			if(goods.getGoodsName() != null && goods.getGoodsName().length() > 0){
				criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
			}			if(goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0){
				criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
			}			if(goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0){
				criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
			}			if(goods.getCaption() != null && goods.getCaption().length() > 0){
				criteria.andCaptionLike("%" + goods.getCaption() + "%");
			}			if(goods.getSmallPic() != null && goods.getSmallPic().length() > 0){
				criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
			}			if(goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0){
				criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
			}
			criteria.andIsDeleteIsNull();
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public void updateAuditState(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}
    }

	@Override
	public List<TbItem> findItemByGoodsId(Long[] ids) {

		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria c = example.createCriteria();
		c.andGoodsIdIn(Arrays.asList(ids));

		// 状态: 正常
		c.andStatusEqualTo("1");

		List<TbItem> itemList = itemMapper.selectByExample(example);

		return itemList;
	}

}
