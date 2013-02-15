package com.impler.less.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impler.less.LessDataPacket;
import com.impler.less.LessHandler;
import com.impler.less.LessHandlerFactory;
import com.impler.less.LessServer;

public class MinaServerSessionHandler extends IoHandlerAdapter {

	private static final Logger log = LoggerFactory.getLogger(MinaServerSessionHandler.class);
    private static final int IDLE = 30;
    
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        session.close(true);
        log.error(session+" EXCEPTION:"+cause);
    }

    public void messageReceived(IoSession session, Object message)
            throws Exception {
        if(message instanceof LessDataPacket){
        	LessDataPacket msg = (LessDataPacket )message;
            //在此得到的msg就是通过socket接收到的且已经安装协议解析好的对象了。在此仅打印出而已
        	log.info(session+" RECEIVED:"+msg);
            LessDataPacket ret = new LessDataPacket();
            ret.setVersion(msg.getVersion());
            ret.setType(msg.getType());
            ret.setEncode(msg.getEncode());
            LessHandler hander = LessHandlerFactory.getLessHandler(msg.getType());
            hander.doHandler(msg, ret);
            session.write(ret);
        }
    }

    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        log.info(session+" SENT:"+message);
    }

    public void sessionCreated(IoSession session) throws Exception {
        log.info(session+" CONNECTED.");
    }

    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        log.info(session+" IDLE DISCONNECTING......");
        session.close(true);
        log.info(session+" DISCONNECTED.");
    }

    public void sessionOpened(IoSession session) throws Exception {
        //设置socket空闲时间
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE);
        session.getConfig().setReadBufferSize(LessServer.DEFAULT_BLOCK_SIZE);
        //其他配置也在此设置
        log.info(session+" OPENED");
    }
}
