package com.offcn;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TestFreemarker {

    public static void main(String[] args) throws Exception {
        // 1. 创建一个Configuration对象, 参数为freemarker版本号
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 2. 设置模板文件所在路径
        configuration.setDirectoryForTemplateLoading(new File("D:\\我的\\学习\\Java\\五阶段\\IdeaProjects\\youlexuan\\demo_freemarker\\src\\main\\resources"));
        // 3. 设置模板文件的字符集
        configuration.setDefaultEncoding("utf-8");
        // 4. 加载模板, 创建一个模板对象
        Template template = configuration.getTemplate("test.ftl");
        // 5. 创建模板使用的字符集(可以是pojo, 也可以是map)
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "小黑");
        map.put("message", "SSR");
        map.put("success", true);

        List goodsList=new ArrayList();
        Map goods1=new HashMap();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);
        Map goods2=new HashMap();
        goods2.put("name", "香蕉");
        goods2.put("price", 2.5);
        Map goods3=new HashMap();
        goods3.put("name", "橘子");
        goods3.put("price", 3.2);
        goodsList.add(goods1);
        goodsList.add(goods2);
        goodsList.add(goods3);
        map.put("goodsList", goodsList);

        map.put("today", new Date());
        map.put("money", 1200000000);

        // 6. 创建一个Writer对象, 一般是FileWriter, 指定文件的文件名
        FileWriter out = new FileWriter("E:\\temp\\demo.html");
        // 7. 调用模板方法process输出文件
        template.process(map, out);
        // 8. 关闭流
        out.close();

    }
}
