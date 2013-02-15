package com.impler.less.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impler.less.LessCodec;
import com.impler.less.LessCodecFactory;
import com.impler.less.LessDataPacket;
import com.impler.less.LessHandler;
import com.impler.less.LessHandlerFactory;
import com.impler.less.LessServer;

public class NIOServer implements LessServer {
	
	private static final Logger log = LoggerFactory.getLogger(NIOServer.class);
	
	private Selector selector;  
	private final ExecutorService pool = Executors.newFixedThreadPool(10);
	private volatile boolean tag;
	private int port;
	
	public NIOServer() {
		this.port = LessServer.DEFAULT_PORT;
	}
	
	public NIOServer(int port) {
		this.port = port < 0 ? LessServer.DEFAULT_PORT : port;
	}
	
	@Override
	public void start() throws IOException{
		selector = Selector.open();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();  
		serverSocketChannel.socket().bind(new InetSocketAddress(port)); // port  
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);// register 
		tag = true;
		new Thread(new SRun()).start();
		log.info("nio server started,Listening on ["+port+"]......");
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
		selector.close();
		log.info("nio server stoped,Listening on ["+port+"].");
	}
	
	public void process(SelectionKey selectionKey){
		log.info(this+"in:"+selectionKey.readyOps());
		switch (selectionKey.readyOps()){
			case SelectionKey.OP_ACCEPT:
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();  
				try {
					SocketChannel socketChannel = serverSocketChannel.accept();
					log.info(""+socketChannel);
					if(socketChannel!=null){
						socketChannel.configureBlocking(false);
						// 立即注册一个 OP_READ 的SelectionKey, 接收客户端的消息  
						socketChannel.register(selector, SelectionKey.OP_READ); 
						log.info(this+" OP_ACCEPT:"+socketChannel.socket().getInetAddress().getHostAddress());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}  
				break;
			case SelectionKey.OP_READ:
				// 有消息进来  
				ByteBuffer byteBuffer = ByteBuffer.allocate(100);  
				SocketChannel socketChannel = (SocketChannel) selectionKey.channel();  
				log.info(this+" host:"+socketChannel);
				try {  
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
				break;
			default:
				log.error("unhandled " + selectionKey.readyOps());
	            break;
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
			log.error("nio decodable checkMagic fail:"+in);
			return false;
		}
		int version = in.get();
		LessCodec codec = LessCodecFactory.getLessCodec(version);
		if(codec != null){
			LessDataPacket tMsg = new LessDataPacket();
			int state = codec.decode(in, tMsg);
			if(state==LessCodec.NEED_DATA)
				return false;
			log.info("nio:"+tMsg);
			LessDataPacket ret = new LessDataPacket();
            ret.setVersion(codec.getVersion());
            ret.setType(tMsg.getType());
            ret.setEncode(tMsg.getEncode());
            LessHandler hander = LessHandlerFactory.getLessHandler(tMsg.getType());
            hander.doHandler(tMsg, ret);
	    	socketChannel.write(codec.encode(ret));
			return true;
		}
		log.error("nio decodable no codec:"+in);
		return false;
	}
	
	public class SRun implements Runnable{
		
		@Override
		public void run() {
			while(tag){
				try {
					if(selector.select(1000)>0){
						Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
						while (iterator.hasNext()) {  
						    SelectionKey selectionKey = iterator.next();  
						    iterator.remove(); // 删除此消息  
						    // 并在当前线程内处理。（为了高效，一般会在另一个线程中处理此消息，例如使用线程池等）  
						    process(selectionKey);
//						    pool.execute(new CRun(selectionKey));  
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public class CRun implements Runnable{
		
		private SelectionKey selectionKey;
		
		public CRun(SelectionKey selectionKey) {
			this.selectionKey = selectionKey;
		}

		@Override
		public void run() {
			process(selectionKey);
		}
		
	}
	
}
