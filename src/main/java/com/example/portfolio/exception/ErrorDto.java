package com.example.portfolio.exception;

import org.springframework.http.ResponseEntity;

public class ErrorDto {

	private String code;
    private String msg;
    private String detail;
    
	public ErrorDto(String code, String msg, String detail) {
		super();
		this.code = code;
		this.msg = msg;
		this.detail = detail;
	}
	
	public ErrorDto(Builder builder) {
		this.code = builder.code;
		this.msg = builder.msg;
		this.detail = builder.detail;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	
	public static ResponseEntity<ErrorDto> toResponseEntity(CustomException customException) {
        ErrorCode errorType = customException.getErrorCode();
        String detail = customException.getDetail();
 
        return ResponseEntity
            .status(customException.getStatus())
            .body(ErrorDto.builder()
                .code(errorType.getCode())
                .msg(errorType.getMsg())
                .detail(detail)
                .build());
    }

	// @Builder 어노테이션 대신 직접 생성
	public static Builder builder() {
		return new Builder();
	}
	
	 // Static inner Builder class
    public static class Builder {
        private String code;
        private String msg;
        private String detail;

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public ErrorDto build() {
            return new ErrorDto(this);
        }
    }
}
