package com.impler.less;

import java.util.List;


public class LessConfiguration {
	
	public void setCodeces(List<LessCodec> codeces) {
		if(codeces!=null){
			for(LessCodec codec : codeces){
				LessCodecFactory.registLessCodec(codec);
			}
		}
	}
	
	public void setHandlers(List<LessHandler> handlers) {
		if(handlers!=null){
			for(LessHandler handler : handlers){
				LessHandlerFactory.registLessHandler(handler);
			}
		}
	}

}
