package com.impler.less.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impler.less.LessClient;
import com.impler.less.LessCodec;
import com.impler.less.LessCodecFactory;
import com.impler.less.LessDataPacket;

public class SimpleClient implements LessClient{
	
	private static final Logger log = LoggerFactory.getLogger(SimpleClient.class);
	
	private SocketChannel socketChannel;
	private String host;
	private int port;
	
	public SimpleClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public LessDataPacket send(LessDataPacket command) throws Exception{
		if(command==null)
			throw new IllegalArgumentException("command must not be null");
		LessCodec codec = LessCodecFactory.getLessCodec(command.getVersion());
		if(codec==null)
			throw new IllegalArgumentException("command version not support");
		socketChannel =  SocketChannel.open(new InetSocketAddress(host, port));
		ByteBuffer byteBuffer = codec.encode(command);
    	socketChannel.write(byteBuffer);
    	byteBuffer.clear();
    	socketChannel.read(byteBuffer);
		byteBuffer.flip();
		LessDataPacket ret = decode(byteBuffer);
    	socketChannel.close();
		return ret;
	}
	
	private static LessDataPacket decode(ByteBuffer in){
		LessDataPacket ret = new LessDataPacket();
		while (in.hasRemaining()) {  
			try {
				int state = decode1(in,ret);
				if(LessCodec.OK == state){
					return ret;
				}else if(LessCodec.NOT_OK == state){
					return null;
				}else if(LessCodec.NEED_DATA == state && in.remaining() < 3){
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
		return null;
	}
	
	private static int decode1(ByteBuffer in,LessDataPacket ret) throws Exception{
		if(in.remaining() < 3)
			return LessCodec.NEED_DATA;
		int magic = in.getShort();
		if(!LessCodecFactory.checkMagic(magic)){
			log.error("simple client decodable checkMagic fail:"+in);
			return LessCodec.NEED_DATA;
		}
		int version = in.get();
		LessCodec codec = LessCodecFactory.getLessCodec(version);
		if(codec != null){
			int state = codec.decode(in, ret);
			log.info("simple client:"+ret);
			return state;
		}
		log.error("simple client decodable no codec:"+in);
		return LessCodec.NOT_OK;
	}

}
