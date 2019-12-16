package com.offcn.listener;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.Serializable;

@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        try {
            ObjectMessage obj = (ObjectMessage) message;

            Long[] ids = (Long[]) obj.getObject();

            for (Long id : ids) {
                itemPageService.genItemHtml(id);
            }

            System.out.println(">>>>>>消息消费: 生成页面成功");

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
