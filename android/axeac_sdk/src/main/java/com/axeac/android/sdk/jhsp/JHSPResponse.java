package com.axeac.android.sdk.jhsp;

public class JHSPResponse {
	
	public static final String MEIP_CODE = "MEIP_CODE";
	public static final String MEIP_MESSAGE = "MEIP_MESSAGE";
	public static final String MEIP_SEQUENCE = "MEIP_SEQUENCE";
	public static final String MEIP_TIMES = "MEIP_TIMES";
	public static final String MEIP_USERNAME = "MEIP_USERNAME";
	public static final String MEIP_RETURN_TYPE = "MEIP_RETURN_TYPE";
	public static final String MEIP_RESOURCE_VER = "MEIP_RESOURCE_VER";
	public static final String MEIP_FORWARD = "MEIP_FORWARD";

	private int code;
	private String message;
	private String sequence;
	private int times;
	private String username; 
	private String data;
	private String returnType;
	private int resourceVer;
	private String forward;
	private String meip;
	
	public String getMeip() {
		return meip;
	}

	public void setMeip(String meip) {
		this.meip = meip;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public int getResourceVer() {
		return resourceVer;
	}

	public void setResourceVer(int resourceVer) {
		this.resourceVer = resourceVer;
	}

	public String getForward() {
		return forward;
	}

	public void setForward(String forward) {
		this.forward = forward;
	}
}