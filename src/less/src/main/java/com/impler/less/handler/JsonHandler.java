package com.impler.less.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.impler.less.LessDataPacket;
import com.impler.less.LessHandler;
import com.impler.less.LessType;

public class JsonHandler implements LessHandler{
	
	private static final Logger log = LoggerFactory.getLogger(JsonHandler.class);
	private static final ConcurrentHashMap<String, JsonCommand> commands = new ConcurrentHashMap<String, JsonCommand>();
	
	public void setCommands(List<JsonCommand> commands){
		if(commands!=null){
			for(JsonCommand command : commands){
				JsonHandler.commands.put(command.getCommand(), command);
			}
		}
	}

	@Override
	public int getType() {
		return LessType.TEXT_JSON;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doHandler(LessDataPacket command, LessDataPacket ret) {
		if(log.isDebugEnabled())
			log.debug("JsonHandler:"+command);
		Map<String,Object> param = null;
		try{
			param = JSON.parseObject(command.getContent(),Map.class);
		}catch(JSONException e){
			log.error(""+command,e);
		}
		if(param==null)
			ret.setContent("{}");
		else{ 
			Map<String,Object> retm = new HashMap<String,Object>();
			retm.put("r", param.get("r"));
			JsonCommand jcommand = commands.get(param.get("c"));
			if(jcommand!=null)
				jcommand.doCommand(param, retm);
			ret.setContent(JSON.toJSONString(retm));
		}
	}

}
