package com.impler.less.handler;

import java.util.Map;

public interface JsonCommand {

	String getCommand();
	
	void doCommand(Map<String,Object> param, Map<String,Object> ret);
	
}
