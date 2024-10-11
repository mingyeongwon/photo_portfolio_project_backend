package com.example.portfolio.controller;

import java.io.FileNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.CategoryDto;
import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.dto.SubCategoryDto;
import com.example.portfolio.dto.ThumbnailCreateDto;
import com.example.portfolio.model.Admin;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.Project;
import com.example.portfolio.service.AdminService;
import com.example.portfolio.service.CategoryService;
import com.example.portfolio.service.PhotoService;
import com.example.portfolio.service.ProjectService;
import com.example.portfolio.service.ThumbnailService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class ProjectController {

	private final CategoryService categoryService;
	private final ProjectService projectService;
	private final ThumbnailService thumbnailService;
	private final AdminService adminService;
	private final PhotoService photoService;

	// 생성자 주입
	public ProjectController(CategoryService categoryService, ProjectService projectService,
			ThumbnailService thumbnailService, AdminService adminService, PhotoService photoService) {
		this.categoryService = categoryService;
		this.projectService = projectService;
		this.thumbnailService = thumbnailService;
		this.adminService = adminService;
		this.photoService = photoService;
	}

	@PostMapping("/create/project")
	public void createProejct(@ModelAttribute ProjectCreateDto projectCreateDto,
			@ModelAttribute ThumbnailCreateDto thumbnailCreateDTO) {
		// 프로젝트 생성
		Project savedProject = projectService.createProject(projectCreateDto);
		// 썸네일 생성
		thumbnailService.createThumbnail(thumbnailCreateDTO, savedProject.getId());
		// 상세 사진 생성
		photoService.createPhotos(projectCreateDto, savedProject);
	}

	// 프로젝트 수정
	// param id 값 어떻게 할지 생각해야함
	@Transactional
	@PutMapping("/update/project/{id}")
	public void updateProject(@ModelAttribute ProjectUpdateDto projectUpdateDto,
			@ModelAttribute ThumbnailCreateDto thumbnailCreateDto, @PathVariable("id") Long id) throws IOException {
		// 프로젝트 ID 확인
		if (id == null) {
			throw new IllegalArgumentException("Project ID가 null입니다.");
		} else {
			projectUpdateDto.setId(id);
		}

		// 썸네일 파일이 존재하는지 체크
		if (thumbnailCreateDto.getMultipartFile() != null && !thumbnailCreateDto.getMultipartFile().isEmpty()
				&& projectUpdateDto.getId() != null) {
			// 프로젝트 업데이트
			Project updatedProject = projectService.updateProject(projectUpdateDto);

			// 썸네일 업데이트
			thumbnailService.updateThumbnail(thumbnailCreateDto, id, updatedProject);

			// 썸네일이 수정되면 상세 사진도 업데이트
			photoService.updatePhotos(projectUpdateDto);
		} else {
			// 썸네일이 없으면 예외를 던지거나 업데이트를 막음
			throw new IllegalArgumentException("썸네일 파일이 없습니다. 썸네일이 없으면 프로젝트와 사진을 수정할 수 없습니다.");
		}
	}

	@Transactional
	@DeleteMapping("/delete/project/{id}")
	public void deleteProjecct(@PathVariable("id") Long id) {
		try {
			thumbnailService.deleteThumbnail(id);
			photoService.deletePhotosByProjectId(id);
			projectService.deleteProject(id);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		// thumbnailService.insertThumbnail(thumbnailCreateDTO);
	}

	
	// 썸네일 불러오기
	@GetMapping(value = { "/project/{category}/{subCategory}", "/project/{category}" })
	public List<ThumbnailCreateDto> getProjectList(@PathVariable("category") Long categoryId,
			@PathVariable(name = "subCategory", required = false) Long subCategoryId) {
		return thumbnailService.getThumbnailByCategory(categoryId, subCategoryId);
	}

//	// 썸네일 업데이트
//	@PatchMapping("/thumbnail/{id}")
//	public void updateThumbnail(ThumbnailCreateDto thumbnailCreateDTO, @PathVariable("id") Long id) {
//		MultipartFile image = thumbnailCreateDTO.getMultipartFile();
//		thumbnailCreateDTO.setTimgoname(image.getOriginalFilename());
//		thumbnailCreateDTO.setTimgtype(image.getContentType());
//		thumbnailService.updateThumbnail(thumbnailCreateDTO, id);
//	}

	// 썸네일 삭제
	@DeleteMapping("/thumbnail/{id}")
	public void deleteThumbnail(@PathVariable("id") Long id) throws FileNotFoundException, IOException {
		thumbnailService.deleteThumbnail(id);
	}

	// 프로젝트 저장
	@PostMapping("/project")
	public void saveProject(ProjectCreateDto projectCreateDto) {
		projectService.createProject(projectCreateDto);
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
