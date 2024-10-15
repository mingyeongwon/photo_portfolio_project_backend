package com.example.portfolio.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
<<<<<<< Upstream, based on origin/master
import com.example.portfolio.dto.CategoryUpdateDto;
=======
import com.example.portfolio.dto.PhotoListDto;
>>>>>>> 39db424 photo 리스트 불러오기
import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectListDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.dto.SubCategoryDto;
import com.example.portfolio.model.Admin;
import com.example.portfolio.model.Category;
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
	private final PhotoService photoService;


	// 생성자 주입
	public ProjectController(CategoryService categoryService, ProjectService projectService,
			AdminService adminService,PhotoService photoService) {
		this.categoryService = categoryService;
		this.projectService = projectService;
		this.adminService = adminService;
		this.photoService = photoService;
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
	
	//프로젝트 가져오기 
	@GetMapping(value={"/get/project/{categoryId}/{subCategory}", "/get/project/{categoryId}"} )
	public List<ProjectListDto> getProject( 
			@PageableDefault(page= 0, size = 5) Pageable pageable, 
			@PathVariable("categoryId") Long categoryId,
			@PathVariable(name = "subCategory", required = false) Long subCategoryId){
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

	@GetMapping("/photos/{id}")
	public List<PhotoListDto> getPhotos(
			@PageableDefault(page= 0, size = 5) Pageable pageable,
			@PathVariable("id") Long projectId) {
		return photoService.getPhotoList(pageable, projectId);
	}
	
	
<<<<<<< Upstream, based on origin/master
=======

//	// 썸네일 저장
//	@PostMapping("/thumbnail")
//	public void saveThumbnail(ThumbnailCreateDto thumbnailCreateDTO) {
//		MultipartFile image = thumbnailCreateDTO.getMultipartFile();
//		thumbnailCreateDTO.setTimgoname(image.getOriginalFilename());
//		thumbnailCreateDTO.setTimgtype(image.getContentType());
//		// thumbnailService.insertThumbnail(thumbnailCreateDTO);
//	}
//
//	
//	// 썸네일 불러오기
//	@GetMapping(value = { "/project/{category}/{subCategory}", "/project/{category}" })
//	public List<ThumbnailCreateDto> getProjectList(@PathVariable("category") Long categoryId,
//			@PathVariable(name = "subCategory", required = false) Long subCategoryId) {
//		return thumbnailService.getThumbnailByCategory(categoryId, subCategoryId);
//	}

//	// 썸네일 업데이트
//	@PatchMapping("/thumbnail/{id}")
//	public void updateThumbnail(ThumbnailCreateDto thumbnailCreateDTO, @PathVariable("id") Long id) {
//		MultipartFile image = thumbnailCreateDTO.getMultipartFile();
//		thumbnailCreateDTO.setTimgoname(image.getOriginalFilename());
//		thumbnailCreateDTO.setTimgtype(image.getContentType());
//		thumbnailService.updateThumbnail(thumbnailCreateDTO, id);
//	}
//
//	// 썸네일 삭제
//	@DeleteMapping("/thumbnail/{id}")
//	public void deleteThumbnail(@PathVariable("id") Long id) throws FileNotFoundException, IOException {
//		thumbnailService.deleteThumbnail(id);
//	}

//	// 프로젝트 저장
//	@PostMapping("/project")
//	public void saveProject(ProjectCreateDto projectCreateDto) {
//		projectService.createProject(projectCreateDto);
//	}

//	// 프로젝트 업데이트
//	@PutMapping("/project")
//	public void updateProject(ProjectUpdateDto projectUpdateDto) {
//		projectService.updateProject(projectUpdateDto);
//
//	}

//	// 프로젝트 삭제
//	@DeleteMapping("/project/{id}")
//	public void deleteProject(@PathVariable("id") Long id) {
//		projectService.deleteProject(id);
//	}
>>>>>>> 9307a9d 프로젝트 검색 및 정렬하기

}
