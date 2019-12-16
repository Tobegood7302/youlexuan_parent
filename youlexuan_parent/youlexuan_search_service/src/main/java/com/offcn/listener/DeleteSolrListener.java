package com.offcn.listener;

import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.List;

@Component
public class DeleteSolrListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        try {
            ObjectMessage text = (ObjectMessage) message;

            Long[] ids = (Long[]) text.getObject();

            itemSearchService.deleteData(ids);

            System.out.println(">>>>消息消费成功: manager删除solr数据");

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
