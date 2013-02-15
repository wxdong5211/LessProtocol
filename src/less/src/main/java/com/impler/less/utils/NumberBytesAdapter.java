package com.impler.less.utils;

/**
 * 数字与字节转换 采用Big Endian
 * @author Invalid
 */
public abstract class NumberBytesAdapter {
	
	/**
	 * bytes to long
	 * @param bytes
	 * @param index
	 * @return
	 */
	public static long b2l(byte[] bytes, int index) {  
        return b2num(bytes, index, Prototype.Long);  
    }
	
	/**
	 * long to bytes
	 * @param l
	 * @return
	 */
	public static byte[] l2b(long l) {
		return num2b(l,Prototype.Long);
    }
	
	/**
	 * bytes to double
	 * @param bytes
	 * @param index
	 * @return
	 */
	public static double b2d(byte[] bytes, int index) {  
        return Double.longBitsToDouble(b2l(bytes, index));  
    }
	
	/**
	 * double to bytes
	 * @param d
	 * @return
	 */
	public static byte[] d2b(double d) {
		return l2b(Double.doubleToLongBits(d));
    }
	
	/**
	 * bytes to int
	 * @param bytes
	 * @param index
	 * @return
	 */
	public static int b2i(byte[] bytes, int index) {  
        return (int) b2num(bytes, index, Prototype.Int);  
    }
	
	/**
	 * int to bytes
	 * @param l
	 * @return
	 */
	public static byte[] i2b(int i) {
		return num2b(i,Prototype.Int);
    }
	
	
	/**
	 * bytes to float
	 * @param bytes
	 * @param index
	 * @return
	 */
	public static float b2f(byte[] bytes, int index) {  
        return Float.intBitsToFloat(b2i(bytes, index));  
    }
	
	/**
	 * float to bytes
	 * @param f
	 * @return
	 */
	public static byte[] f2b(float f) {
		return i2b(Float.floatToIntBits(f));
    }
	
	/**
	 * bytes to short
	 * @param bytes
	 * @param index
	 * @return
	 */
	public static short b2s(byte[] bytes, int index) {  
        return (short)b2num(bytes, index, Prototype.Short);  
    }
	
	/**
	 * short to bytes
	 * @param s
	 * @return
	 */
	public static byte[] s2b(short s) {
		return num2b(s,Prototype.Short);
    }
	
	/**
	 * bytes to char
	 * @param bytes
	 * @param index
	 * @return
	 */
	public static char b2c(byte[] bytes, int index) {  
        return (char)b2num(bytes, index, Prototype.Char);  
    }
	
	/**
	 * char to bytes
	 * @param c
	 * @return
	 */
	public static byte[] c2b(char c) {
		return num2b(c,Prototype.Char);
    }
	
	/**
	 * 原型转字节
	 * @param num
	 * @param type
	 * @return
	 */
	public static byte[] num2b(long num, Prototype type) {
		if(type==null)
			throw new IllegalArgumentException("type must not be null");
		int len = type.getLength();
		byte[] b = new byte[len];
		int temp = 0xff;
		for(int i=len-1;i>=0;i--){
			b[len-i-1]=(byte) ((num>>(i*8))&temp);
		}
		return b;
    }
	
	/**
	 * 字节转原型
	 * @param bytes
	 * @param index
	 * @param type
	 * @return
	 */
	public static long b2num(byte[] bytes, int index, Prototype type) {
		if(type==null)
			throw new IllegalArgumentException("type must not be null");
		if(bytes==null)
			throw new IllegalArgumentException("bytes must not be null");
		int len = type.getLength();
		if(bytes.length<index+len)
			throw new IllegalArgumentException();
		long num = 0;
		for(int i=0;i<len;i++){
			num|=((long)bytes[index+i])<<(len-i-1)*8;
		}
        return num;  
    }
	
	/**
	 * 支持的原型类型
	 * @author Invalid
	 *
	 */
	public static enum Prototype{
		Char(2),
		Short(2),
		Int(4),
		Float(4),
		Long(8),
		Double(8);
		private int length;
		private Prototype(int length){this.length = length;}
		public int getLength(){
			return length;
		}
	}
	
}
