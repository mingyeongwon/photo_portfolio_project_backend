package com.example.portfolio.dto;

import org.springframework.web.multipart.MultipartFile;

public class ProjectUpdateDto {
	private Long id;
	private String title;
	private Long categoryId;
	private Long subcategoryId;
		
	private MultipartFile thumbnailMultipartFile;
	private MultipartFile[] photoMultipartFiles;
	
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

	public MultipartFile getThumbnailMultipartFile() {
		return thumbnailMultipartFile;
	}
	public void setThumbnailMultipartFile(MultipartFile thumbnailMultipartFile) {
		this.thumbnailMultipartFile = thumbnailMultipartFile;
	}
	public MultipartFile[] getPhotoMultipartFiles() {
		return photoMultipartFiles;
	}
	public void setPhotoMultipartFiles(MultipartFile[] photoMultipartFiles) {
		this.photoMultipartFiles = photoMultipartFiles;
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