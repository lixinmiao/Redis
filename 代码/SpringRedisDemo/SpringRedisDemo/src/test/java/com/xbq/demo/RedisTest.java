package com.xbq.demo;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.xbq.demo.util.RedisCacheUtil;

/**
 * 测试
 */
public class RedisTest {

	private RedisCacheUtil redisCache;
	private static String key;
	private static String field;
	private static String value;
	
	@Before
	public void setUp() throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		context.start();
		redisCache = (RedisCacheUtil) context.getBean("redisCache");
	}

	// 初始化 数据
	static {
		key = "tb_student";
		field = "stu_name";
		value = "一系列的关于student的信息！";
	}
	
	// 测试增加数据
	@Test
	public void testHset() {
		redisCache.hset(key, field, value);
		System.out.println("数据保存成功！");
	}

	// 测试查询数据
	@Test
	public void testHget(){
		String re = redisCache.hget(key, field);
		System.out.println("得到的数据：" + re);
	}
	
	// 测试数据的数量
	@Test
	public void testHsize(){
		long size = redisCache.hsize(key);
		System.out.println("数量为：" + size);
	}
}
