package com.example.portfolio.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
import com.example.portfolio.dto.ProjectDetailDto;
import com.example.portfolio.dto.ProjectListCustomDto;
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
	public ProjectController(CategoryService categoryService, ProjectService projectService, AdminService adminService,
			AdminDetailsService adminDetailsService, PhotoService photoService) {
		this.categoryService = categoryService;
		this.projectService = projectService;
		this.adminService = adminService;
		this.adminDetailsService = adminDetailsService;
		this.photoService = photoService;
	}

	@PostMapping("/create/project")
	public void createProject(@ModelAttribute ProjectCreateDto projectCreateDtos) {
		projectService.createProject(projectCreateDtos);
	}

	@PutMapping("/update/project/{id}")
	public void updateProject(@ModelAttribute ProjectUpdateDto projectUpdateDto, @PathVariable("id") Long id)
			throws IOException {
		// 프로젝트 ID 설정
		projectUpdateDto.setId(id);
		// 프로젝트 업데이트 서비스 호출
		projectService.updateProject(projectUpdateDto);
	}

	@GetMapping("/get/project")
	public Slice<ProjectListDto> getProjectListForMainPage(
			@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestParam(name = "categoryId", required = false) Long categoryId,
			@RequestParam(name = "subCategoryId", required = false) Long subCategoryId) {
		return projectService.getProjectList(pageable, categoryId, subCategoryId);
	}

	@GetMapping("/get/adminProject")
	public ProjectListCustomDto getProjectListForAdmin(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "5") int size,
			@RequestParam(value = "sort", defaultValue = "id") String sort,
			@RequestParam(value = "direction", defaultValue = "desc") String direction,
			@RequestParam(value = "keyWord", defaultValue = "") String keyWord) {
		Sort sortOrder = direction.equalsIgnoreCase("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
		Pageable pageable = PageRequest.of(page, size, sortOrder);

		return adminService.getAdminProjectList(pageable, keyWord);

	}


	@GetMapping("/get/adminProject/{projectId}")
	public ProjectDetailDto getAdminProjectDetail(@PathVariable("projectId") Long projectId) {
		return projectService.getAdminProject(projectId);
	}

	@DeleteMapping("/delete/project/{id}")
	public void deleteProjecct(@PathVariable("id") Long id) {
		projectService.deleteProject(id);
	}

	@GetMapping("/loginSucess")
	public ResponseEntity<String> loginSucess() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();
		return ResponseEntity.ok(id);
	}
	
	@PostMapping("/signUp")
	public String signUpAdmin(@RequestBody Admin admin) {
		adminService.signUpAdmin(admin);
		return "회원가입 성공";
	}

	@GetMapping("/get/categories")
	public List<CategoryDto> getAllCategories(@RequestParam(name = "view", required = false) String view) {
		if ("main".equals(view)) {
			return categoryService.getCategoriesWithProjects();
		} else {
			return categoryService.getAllCategories();
		}
	}

//  @Secured("ROLE_ADMIN")
	@PostMapping("/categories")
	public CategoryCreateDto createCategories(@RequestBody CategoryCreateDto categoryCreateDtos) {
		return categoryService.createCategories(categoryCreateDtos);
	}

	@DeleteMapping("/categories/{id}")
	public void deleteCategories(@PathVariable("id") Long categoryId) {
		categoryService.deleteCategory(categoryId);
	}

	@GetMapping("/categories/{id}/used")
	public boolean isCategoryUsed(@PathVariable("id") Long categoryId) {
		return categoryService.isCategoryUsed(categoryId);
	}

	@GetMapping("/category")
	public List<Category> getCategory() {
		return categoryService.getCategory();
	}

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
	public Slice<PhotoListDto> getPhotos(
			@PageableDefault( size = 12) Pageable pageable,
			@PathVariable("id") Long projectId) {
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
