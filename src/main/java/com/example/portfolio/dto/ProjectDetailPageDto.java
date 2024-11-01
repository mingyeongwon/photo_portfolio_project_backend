package com.example.portfolio.dto;

import java.util.List;

public class ProjectDetailPageDto {
	
	private String title;
	private List<PhotoListDto> photos;
	private boolean last;
	
	public ProjectDetailPageDto(String title, List<PhotoListDto> photos, boolean last) {
		super();
		this.title = title;
		this.photos = photos;
		this.last = last;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<PhotoListDto> getPhotos() {
		return photos;
	}

	public void setPhotos(List<PhotoListDto> photos) {
		this.photos = photos;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}
	
}
