package com.impler.less;

import java.io.IOException;

/**
 * less协议的socket server 
 * @author Invalid
 *
 */
public interface LessServer {
	
	int DEFAULT_PORT = 10086;
	
	int DEFAULT_BLOCK_SIZE = 4096;
	
	void start() throws IOException;
	 
	void stop() throws IOException;

}
