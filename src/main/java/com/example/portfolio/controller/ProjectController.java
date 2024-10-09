package com.example.portfolio.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio.dto.CategoryDto;
import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.dto.ThumbnailCreateDto;
import com.example.portfolio.model.Admin;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.Project;
import com.example.portfolio.model.Thumbnail;
import com.example.portfolio.service.AdminService;
import com.example.portfolio.service.CategoryService;
import com.example.portfolio.service.PhotoService;
import com.example.portfolio.service.ProjectService;
import com.example.portfolio.service.ThumbnailService;

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
		thumbnailService.createThumbnail(thumbnailCreateDTO);
		// 상세 사진 생성
		photoService.createPhotos(projectCreateDto, savedProject);
	}

	// 프로젝트 수정
	// param id 값 어떻게 할지 생각해야함
	@PutMapping("/update/project/{id}")
	public void updateProject(@ModelAttribute ProjectUpdateDto projectUpdateDto,
			@ModelAttribute ThumbnailCreateDto thumbnailCreateDto, @PathVariable("id") Long id) {
		// 프로젝트 업데이트
		Project updatedProject = projectService.updateProject(projectUpdateDto);
		thumbnailService.updateThumbnail(thumbnailCreateDto, id, updatedProject);
		   // 상세 사진 업데이트
        photoService.updatePhotos(projectUpdateDto);
	}

	@DeleteMapping("/delete/project/{id}")
	public void deleteProjecct(@ModelAttribute ProjectUpdateDto projectUpdateDto,
			@ModelAttribute ThumbnailCreateDto thumbnailCreateDTO, @PathVariable("id") Long id) {
		Project updatedProject = projectService.updateProject(projectUpdateDto);

		try {
			thumbnailService.deleteThumbnail(id);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		photoService.deletePhotosByProjectId(id);
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

	// 썸네일 불러오기
	@GetMapping("/thumbnail/{categoryId}")
	public List<Thumbnail> getThumbnail(@PathVariable("categoryId") Long categoryId) {
		return thumbnailService.getThumbnail(categoryId);
	}


	// 썸네일 삭제
	@DeleteMapping("/thumbnail/{id}")
	public void deleteThumbnail(@PathVariable("id") Long id) throws FileNotFoundException, IOException {
		thumbnailService.deleteThumbnail(id);
	}
	
	// 프로젝트 불러오기
	@GetMapping("/project")
	public void getProject() {
//		projectService.getProject();
	}

	// 프로젝트 삭제
	@DeleteMapping("/project/{id}")
	public void deleteProject(@PathVariable("id") Long id) {
		projectService.deleteProject(id);
	}

}
