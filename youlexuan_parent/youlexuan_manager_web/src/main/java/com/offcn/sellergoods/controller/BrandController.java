package com.offcn.sellergoods.controller;

import java.util.List;
import java.util.Map;

import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbBrand;
import com.offcn.sellergoods.service.BrandService;

@Controller
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    @ResponseBody
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }

    /**
     * 返回指定页码、行数 品牌列表
     * @return
     */
    @RequestMapping("/findPage")
    @ResponseBody
    public PageResult findPage(int page, int size){
        return brandService.findPage(page, size);
    }

    @RequestMapping("/add")
    @ResponseBody
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return new Result(true, "添加品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加品牌失败");
        }
    }

    @RequestMapping("/findOne")
    @ResponseBody
    public TbBrand findOne(long id){
        return brandService.findOne(id);
    }


    @RequestMapping("/update")
    @ResponseBody
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true, "修改品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改品牌失败");
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Result delete(long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true, "删除品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除品牌失败");
        }
    }

    @RequestMapping("/search")
    @ResponseBody
    public PageResult search(@RequestBody TbBrand brand, int page, int size){
        return brandService.findPage(brand, page, size);
    }

    @RequestMapping("/selectBrandList")
    @ResponseBody
    public List<Map> selectBrandList(){
        return brandService.selectBrandList();
    }


}