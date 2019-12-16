package com.offcn.solr;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-*.xml")
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void testImportData() {
        TbItemExample ex = new TbItemExample();
        TbItemExample.Criteria c = ex.createCriteria();
        // 查询正常状态下的item
        c.andStatusEqualTo("1");
        List<TbItem> itemList = itemMapper.selectByExample(ex);

        System.out.println("---------商品列表---------");
        for (TbItem item : itemList) {
            // 将spec中的json字符串转换为map
            Map<String, Object> map = JSON.parseObject(item.getSpec());
            // 新建map存储
            Map newMap = new HashMap();
            for (String key : map.keySet()) {
                newMap.put(Pinyin.toPinyin(key, "").toLowerCase(), map.get(key));
            }
            // 给动态域注解的字段赋值
            item.setSpecMap(newMap);
            System.out.println(item.getTitle() + ">>>>" + item.getPrice() + ">>>>" + item.getSpec());
        }

        System.out.println("-------录入开始--------");

        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();

        System.out.println("-------录入结束--------");

    }




}
