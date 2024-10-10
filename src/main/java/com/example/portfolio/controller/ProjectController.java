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
import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.dto.SubCategoryDto;
import com.example.portfolio.dto.ThumbnailCreateDto;
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
	
	@GetMapping("/subCategory/{id}")
	public List<SubCategoryDto> getSubCategory(@PathVariable("id") Long categoryId) {
		return categoryService.getSubCategory(categoryId);
	}
	
	// 썸네일 저장
	@PostMapping("/thumbnail")
	public void saveThumbnail(ThumbnailCreateDto thumbnailCreateDTO) {
		MultipartFile image = thumbnailCreateDTO.getMultipartFile();
		thumbnailCreateDTO.setTimgoname(image.getOriginalFilename());
		thumbnailCreateDTO.setTimgtype(image.getContentType());
		thumbnailService.insertThumbnail(thumbnailCreateDTO);
	}
	
	// 썸네일 불러오기
	@GetMapping("/thumbnail/{category}/{subCategory}")
	public List<ThumbnailCreateDto> getThumbnail(@PathVariable("category") Long categoryId,
			@PathVariable("subCategory") Long subCategoryId) {
		return thumbnailService.getThumbnailByCategory(categoryId, subCategoryId);
	}
	
	
	// 썸네일 업데이트
	@PatchMapping("/thumbnail/{id}")
	public void updateThumbnail(ThumbnailCreateDto thumbnailCreateDTO, @PathVariable("id") Long id) {

		MultipartFile image = thumbnailCreateDTO.getMultipartFile();
		thumbnailCreateDTO.setTimgoname(image.getOriginalFilename());
		thumbnailCreateDTO.setTimgtype(image.getContentType());
		thumbnailService.updateThumbnail(thumbnailCreateDTO, id);
		
	}
	  //썸네일 삭제
    @DeleteMapping("/thumbnail/{id}")
    public void deleteThumbnail(@PathVariable("id") Long id) throws FileNotFoundException, IOException {
        thumbnailService.deleteThumbnail(id);
    }
 // 프로젝트 저장
 	@PostMapping("/project")
 	public void saveProject(ProjectCreateDto  projectCreateDto) {
 		projectService.createProject(projectCreateDto);
 	}
 	
 	// 프로젝트 불러오기
 	@GetMapping("/project/{id}")
 	public List<Photo> getProject(@PathVariable("id") Long projectId) {
 		return projectService.getProject(projectId);
 	}
 	
 	// 프로젝트 업데이트
 	@PutMapping("/project")
 	public void updateProject(ProjectUpdateDto projectUpdateDto) {
 		projectService.updateProject(projectUpdateDto);
 		
 	}
 	
 	// 프로젝트 삭제
 	@DeleteMapping("/project/{id}")
 	public void deleteProject(@PathVariable("id") Long id) {
 		projectService.deleteProject(id); 
 	}
    

}
