package com.impler.less;

/**
 * 数据包
 * @author Invalid
 *
 */
public class LessDataPacket {
	
	private int version;
	private int attribute;
	private int length;
	private int type;
	private int encode;
	private int nextpos;
	
	private String content;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public int getAttribute() {
		return attribute;
	}
	
	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getEncode() {
		return encode;
	}

	public void setEncode(int encode) {
		this.encode = encode;
	}

	public int getNextpos() {
		return nextpos;
	}

	public void setNextpos(int nextpos) {
		this.nextpos = nextpos;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "LessDataPacket [version=" + version + ", attribute="
				+ attribute + ", length=" + length + ", type=" + type
				+ ", encode=" + encode + ", nextpos=" + nextpos + ", content="
				+ content + "]";
	}

}
