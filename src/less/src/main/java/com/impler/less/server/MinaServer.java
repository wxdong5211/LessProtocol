package com.impler.less.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impler.less.LessServer;
import com.impler.less.mina.MinaMessageCodecFactory;
import com.impler.less.mina.MinaServerSessionHandler;

public class MinaServer implements LessServer{
	
	private static final Logger log = LoggerFactory.getLogger(MinaServer.class);
	
	private NioSocketAcceptor acceptor;
    private int port;
    
    public MinaServer(){
    	this.port = LessServer.DEFAULT_PORT;
	}
    
    public MinaServer(int port){
    	this.port = port < 0 ? LessServer.DEFAULT_PORT : port;
    }
    
	@Override
	public void start() throws IOException {
		acceptor = new NioSocketAcceptor();
		//set self codecFactory
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaMessageCodecFactory ()));
//		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		//set self handler
		acceptor.setHandler(new MinaServerSessionHandler());
		acceptor.bind(new InetSocketAddress(port));
		log.info("mina server started,Listening on ["+port+"]......");
	}
	
	@Override
	public void stop() throws IOException {
		acceptor.dispose();
		log.info("mina server stoped,Listening on ["+port+"].");
	}

}
