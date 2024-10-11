package com.example.portfolio.exception;

public enum ErrorCode {
	// 400
	UNKNOWN("UNKNWON", "알수 없는 에러가 발생했습니다."),
	NOT_FIND_PROJECT("NOT_FIND_PROJECT", "프로젝트를 찾지 못했습니다.");
	
	private ErrorCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	private final String code;
	private final String msg;
	
	public String getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
}
