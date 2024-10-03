package com.example.portfolio.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ThumbnailCreateDTO;
import com.example.portfolio.model.Category;
import com.example.portfolio.service.CategoryService;
import com.example.portfolio.service.ProjectService;
import com.example.portfolio.service.ThumbnailService;

@RestController
@RequestMapping("/api")
public class ProjectController {
	
	private CategoryService categoryService;
	private ProjectService projectService;
	private ThumbnailService thumbnailService;
	
	public ProjectController (CategoryService categoryService,ProjectService projectService, ThumbnailService thumbnailService) {
		this.categoryService = categoryService;
		this.projectService = projectService;
		this.thumbnailService = thumbnailService;
	}
	
	@GetMapping("/category")
	public List<Category> getCategory() {
		return categoryService.getCategory();
	}
	
	@PostMapping("/thumbnail")
	public void saveThumbnail(ThumbnailCreateDTO thumbnailCreateDTO) {
		MultipartFile image = thumbnailCreateDTO.getMultipartFile();
		thumbnailCreateDTO.setTimgoname(image.getOriginalFilename());
		thumbnailCreateDTO.setTimgtype(image.getContentType());
		try {
			thumbnailCreateDTO.setTimgdata(image.getBytes());
		} catch(IOException e) {
			e.printStackTrace();
		}
		thumbnailService.insertThumbnail(thumbnailCreateDTO);
	}

}
