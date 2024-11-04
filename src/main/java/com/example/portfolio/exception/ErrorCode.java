package com.example.portfolio.exception;

public enum ErrorCode {
	// 520 처리되지 않은 에러
	UNKNOWN("UNKNWON", "An unknown error occurred"),
	
	// 409
	CATEGORY_IN_USE("CATEGORY_IN_USE", "Category cannot be deleted because it is being used in projects"),
	SUBCATEGORY_IN_USE("SUBCATEGORY_IN_USE", "Subcategory cannot be deleted because it is being used in projects"),
	
	// 404
	NOT_FIND_PROJECT("NOT_FIND_PROJECT", "Project not found"),
	CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "Category not found"),
	SUBCATEGORY_NOT_FOUND("SUBCATEGORY_NOT_FOUND", "Subcategory not found"),
	STORAGE_FILE_NOT_FOUND("STORAGE_FILE_NOT_FOUND", "File not found in storage"),
	STORAGE_KEY_FILE_NOT_FOUND("STORAGE_KEY_FILE_NOT_FOUND", "Storage key file not found"),
	STORAGE_BATCH_DELETE_ERROR("STORAGE_BATCH_DELETE_ERROR", "Failed to delete multiple files from storage"),
   
	// 500 Internal Server Error
    STORAGE_IO_ERROR("STORAGE_IO_ERROR", "Storage I/O operation failed"),
    DATABASE_ERROR("DATABASE_ERROR", "Database operation failed");

	
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
