package com.offcn.sellergoods.controller;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.offcn.group.Goods;
import com.offcn.pojo.TbItem;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
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

	/*@Reference(timeout=4000)
	private ItemPageService itemPageService;*/

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private ActiveMQQueue youlexuanImportsolrQueue;

	@Autowired
	private ActiveMQQueue youlexuanDeletesolrQueue;

	@Autowired
	private ActiveMQTopic youlexuanCreatePageTopic;
	
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
		try {
			goodsService.update(goods);
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
		return goodsService.findPage(goods, page, size);
	}

	/**
	 * 修改商品状态
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateAuditState")
	public Result updateAuditState(Long [] ids, String status){
		try {
			goodsService.updateAuditState(ids, status);

			// 审核通过
			if ("1".equals(status)) {
				// 向solr中导入数据
				List<TbItem> itemList = goodsService.findItemByGoodsId(ids);

//				itemSearchService.importData(itemList);

				// 开始进行解耦处理:
				// 确保发送端和接收端的消息类型是一致的
				// 当遇到较复杂的类型, 需要发送时, 建议先处理为字符串, 以文本的形式发送

				String itemListStr = JSON.toJSONString(itemList);
				jmsTemplate.convertAndSend(youlexuanImportsolrQueue, itemListStr);

				// 生成静态页
				// 生成商品详情页
//				for (Long id : ids) {
//					itemPageService.genItemHtml(id);
//				}

				jmsTemplate.convertAndSend(youlexuanCreatePageTopic, ids);

			} else if ("2".equals(status)) {

//				itemSearchService.deleteData(ids);

				jmsTemplate.convertAndSend(youlexuanDeletesolrQueue, ids);

			}

			return new Result(true, "更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "更新失败");
		}
	}

	/**
	 * 生成静态页面
	 * @param goodsId
	 */
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId) {

//		itemPageService.genItemHtml(goodsId);

	}
	
}
