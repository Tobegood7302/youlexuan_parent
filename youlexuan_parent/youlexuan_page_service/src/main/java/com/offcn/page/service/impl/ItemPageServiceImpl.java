package com.offcn.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pageDir}")
    private String pageDir;

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;


    @Override
    public boolean genItemHtml(Long goodsId) {
        FileWriter out = null;

        try {
            Configuration conf = freemarkerConfig.getConfiguration();

            // 获取模板
            Template template = conf.getTemplate("item.ftl");

            // 数据
            Map<String, Object> map = new HashMap<>();

            // 1. 获取goods数据
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            map.put("goods", goods);

            // 2. 获取三级分类名称
            String itemCat1Name = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2Name = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3Name = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            map.put("itemCat1Name", itemCat1Name);
            map.put("itemCat2Name", itemCat2Name);
            map.put("itemCat3Name", itemCat3Name);

            // 3. 获取goodsDesc数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            map.put("goodsDesc", goodsDesc);

            // 4. 获取规格列表
            TbItemExample ex = new TbItemExample();
            TbItemExample.Criteria c = ex.createCriteria();
            // 本商品的
            c.andGoodsIdEqualTo(goodsId);
            // 正常状态下
            c.andStatusEqualTo("1");
            // 排序: 默认排第一个
            ex.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(ex);
            map.put("itemList", itemList);

            // 输出
            out = new FileWriter(pageDir + goodsId + ".html");

            template.process(map, out);

            System.out.println(pageDir + goodsId + ".html页面生成成功");

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {

        for (Long goodsId : goodsIds) {
            try {
                new File(pageDir + goodsId + ".html").delete();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
