package com.ryx;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

public class TopicMessageListener implements MessageListener {  
  
    private RedisTemplate redisTemplate;  
      
    public void setRedisTemplate(RedisTemplate redisTemplate) {  
        this.redisTemplate = redisTemplate;  
    }  
  
  

	@Override
	public void onMessage(Message message, byte[] arg1) {
		// TODO Auto-generated method stub
		 byte[] body = message.getBody();//请使用valueSerializer  
	        byte[] channel = message.getChannel();  
	        //请参考配置文件，本例中key，value的序列化方式均为string。  
	        //其中key必须为stringSerializer。和redisTemplate.convertAndSend对应  
	        String itemValue = new String(body);
	        String topic =  new String(channel);
	        System.out.println(itemValue);
	        System.out.println(topic);
	}  
}  