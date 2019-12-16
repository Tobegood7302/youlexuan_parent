package com.offcn.listener;

import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class ShopDeleteSolrListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        try {
            ObjectMessage text = (ObjectMessage) message;

            Long[] ids = (Long[]) text.getObject();

            itemSearchService.deleteData(ids);

            System.out.println(">>>>消息消费成功: shop删除solr数据");

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
