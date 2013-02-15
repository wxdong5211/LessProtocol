package com.impler.dynamicode;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * DynamicCodeGenerate
 * @author Invalid
 */
public class DynamicCode {
	
	/**周期时间 秒*/
	private static final long stepSecond = 30;
	/**令牌长度*/
	private static final int codeLength = 8;
	/**误差周期*/
	private static final int mistakeRange = 4;
	
	/**
	 * 验证提交的令牌
	 * @param key
	 * @param submitCode
	 * @return
	 */
	public static boolean validateCode(String key,String submitCode){
		if(submitCode==null || submitCode.length()==0)return false;
		int range = mistakeRange/2;
		int len = range*2+1;
		long now = System.currentTimeMillis();
		for(int i=0;i<len;i++){
			if(submitCode.equals(generateCode1(key,now,i-range)))
				return true;
		}
		return false;
	}

	/**
	 * 生成令牌
	 * @param key
	 * @return
	 */
	public static String generateCode(String key){
		return generateCode1(key,System.currentTimeMillis(),0);
	}
	
	private static String generateCode1(String key,long now,int offset){
		now/=1000;
		long time = now-now%stepSecond+offset*stepSecond;
		long day = now/60/60/24+365*(3000+1970);
		key+=time;
		ArrayList<Integer> strigrams = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8));
		for(int i=0;i<8;i++){
			key+=strigrams.remove(getTrigram(i,8,day));
		}
		String crypto = "";
		long tag = (time/stepSecond+day)%3;
		if(tag==0)crypto = "HmacSHA1";
		else if(tag==1)crypto = "HmacSHA256";
		else crypto = "HmacSHA512";
		return TOTP.generateTOTP(key, time+"", codeLength+"", crypto);
	}
	
	private static int getTrigram(int i,int max,long day){
		return (i==0&&max>0) ? (int) (day%max) : getTrigram(i-1,max-1,day/max);
	}
	
}
