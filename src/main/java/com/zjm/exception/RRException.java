package com.zjm.exception;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
public class RRException  extends RuntimeException{
	private static final long serialVersionUID = 1L;
	private String msg;
	private int code = 500000;

	public RRException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public RRException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}

	public RRException(int code, String msg) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}

	public RRException(int code, String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
