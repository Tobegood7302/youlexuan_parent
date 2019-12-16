package com.offcn.sellergoods.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 返回分页列表
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);

    public PageResult findPage(TbBrand brand, int pageNum, int pageSize);

    void add(TbBrand brand);

    void update(TbBrand brand);

    void delete(long[] ids);

    TbBrand findOne(long id);

    List<Map> selectBrandList();
}
