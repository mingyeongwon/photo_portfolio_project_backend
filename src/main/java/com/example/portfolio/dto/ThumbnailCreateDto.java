package com.example.portfolio.dto;

import org.springframework.web.multipart.MultipartFile;

public class ThumbnailCreateDto {
	private Long id;
	private Long projectId;
	private MultipartFile multipartFile;
	private String timgoname;
	private String timgtype;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTimgoname() {
		return timgoname;
	}

	public void setTimgoname(String timgoname) {
		this.timgoname = timgoname;
	}

	public String getTimgtype() {
		return timgtype;
	}

	public void setTimgtype(String timgtype) {
		this.timgtype = timgtype;
	}

	public ThumbnailCreateDto() {
		
	}

	public MultipartFile getMultipartFile() {
		return multipartFile;
	}

	public void setMultipartFile(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

}
