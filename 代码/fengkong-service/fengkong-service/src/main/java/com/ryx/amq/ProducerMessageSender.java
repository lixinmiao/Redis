package com.ryx.amq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class ProducerMessageSender {

	//@Autowired
	private JmsTemplate jmsTemplate;
	
	public void SendMessage(final Integer count){
		jmsTemplate.send(new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage("这是一首简单的小情歌"+"---->"+count);
			}
		});
	}
}
