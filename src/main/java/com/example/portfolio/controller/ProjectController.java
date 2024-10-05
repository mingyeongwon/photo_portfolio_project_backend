package com.example.portfolio.controller;

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
import com.example.portfolio.dto.ProjectCreateDTO;
import com.example.portfolio.dto.ThumbnailCreateDTO;
import com.example.portfolio.model.Admin;
import com.example.portfolio.model.Category;
import com.example.portfolio.service.AdminService;
import com.example.portfolio.service.CategoryService;
import com.example.portfolio.service.ProjectService;
import com.example.portfolio.service.ThumbnailService;

@RestController
@RequestMapping("/api")
public class ProjectController {

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
	public void createThumbnail(ThumbnailCreateDTO thumbnailCreateDTO) {
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
	
	// 썸네일 불러오기
	@GetMapping("/thumbnail")
	public void getThumbnail() {
		//TODO: GCP에서 사진 받아오는 로직 구현해야함
		
		
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
		thumbnailService.updateThumbnail(thumbnailCreateDTO, id);
		
	}
	
	// 썸네일 삭제
	@DeleteMapping("/thumbnail/{id}")
	public void deleteThumbnail(@PathVariable("id") Long id) {
		thumbnailService.deleteThumbnail(id);
	}
	
	
	// 프로젝트 생성
	@PostMapping("/project")
	public void createProject(ProjectCreateDTO projectCreateDTO) {
		projectService.insertProject(projectCreateDTO);
	}
	
	// 프로젝트 불러오기
	
	
	// 프로젝트 업데이트
	
	
	// 프로젝트 삭제
	@DeleteMapping("/project/{id}")
	public void deleteProject(@PathVariable("id") Long id) {
		projectService.deleteProject(id);
	}
	

}
