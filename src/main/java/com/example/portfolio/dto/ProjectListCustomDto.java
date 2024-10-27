package com.example.portfolio.dto;

import java.util.List;

public class ProjectListCustomDto {
	private List<ProjectListDto> content;
	private int totalPages;
	
	public List<ProjectListDto> getContent() {
		return content;
	}
	public void setContent(List<ProjectListDto> content) {
		this.content = content;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	

	
}
