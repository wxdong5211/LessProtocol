package com.impler.less.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.impler.less.LessCodec;
import com.impler.less.LessCodecFactory;
import com.impler.less.LessDataPacket;

public class MinaMessageDecoder implements MessageDecoder{
	
	private static final Logger log = LoggerFactory.getLogger(MinaMessageDecoder.class);
	
	private LessCodec codec;

	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		//3=2字节的Magic+1字节的version
		if(in.remaining() < 3)
			return MessageDecoderResult.NEED_DATA;
		//get magic
		int magic = in.getShort();
		if(!LessCodecFactory.checkMagic(magic)){
			log.error("mina decodable checkMagic fail:"+in);
			return MessageDecoderResult.NOT_OK;
		}
		//get version
		int version = in.get();
		codec = LessCodecFactory.getLessCodec(version);
		if(codec != null)
			return MessageDecoderResult.OK;
		log.error("mina decodable no codec:"+in);
		return MessageDecoderResult.NOT_OK;
	}
	    //真正解析socket数据
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
				ProtocolDecoderOutput out) throws Exception {
		in.position(in.position()+3);
		LessDataPacket tMsg = new LessDataPacket();
		int state = codec.decode(in.buf(), tMsg);
		if(state==LessCodec.NEED_DATA)
			return MessageDecoderResult.NEED_DATA;
		out.write(tMsg);
		return MessageDecoderResult.OK;
	}

	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
	}
	
}
