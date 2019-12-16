package com.offcn.search.service;

import com.offcn.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 查询solr索引库
     * @param searchMap
     * @return
     */
    public Map<String, Object> search(Map searchMap);

    // 新增solr索引库
    public void importData(List<TbItem> itemList);

    /**
     * 删除solr索引库
     * @param ids 表示多个TbGoods id
     */
    public void deleteData(Long[] ids);

}
