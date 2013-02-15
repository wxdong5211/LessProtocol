package com.impler.less;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impler.dynamicode.DynamicCode;
import com.impler.less.client.SimpleClient;
import com.impler.less.server.MinaServer;
import com.impler.less.server.NIOServer;
import com.impler.less.server.SimpleServer;

public class TestServer {
	
	private static final Logger log = LoggerFactory.getLogger(TestServer.class);
	
//	private static final LessServer server1 = new SimpleServer();
//	private static final LessServer server2 = new NIOServer(10087);
//	private static final LessServer server3 = new MinaServer(10088);
	
	@BeforeClass
	public static void before() throws Exception{
//		server1.start();
//		server2.start();
//		server3.start();
	}
	
	@AfterClass
	public static void after() throws Exception{
//		server1.stop();
//		server2.stop();
//		server3.stop();
	}
	
	@Test 
	public void testToken() throws Exception{
		String code = DynamicCode.generateCode("1");
		System.out.println(code);
		System.out.println(DynamicCode.validateCode("1",code));
		System.out.println(System.currentTimeMillis());
		System.out.println((System.currentTimeMillis()+"").length());
	}
	
	@Test @Ignore
	public void testSimpleServer() throws Exception{
		for(int i=0;i<1;i++){
			client1("127.0.0.1", 10086);
		}
	}
	
	@Test @Ignore
	public void testNIOServer() throws Exception{
		for(int i=0;i<1;i++){
			client1("127.0.0.1", 10087);
		}
	}
	
	@Test @Ignore
	public void testMinaServer() throws Exception{
		for(int i=0;i<1;i++){
			client1("127.0.0.1", 10088);
		}
	}
	
	public static void client1(String host, int port) throws Exception{
		LessClient s = new SimpleClient(host,port);
		LessDataPacket command = new LessDataPacket();
//		String r = "im 客户"+Math.random();
		String r = "{\"c\":\"0\",\"r\":\"1\",\"e\":\"13012345678\",\"p0\":\"2"+"im 客户"+Math.random()+"\"}";
    	log.info("client string:"+r);
    	command.setContent(r);
    	command.setType(LessType.TEXT_JSON);
    	command.setEncode(LessEncode.UTF8);
		log.info("client:"+s.send(command));
	}
	
}
