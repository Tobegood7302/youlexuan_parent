package com.offcn.listener;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

@Component
public class DeletePageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage obj = (ObjectMessage) message;

            Long[] ids = (Long[]) obj.getObject();

            itemPageService.deleteItemHtml(ids);

            System.out.println(">>>>>>消费成功: 删除商品详情页");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
