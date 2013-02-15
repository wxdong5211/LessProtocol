package com.impler.less;

import java.nio.ByteBuffer;

/**
 * 数据编码解码器
 * @author Invalid
 *
 */
public interface LessCodec {
	
	int MAGIC = 0x1E55;
	String NAME = "LESS";
	
	int OK 			= 0x0;
	int NOT_OK 		= 0x1;
	int NEED_DATA 	= 0x2;
	
	int getVersion();
	
	ByteBuffer encode(LessDataPacket packet) throws Exception;
	
	int decode(ByteBuffer in, LessDataPacket packet) throws Exception;

}
