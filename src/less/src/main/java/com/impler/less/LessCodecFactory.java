package com.impler.less;

import java.util.concurrent.ConcurrentHashMap;

import com.impler.less.codec.LessCodecVer0;

/**
 * 编码解码器工厂
 * @author Invalid
 *
 */
public abstract class LessCodecFactory {
	
	private static final ConcurrentHashMap<String, LessCodec> codeces = new ConcurrentHashMap<String, LessCodec>();
	
	static {
		registLessCodec(new LessCodecVer0());
	}
	
	public static boolean checkMagic(int magic){
		return LessCodec.MAGIC == magic;
	}
	
	public static LessCodec getLessCodec(int version){
		return codeces.get(LessCodec.NAME+version);
	}
	
	public static void registLessCodec(LessCodec codec){
		if(codec!=null)
			codeces.putIfAbsent(LessCodec.NAME+codec.getVersion(), codec);
	}
	
	public static void unregistLessCodec(LessCodec codec){
		if(codec!=null)
			unregistLessCodec(codec.getVersion());
	}
	
	public static void unregistLessCodec(int version){
		codeces.remove(LessCodec.NAME+version);
	}

}
