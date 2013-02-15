package com.impler.less.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.impler.less.LessCodec;
import com.impler.less.LessCodecFactory;
import com.impler.less.LessDataPacket;
import com.impler.less.exception.UnsupportLessVersionException;

public class MinaMessageEncoder implements MessageEncoder<LessDataPacket>{

	@Override
	public void encode(IoSession session, LessDataPacket message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer encodedMessage = IoBuffer.allocate(4096).setAutoExpand(true);
		int version = message.getVersion();
		LessCodec codec = LessCodecFactory.getLessCodec(version);
		if(codec == null) 
			throw new UnsupportLessVersionException("encode version:"+version);
		encodedMessage.put(codec.encode(message));
		encodedMessage.flip();
		out.write(encodedMessage );
	}

}
