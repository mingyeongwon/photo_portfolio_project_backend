package com.example.portfolio.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio.dto.CategoryCreateDto;
import com.example.portfolio.dto.CategoryDto;
import com.example.portfolio.dto.CategoryUpdateDto;
import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.dto.SubCategoryDto;
import com.example.portfolio.model.Admin;
import com.example.portfolio.model.Category;
import com.example.portfolio.service.AdminService;
import com.example.portfolio.service.CategoryService;
import com.example.portfolio.service.ProjectService;

@RestController
@RequestMapping("/api")
public class ProjectController {

	private final CategoryService categoryService;
	private final ProjectService projectService;
	private final AdminService adminService;

	// 생성자 주입
	public ProjectController(CategoryService categoryService, ProjectService projectService,
			AdminService adminService) {
		this.categoryService = categoryService;
		this.projectService = projectService;
		this.adminService = adminService;
	}

	// 프로젝트 생성
	@PostMapping("/create/project")
	public void createProject(@ModelAttribute ProjectCreateDto projectCreateDtos) {
		projectService.createProject(projectCreateDtos);
	}

	// 프로젝트 수정
	@PutMapping("/update/project/{id}")
	public void updateProject(@ModelAttribute ProjectUpdateDto projectUpdateDto, @PathVariable("id") Long id)
			throws IOException {
		// 프로젝트 ID 설정
		projectUpdateDto.setId(id);
		// 프로젝트 업데이트 서비스 호출
		projectService.updateProject(projectUpdateDto);
	}

	// 프로젝트 삭제
	@DeleteMapping("/delete/project/{id}")
	public void deleteProjecct(@PathVariable("id") Long id) {
		projectService.deleteProject(id);
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
//  @Secured("ROLE_ADMIN")
	@PostMapping("/categories")
	public void createCategories(@RequestBody List<CategoryCreateDto> categoryCreateDtos) {
		categoryService.createCategories(categoryCreateDtos);
	}

	// 카테고리 수정
	@PutMapping("/categories")
	public void updateCategories(@RequestBody List<CategoryUpdateDto> categoryUpdateDtos) {
		categoryService.updateCategories(categoryUpdateDtos);
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

}