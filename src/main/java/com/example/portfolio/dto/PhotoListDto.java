package com.example.portfolio.dto;

public class PhotoListDto {
	private Long id;
	private String imageUrl; 
	private String thumbnailUrl;
	private String title;
	
	public PhotoListDto() {}

	public PhotoListDto(Long id, String imageUrl, String thumbnailUrl, String title) {
		super();
		this.id = id;
		this.imageUrl = imageUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.title = title;
	}


	public PhotoListDto(Long id, String imageUrl) {
		super();
		this.id = id;
		this.imageUrl = imageUrl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String  imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
