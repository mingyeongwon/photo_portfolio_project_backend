package com.example.portfolio.controller;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.CategoryDto;
import com.example.portfolio.dto.ThumbnailCreateDTO;
import com.example.portfolio.model.Admin;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.Photo;
import com.example.portfolio.model.Thumbnail;
import com.example.portfolio.service.AdminService;
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
	private AdminService adminService;
	
	public ProjectController (CategoryService categoryService,ProjectService projectService, ThumbnailService thumbnailService, AdminService adminService) {
		this.categoryService = categoryService;
		this.projectService = projectService;
		this.thumbnailService = thumbnailService;
		this.adminService = adminService;
	}
	
	// 아이디 만들기
	@PostMapping("/signUp")
	public String signUpAdmin(@RequestBody Admin admin) {
		adminService.signUpAdmin(admin);
		return "회원가입 성공";
	}
    
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
	
	// 썸네일 저장
	@PostMapping("/thumbnail")
	public void saveThumbnail(ThumbnailCreateDTO thumbnailCreateDTO) {
		thumbnailService.insertThumbnail(thumbnailCreateDTO);
	}
	
	// 썸네일 불러오기

	@GetMapping("/thumbnail/{category}/{subCategory}")
	public List<Thumbnail> getThumbnail(@PathVariable("categoryId") Long categoryId, @PathVariable("categoryId") Long subCategoryId) {
		if(subCategoryId==null) {
			return thumbnailService.getThumbnailByCategory(categoryId);
		}else {
			return thumbnailService.getThumbnailBySubCategory(subCategoryId);
		}
		
	}
	
	
	// 썸네일 업데이트
	@PatchMapping("/thumbnail/{id}")
	public void updateThumbnail(ThumbnailCreateDTO thumbnailCreateDTO, @PathVariable("id") Long id) {
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
	  //썸네일 삭제
    @DeleteMapping("/thumbnail/{id}")
    public void deleteThumbnail(@PathVariable("id") Long id) throws FileNotFoundException, IOException {
        thumbnailService.deleteThumbnail(id);
    }
    
    @GetMapping("/photos/{id}")
    public List<Photo> getPhotos(@PathVariable("project_id") Long projectId) {
    	
        return null;
    }


}
