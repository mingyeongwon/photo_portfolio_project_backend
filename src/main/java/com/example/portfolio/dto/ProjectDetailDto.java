package com.example.portfolio.dto;

import java.util.List;

public class ProjectDetailDto {
	private Long id;
	private String title;
	private String imageUrl; // 썸네일 URL
	private Long categoryId;
	private Long subCategoryId;
	private List<PhotoListDto> photos;

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


	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getSubCategoryId() {
		return subCategoryId;
	}


	public void setSubCategoryId(Long subCategoryId) {
		this.subCategoryId = subCategoryId;
	}
	
    public List<PhotoListDto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoListDto> photos) {
        this.photos = photos;
    }
	
}
