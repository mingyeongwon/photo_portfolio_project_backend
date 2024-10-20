package com.example.portfolio.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio.dto.CategoryCreateDto;
import com.example.portfolio.dto.CategoryDto;
import com.example.portfolio.dto.CategoryUpdateDto;
import com.example.portfolio.dto.PhotoListDto;
import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectListDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.dto.SubCategoryCreateDto;
import com.example.portfolio.dto.SubCategoryDto;
import com.example.portfolio.dto.SubCategoryUpdateDto;
import com.example.portfolio.model.Admin;
import com.example.portfolio.model.Category;
import com.example.portfolio.security.AdminDetailsService;
import com.example.portfolio.service.AdminService;
import com.example.portfolio.service.CategoryService;
import com.example.portfolio.service.PhotoService;
import com.example.portfolio.service.ProjectService;

@RestController
@RequestMapping("/api")
public class ProjectController {

	private final CategoryService categoryService;
	private final ProjectService projectService;
	private final AdminService adminService;
	private final AdminDetailsService adminDetailsService;
	private final PhotoService photoService;

	// 생성자 주입
	public ProjectController(CategoryService categoryService, ProjectService projectService,
			AdminService adminService, AdminDetailsService adminDetailsService, PhotoService photoService) {
		this.categoryService = categoryService;
		this.projectService = projectService;
		this.adminService = adminService;
		this.adminDetailsService = adminDetailsService;
		this.photoService = photoService;
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
	
	//프로젝트 가져오기 
	@GetMapping("/get/project")
	public List<ProjectListDto> getProject( Pageable pageable, 
			@RequestParam( name="categoryId", required = false) Long categoryId,
			@RequestParam(name = "subCategoryId", required = false) Long subCategoryId){
		return projectService.getProjectList(pageable,categoryId,subCategoryId);
	}

	
	//admin page 프로젝트 가져오기 
	@GetMapping("/get/adminProject" )
	public List<ProjectListDto> getAdminProject(
			@PageableDefault(page= 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, 
			@RequestParam(value= "keyWord", defaultValue = "") String keyWord ){ 
		return adminService.getAdminProjectList(pageable,keyWord);
	}

	// 프로젝트 삭제
	@DeleteMapping("/delete/project/{id}")
	public void deleteProjecct(@PathVariable("id") Long id) {
		projectService.deleteProject(id);
	}
	
	// 로그인
	@GetMapping("/loginSucess")
	public ResponseEntity<String> loginSucess() {
		// 로그인된 유저 정보 가져옴
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();
		
		return ResponseEntity.ok(id);
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
	
	@GetMapping("/photos/{id}")
	public List<PhotoListDto> getPhotos(
			@PageableDefault( size = 12) Pageable pageable,
			@PathVariable("id") Long projectId) {
		System.out.println("page"+pageable.getPageNumber());
		return photoService.getPhotoList(pageable, projectId);
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

