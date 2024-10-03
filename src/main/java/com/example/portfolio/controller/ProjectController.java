package com.example.portfolio.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.CategoryDto;
import com.example.portfolio.dto.ThumbnailCreateDTO;
import com.example.portfolio.model.Category;
import com.example.portfolio.service.CategoryService;
import com.example.portfolio.service.ProjectService;
import com.example.portfolio.service.ThumbnailService;

@RestController
@RequestMapping("/api")
public class ProjectController {

	@Autowired
	private CategoryService categoryService;
	private ProjectService projectService;
	private ThumbnailService thumbnailService;
	
	public ProjectController (CategoryService categoryService,ProjectService projectService, ThumbnailService thumbnailService) {
		this.categoryService = categoryService;
		this.projectService = projectService;
		this.thumbnailService = thumbnailService;
	}
	
	//카테고리 페이지 데이터 전달
//	@GetMapping("/category")
//	public List<Category> getAllCategories() {
//		return categoryService.getAllCategories();
//	}
	
	//카테고리 생성
    
    // 카테고리 전체 목록 가져오기
    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }
    
    // 카테고리 생성

    @PostMapping("/categories")
    public void createCategories(@RequestBody List<CategoryDto> categoryDtos) {
        categoryService.createCategories(categoryDtos);
    }
    
    // 카테고리 수정
    @PutMapping("/categories")
    public void updateCategories(@RequestBody List<CategoryDto> categoryDtos) {
        categoryService.updateCategories(categoryDtos);
    }
    
    // 카테고리 삭제
    @DeleteMapping("/categories")
    public void deleteCategories(@RequestBody List<CategoryDto> categoryDtos) {
        categoryService.deleteCategories(categoryDtos);
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
