package com.impler.less.mina;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import com.impler.less.LessDataPacket;

public class MinaMessageCodecFactory extends DemuxingProtocolCodecFactory{

	public MinaMessageCodecFactory (){
		super.addMessageDecoder(MinaMessageDecoder.class);
		super.addMessageEncoder(LessDataPacket.class, MinaMessageEncoder.class);
	}
}
