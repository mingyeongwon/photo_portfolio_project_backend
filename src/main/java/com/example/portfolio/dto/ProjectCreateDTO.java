package com.example.portfolio.dto;

import org.springframework.web.multipart.MultipartFile;

public class ProjectCreateDTO {
	private Long id;
	private String title;
	private Long categoryId;
	private Long subcategoryId;
	
	private MultipartFile[] multipartFiles;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public MultipartFile[] getMultipartFiles() {
		return multipartFiles;
	}
	public void setMultipartFiles(MultipartFile[] multipartFiles) {
		this.multipartFiles = multipartFiles;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public Long getSubcategoryId() {
		return subcategoryId;
	}
	public void setSubcategoryId(Long subcategoryId) {
		this.subcategoryId = subcategoryId;
	}
}
