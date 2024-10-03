package com.example.portfolio.dto;

import org.springframework.web.multipart.MultipartFile;

public class ThumbnailCreateDTO {
	private Long projectId;
	private MultipartFile multipartFile;
	private String timgoname;
	private String timgtype;
	private byte[] timgdata;
	
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

	public byte[] getTimgdata() {
		return timgdata;
	}

	public void setTimgdata(byte[] timgdata) {
		this.timgdata = timgdata;
	}

	public ThumbnailCreateDTO() {
		
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
