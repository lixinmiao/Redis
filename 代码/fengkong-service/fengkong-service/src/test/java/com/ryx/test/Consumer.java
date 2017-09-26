package com.ryx.test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Consumer {
	
	public static void main(String[] args) throws Exception {
		//ApplicationContext context=new ClassPathXmlApplicationContext("classpath:spring/applicationContext-amq.xml");
		//根据用户名，密码，url创建一个连接工厂
				ConnectionFactory cf = new ActiveMQConnectionFactory("ryx", "ryx", "tcp://172.20.1.205:61616");
				//从工厂中获取一个连接
				Connection connection = cf.createConnection();
				connection.start();
				//第一个参数:是否支持事务，如果为true，则会忽略第二个参数，被jms服务器设置为SESSION_TRANSACTED
				//第二个参数为false时，paramB的值可为Session.AUTO_ACKNOWLEDGE，Session.CLIENT_ACKNOWLEDGE，DUPS_OK_ACKNOWLEDGE其中一个。
				//Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常，也会被当作正常发送成功。
		        //Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功，并删除消息。
		        //DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。
				final Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);


				
				
				//=======================================================================
				//创建一个到达的目的地(队列),如果这个队列不存在，将会被创建
				//点对点模式 session.createQueue
				Destination destination = session.createQueue("ActiveMQ.DLQ");
				
				//发布订阅模式
				//Destination destination = session.createTopic("ryx.consumption.counting");
				//=======================================================================
				
				
				
				
				
				//创建一个消费者
				MessageConsumer consumer = session.createConsumer(destination);
				
				Message message = consumer.receive();
				while(message!=null) {
					String jmsType = message.getJMSType();
					System.out.println(jmsType);
					TextMessage txtMsg = (TextMessage)message;
					session.commit();
					System.out.println("收到消 息：" + txtMsg.getText());
					message = consumer.receive(1000L);
				}
				session.close();
				connection.close();
	}
	
	
	public static void hello() throws Exception {
		//ApplicationContext context=new ClassPathXmlApplicationContext("classpath:spring/applicationContext-amq.xml");
		//根据用户名，密码，url创建一个连接工厂
		ConnectionFactory cf = new ActiveMQConnectionFactory("ryx", "ryx", "tcp://172.20.1.205:61616");
		//从工厂中获取一个连接
		Connection connection = cf.createConnection();
		connection.start();
		//第一个参数:是否支持事务，如果为true，则会忽略第二个参数，被jms服务器设置为SESSION_TRANSACTED
		//第二个参数为false时，paramB的值可为Session.AUTO_ACKNOWLEDGE，Session.CLIENT_ACKNOWLEDGE，DUPS_OK_ACKNOWLEDGE其中一个。
		//Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常，也会被当作正常发送成功。
        //Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功，并删除消息。
        //DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。
		final Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);


		
		
		//=======================================================================
		//创建一个到达的目的地(队列),如果这个队列不存在，将会被创建
		//点对点模式 session.createQueue
		Destination destination = session.createQueue("ActiveMQ.DLQ");
		
		//发布订阅模式
		//Destination destination = session.createTopic("ryx.consumption.counting");
		//=======================================================================
		
		
		
		
		
		//创建一个消费者
		MessageConsumer consumer = session.createConsumer(destination);
		
		Message message = consumer.receive();
		while(message!=null) {
			TextMessage txtMsg = (TextMessage)message;
			session.commit();
			System.out.println("收到消 息：" + txtMsg.getText());
			message = consumer.receive(1000L);
		}
		session.close();
		connection.close();
	}

}
