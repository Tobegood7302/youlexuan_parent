package com.offcn.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import org.apache.zookeeper.data.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {

        Map<String, Object> map = new HashMap<>();

        // 将关键字中的空格去掉
        searchMap.put("keywords", ((String) searchMap.get("keywords")).replace(" ", ""));

        // 关键字高亮查询
        hiSearch(searchMap, map);

        // 查询分组
        categoryListSearch(searchMap, map);




        return map;
    }

    @Override
    public void importData(List<TbItem> itemList) {
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

        System.out.println("-------导入solr库开始--------");

        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();

        System.out.println("-------导入solr库结束--------");
    }

    @Override
    public void deleteData(Long[] ids) {
        Query query = new SimpleQuery();
        Criteria c = new Criteria("item_goodsid").in(ids);
        query.addCriteria(c);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 得到分类相对应的品牌和规格
     * @param category
     * @param map
     */
    private void brandAndSpecListSearch(String category, Map<String, Object> map) {

        // 获取模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId != null) {
            // 根据模板id查询品牌
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
            // 返回值添加品牌列表
            map.put("brandList", brandList);

            // 根据模板id查询规格
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
            // 返回值添加规格列表
            map.put("specList", specList);
        }


    }

    /**
     * 根据检索的关键字查询分类: 考虑分组
     * @param searchMap
     * @param map
     */
    private void categoryListSearch(Map searchMap, Map<String, Object> map) {
        // 定义list集合
        List<String> list = new ArrayList<>();

        // 定义查询
        Query query = new SimpleQuery();
        Criteria c = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(c);

        // 添加分组信息
        // 设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        // 得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);

        // 根据列得到分组结果值
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");

        // 得到分组入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();

        // 得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());
        }

        // 将list放入map中
        map.put("category", list);

        // 返回值添加品牌列表和规格
        brandAndSpecListSearch(list.get(0), map);

    }

    /**
     * 高亮, 关键字, 分类, 品牌, 规格, 价格, 排序的查询
     * @param searchMap
     * @param map
     */
    private void hiSearch(Map searchMap, Map<String, Object> map) {
        // 创建查询条件
        HighlightQuery query = new SimpleHighlightQuery();
        // 设置高亮的域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        // 设置高亮的前缀
        highlightOptions.setSimplePrefix("<span style='color:red'>");
        // 设置高亮的后缀
        highlightOptions.setSimplePostfix("</span>");
        // 设置高亮选项
        query.setHighlightOptions(highlightOptions);


        // 添加匹配条件
        // 1. 关键字 + 高亮
        Criteria c = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(c);

        // 2. 分类条件
        if (!"".equals(searchMap.get("category"))) {

            Criteria categoryCriteria = new Criteria("item_category").is(searchMap.get("category"));

            FilterQuery filterQuery = new SimpleFacetQuery(categoryCriteria);
            query.addFilterQuery(filterQuery);
        }
        // 3. 品牌条件
        if (!"".equals(searchMap.get("brand"))) {

            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFacetQuery(brandCriteria);
            query.addFilterQuery(filterQuery);
        }
        // 4. 规格条件
        if (searchMap.get("spec") != null) {
            Map<String, String> spec = (Map) searchMap.get("spec");
            for (String key : spec.keySet()) {
                Criteria brandCriteria = new Criteria("item_spec_" + Pinyin.toPinyin(key, "").toLowerCase()).is(spec.get(key));
                FilterQuery filterQuery = new SimpleFacetQuery(brandCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        // 5.价格条件
        if (!"".equals(searchMap.get("price"))) {

            // 将前端传回来的price分割
            String[] prices = ((String) searchMap.get("price")).split("-");

            // 价格下限
            Criteria priceCriteria1 = new Criteria("item_price").greaterThanEqual(prices[0]);
            FilterQuery fileterQuery1 = new SimpleFacetQuery(priceCriteria1);
            query.addFilterQuery(fileterQuery1);
            // 价格上限
            // 判断上限是否是无穷(*)
            if (!"*".equals(prices[1])) {
                Criteria priceCriteria2 = new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery fileterQuery2 = new SimpleFacetQuery(priceCriteria2);
                query.addFilterQuery(fileterQuery2);
            }
        }

        // 6. 排序
        if (!"".equals(searchMap.get("sortName")) && !"".equals(searchMap.get("sortVal"))) {
            Sort sort = null;
            if ("asc".equals(searchMap.get("sortVal"))) {
                sort = new Sort(Sort.Direction.ASC, "item_" + searchMap.get("sortName"));
            } else if ("desc".equals(searchMap.get("sortVal"))) {
                sort = new Sort(Sort.Direction.DESC, "item_" + searchMap.get("sortName"));
            }
            query.addSort(sort);
        }

        // 7. 分页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize = 20;
        }
        query.setOffset((pageNo - 1) * pageSize);
        query.setRows(pageSize);


        // 查询solr
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        // 替换item_title
        // 遍历高亮
        for (HighlightEntry<TbItem> h : page.getHighlighted()) {
            // 获得原实体类
            TbItem item = h.getEntity();
            // 可能是复制域命中的高亮, 所以最后的不是item_title高亮的, 会报数组越界错误, 所以需要判断
            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
                // 设置高亮的结果
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }

        // 添加分页属性到map
        // 总页数
        map.put("totalPages", page.getTotalPages());
        // 总记录数
        map.put("total", page.getTotalElements());

        // 添加到map.rows
        map.put("rows", page.getContent());
    }
}
