package com.impler.less.codec;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impler.less.LessCodec;
import com.impler.less.LessDataPacket;
import com.impler.less.LessEncode;

public class LessCodecVer0 implements LessCodec{
	
	private static final Logger log = LoggerFactory.getLogger(LessCodecVer0.class);
	
	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public ByteBuffer encode(LessDataPacket packet) throws Exception {
		ByteBuffer encodedMessage = ByteBuffer.allocate(4096);
		encodedMessage.putShort((short) LessCodec.MAGIC);
		encodedMessage.put(2,(byte) getVersion());
		encodedMessage.put(3,(byte)packet.getAttribute());
		int encode = packet.getEncode();
		Charset charset = getCharset(encode);
        CharsetEncoder charsetEncoder = charset.newEncoder();
		ByteBuffer rb = charsetEncoder.encode(CharBuffer.wrap(packet.getContent()));
    	int rlen = rb.limit();
		encodedMessage.putInt(4,rlen);
		encodedMessage.put(8,(byte) packet.getType());
		encodedMessage.put(9,(byte) encode);
		encodedMessage.putInt(10,0);
		encodedMessage.position(14);
		encodedMessage.put(rb);
		encodedMessage.flip();
		return encodedMessage;
	}

	@Override
	public int decode(ByteBuffer in, LessDataPacket packet) throws Exception {
		packet.setVersion(getVersion());
		if(in.remaining() < 5)
    		return NEED_DATA;
		int attribute = in.get();
    	int len = in.getInt();
    	if(in.remaining() < len+6)
    		return NEED_DATA;
    	int type = in.get();
    	int encode = in.get();
    	int subseq = in.getInt();
    	if(log.isDebugEnabled())
    		log.debug("LessCodecVer0 decode length:"+len);
    	int oldpos = in.position();
    	int oldlimit = in.limit();
    	in.limit(oldpos+len);
    	
    	Charset charset = getCharset(encode);
        CharsetDecoder decoder = charset.newDecoder();
        String msgBody = decoder.decode(in).toString();//in.getString(len,decoder);
        in.limit(oldlimit);
        packet.setAttribute(attribute);
        packet.setLength(len);
        packet.setType(type);
        packet.setEncode(encode);
        packet.setNextpos(subseq);
        packet.setContent(msgBody);
		return OK;
	}
	
	private static Charset getCharset(int encode){
		Charset charset = null;
		if(LessEncode.UTF8 == encode){
			charset = Charset.forName("UTF-8");
		} else if(LessEncode.GBK == encode){
			charset = Charset.forName("GBK");
		} else {
			charset = Charset.defaultCharset();
		}
		return charset;
	}

}
