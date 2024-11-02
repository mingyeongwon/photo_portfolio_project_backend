package com.example.portfolio.dto;

import java.util.List;

import org.springframework.data.domain.Slice;

public class ProjectDetailPageDto {
	
	private String title;
	private String thumbnailUrl;
	private Slice<PhotoListDto> photos;
	
	public ProjectDetailPageDto(String title, String thumbnailUrl, Slice<PhotoListDto> photos) {
		super();
		this.title = title;
		this.thumbnailUrl = thumbnailUrl;
		this.photos = photos;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Slice<PhotoListDto> getPhotos() {
		return photos;
	}

	public void setPhotos(Slice<PhotoListDto> photos) {
		this.photos = photos;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	
}
