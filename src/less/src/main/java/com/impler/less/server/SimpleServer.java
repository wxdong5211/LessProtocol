package com.impler.less.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impler.less.LessCodec;
import com.impler.less.LessCodecFactory;
import com.impler.less.LessDataPacket;
import com.impler.less.LessHandlerFactory;
import com.impler.less.LessServer;
import com.impler.less.LessHandler;

/**
 * 阻塞式服务
 * @author Invalid
 *
 */
public class SimpleServer implements LessServer{
	
	private static final Logger log = LoggerFactory.getLogger(SimpleServer.class);
	
	private ServerSocketChannel serverSocketChannel;
	private final ExecutorService pool = Executors.newFixedThreadPool(10);
	private volatile boolean tag;
	private int port;
	
	public SimpleServer() {
		this.port = LessServer.DEFAULT_PORT;
	}
	
	public SimpleServer(int port) {
		this.port = port < 0 ? LessServer.DEFAULT_PORT : port;
	}
	
	@Override
	public void start() throws IOException{
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(port)); // port
		tag = true;
		new Thread(new SRun()).start();
		log.info("simple server started,Listening on ["+port+"]......");
	}
	
	@Override
	public void stop() throws IOException{
		tag=false;
		pool.shutdown();
		try{
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {  
			      pool.shutdownNow(); // Cancel currently executing tasks  
			  // Wait a while for tasks to respond to being cancelled  
			  if (!pool.awaitTermination(60, TimeUnit.SECONDS))  
				  log.error("Pool did not terminate");  
			}
		}catch(InterruptedException e){
			 pool.shutdownNow();
		}
		log.info("simple server stoped,Listening on ["+port+"].");
	}
	
	public void process(SocketChannel socketChannel){
		ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
		try {
			log.info("simple server:"+socketChannel);
//			socketChannel.configureBlocking(false);
			socketChannel.read(byteBuffer);
			byteBuffer.flip();
		    while (byteBuffer.hasRemaining()) {  
		    	decode(byteBuffer,socketChannel);
			    byteBuffer.clear();
		    	socketChannel.read(byteBuffer);
		    	byteBuffer.flip();
		    }
		    
	    	// 输入结束，关闭 socketChannel  
	    	socketChannel.close();
		} catch (Exception e) {  
			// 如果read抛出异常，表示连接异常中断，需要关闭 socketChannel  
		    e.printStackTrace();  
		    try {
				socketChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}  
		}
	}
	
	private void decode(ByteBuffer in, SocketChannel socketChannel){
		while (in.hasRemaining()) {  
			try {
				if(!decode1(in,socketChannel))
					break;
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
	
	private boolean decode1(ByteBuffer in, SocketChannel socketChannel) throws Exception{
		if(in.remaining() < 3)
			return false;
		int magic = in.getShort();
		if(!LessCodecFactory.checkMagic(magic)){
			log.error("simple decodable checkMagic fail:"+in);
			return false;
		}
		int version = in.get();
		LessCodec codec = LessCodecFactory.getLessCodec(version);
		if(codec != null){
			LessDataPacket tMsg = new LessDataPacket();
			int state = codec.decode(in, tMsg);
			if(state==LessCodec.NEED_DATA)
				return false;
			LessDataPacket ret = new LessDataPacket();
            ret.setVersion(codec.getVersion());
            ret.setType(tMsg.getType());
            ret.setEncode(tMsg.getEncode());
            LessHandler hander = LessHandlerFactory.getLessHandler(tMsg.getType());
            hander.doHandler(tMsg, ret);
	    	socketChannel.write(codec.encode(ret));
			return true;
		}
		log.error("simple decodable no codec:"+in);
		return false;
	}
	
	public class SRun implements Runnable{
		
		@Override
		public void run() {
			while(tag){
				try {
					if(serverSocketChannel.isOpen())
						pool.execute(new CRun(serverSocketChannel.accept()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(serverSocketChannel.isOpen())
				try {
					serverSocketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
	}
	
	public class CRun implements Runnable{
		
		private SocketChannel socketChannel;
		
		public CRun(SocketChannel socketChannel) {
			this.socketChannel = socketChannel;
		}

		@Override
		public void run() {
			process(socketChannel);
		}
		
	}
	
}
