package com.example.portfolio.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ProjectListDto {
	private Long id;
	private String title;
	private String imageUrl; // 썸네일 URL
	
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul") //날짜 포멧 바꾸기
	private Date createdAt;
	
	private int view;
	private String categoryName;
	private String subCategoryName;
	private Long imageCount;


	public ProjectListDto(Long id, String title, String imageUrl, Date createdAt, int view, String categoryName, String subCategoryName, Long imageCount) {
		this.id = id;
		this.title = title;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
		this.view = view;
		this.categoryName = categoryName;
		this.imageCount = imageCount;
		this.subCategoryName = subCategoryName;
	}


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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public int getView() {
		return view;
	}

	public void setView(int view) {
		this.view = view;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Long getImageCount() {
		return imageCount;
	}

	public void setImageCount(Long imageCount) {
		this.imageCount = imageCount;
	}


	public String getSubCategoryName() {
		return subCategoryName;
	}


	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}


	
}
