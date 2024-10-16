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
import com.example.portfolio.dto.SubCategoryCreateDto;
import com.example.portfolio.dto.SubCategoryDto;
import com.example.portfolio.dto.SubCategoryUpdateDto;
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
	public void createProject(@ModelAttribute ProjectCreateDto projectCreateDto) {
		projectService.createProject(projectCreateDto);
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
	public CategoryCreateDto createCategories(@RequestBody CategoryCreateDto categoryCreateDtos) {
		return categoryService.createCategories(categoryCreateDtos);
	}

	// 카테고리 삭제
	@DeleteMapping("/categories/{id}")
	public void deleteCategories(@PathVariable("id") Long categoryId) {
		categoryService.deleteCategory(categoryId);
	}

	// 카테고리가 사용 중인지 확인하는 엔드포인트 추가
	@GetMapping("/categories/{id}/used")
	public boolean isCategoryUsed(@PathVariable("id") Long categoryId) {
		return categoryService.isCategoryUsed(categoryId);
	}

	@GetMapping("/category")
	public List<Category> getCategory() {
		return categoryService.getCategory();
	}

	// 카테고리 수정
	@PutMapping("/categories/{id}")
	public void updateCategory(@PathVariable("id") Long categoryId, @RequestBody CategoryUpdateDto categoryUpdateDto) {
		categoryUpdateDto.setId(categoryId);
		System.out.println("디티오 이름입니다.: " + categoryUpdateDto.getName());
		categoryService.updateCategory(categoryUpdateDto);
	}

	@PostMapping("/category/{selectedCategoryId}/subcategory")
	public SubCategoryCreateDto createSubCategory(@PathVariable("selectedCategoryId") Long categoryId,
			@RequestBody SubCategoryCreateDto subCategoryCreateDto) {
		SubCategoryCreateDto createdSubCategory = categoryService.createSubCategory(categoryId, subCategoryCreateDto);
		return createdSubCategory;
	}

	@GetMapping("/subCategory/{id}")
	public List<SubCategoryDto> getSubCategory(@PathVariable("id") Long categoryId) {
		return categoryService.getSubCategory(categoryId);
	}

	@DeleteMapping("/subcategory/{id}")
	public void deleteSubCategory(@PathVariable("id") Long subCategoryId) {
		categoryService.deleteSubCategory(subCategoryId);
	}

	// 카테고리가 사용 중인지 확인하는 엔드포인트 추가
	@GetMapping("/subcategory/{id}/used")
	public boolean isSubCategoryUsed(@PathVariable("id") Long subCategoryId) {
		return categoryService.isSubCategoryUsed(subCategoryId);
	}

	@PutMapping("/subcategories/{subCategoryId}")
	public void updateSubCategory(@PathVariable("subCategoryId") Long subCategoryId,
			@RequestBody SubCategoryUpdateDto subCategoryUpdateDto) {
		categoryService.updateSubCategory(subCategoryId, subCategoryUpdateDto);
	}
}
