package com.impler.less;

/**
 * 客户端
 * @author Invalid
 *
 */
public interface LessClient {
	
	LessDataPacket send(LessDataPacket command) throws Exception;

}
