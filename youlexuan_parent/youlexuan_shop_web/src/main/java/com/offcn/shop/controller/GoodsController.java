package com.offcn.shop.controller;
import java.util.List;

import com.offcn.group.Goods;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbGoods;
import com.offcn.sellergoods.service.GoodsService;

import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
/**
 * goodscontroller
 * @author senqi
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	/*@Reference
	private ItemSearchService itemSearchService;*/

	@Autowired
	private ActiveMQQueue youlexuanShopDeletesolrQueue;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private ActiveMQTopic youlexuanDelPageTopic;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){

		// 关联商家
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();

		goods.getGoods().setSellerId(sellerId);

		// 设置默认的审核状态
		goods.getGoods().setAuditStatus("0");
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){

		// 设置默认的审核状态
		goods.getGoods().setAuditStatus("0");

		try {
			goodsService.update(goods);

			// 修改商品, 只需要将solr中对应的商品删除
//			itemSearchService.deleteData(new Long[]{goods.getGoods().getId()});

			jmsTemplate.convertAndSend(youlexuanShopDeletesolrQueue, new Long[]{goods.getGoods().getId()});

			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);

			// 删除solr索引
//			itemSearchService.deleteData(ids);
			jmsTemplate.convertAndSend(youlexuanShopDeletesolrQueue, ids);

			// 删除商品详情页
			jmsTemplate.convertAndSend(youlexuanDelPageTopic, ids);

			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int size  ){

		// 添加商家id
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerId);

		return goodsService.findPage(goods, page, size);
	}

	@RequestMapping("/updateAuditState")
	public Result updateAuditState(Long [] ids, String status){
		try {
			goodsService.updateAuditState(ids, status);

			// 商家提交重审, 删除solr中对应数据
			if ("0".equals(status)) {
//				itemSearchService.deleteData(ids);

				jmsTemplate.convertAndSend(youlexuanShopDeletesolrQueue, ids);

			}

			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	
}
