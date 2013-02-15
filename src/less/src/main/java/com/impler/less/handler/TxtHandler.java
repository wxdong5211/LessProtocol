package com.impler.less.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impler.less.LessDataPacket;
import com.impler.less.LessHandler;
import com.impler.less.LessType;

public class TxtHandler implements LessHandler{
	
	private static final Logger log = LoggerFactory.getLogger(TxtHandler.class);

	@Override
	public int getType() {
		return LessType.TEXT_TXT;
	}

	@Override
	public void doHandler(LessDataPacket command, LessDataPacket ret) {
		log.info(""+command);
		ret.setContent(this+"haha哈");
	}

}
