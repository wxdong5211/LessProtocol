package com.impler.less;


public interface LessHandler {
	
	int getType();
	
	void doHandler(LessDataPacket command, LessDataPacket ret);

}
