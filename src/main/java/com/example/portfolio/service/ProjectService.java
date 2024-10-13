package com.example.portfolio.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.Project;
import com.example.portfolio.model.SubCategory;
import com.example.portfolio.repository.CategoryRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.SubCategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final CategoryRepository categoryRepository;
	private final SubCategoryRepository subCategoryRepository;
	private final GcsService gcsService;
	private final PhotoService photoService;

	// 여러 의존성을 생성자로 주입
	public ProjectService(ProjectRepository projectRepository, CategoryRepository categoryRepository,
			SubCategoryRepository subCategoryRepository, GcsService gcsService, PhotoService photoService) {
		this.projectRepository = projectRepository;
		this.categoryRepository = categoryRepository;
		this.subCategoryRepository = subCategoryRepository;
		this.gcsService = gcsService;
		this.photoService = photoService;
	}

	// 프로젝트 생성
	@Transactional
	public void createProject(ProjectCreateDto projectCreateDto) {
		Project project = new Project();

		// 프로젝트 제목 설정
		project.setTitle(projectCreateDto.getTitle());

		// 카테고리 설정
		Category category = categoryRepository.findById(projectCreateDto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found"));
		project.setCategory(category);

		// 서브 카테고리 설정
		SubCategory subCategory = subCategoryRepository.findById(projectCreateDto.getSubcategoryId())
				.orElseThrow(() -> new RuntimeException("SubCategory not found"));
		project.setSubCategory(subCategory);

		// 프로젝트를 먼저 저장하여 ID 생성
		Long projectId = projectRepository.save(project).getId();

		// 썸네일 생성
		MultipartFile multipartFile = projectCreateDto.getThumbnailMultipartFile();
		try {
			String url = gcsService.uploadFile(multipartFile, projectId); // GCS에 파일 업로드
			project.setThumbnailUrl(url); // 썸네일 URL 설정
		} catch (IOException e) {
			throw new RuntimeException("Failed to upload thumbnail to GCS", e); // 예외 처리
		}

		// 사진 생성
		photoService.createPhotos(projectCreateDto, projectId);

		// 최종적으로 프로젝트 업데이트
		projectRepository.save(project);
	}

	// 프로젝트 업데이트
	@Transactional
	public void updateProject(ProjectUpdateDto projectUpdateDto) {
		Project project = projectRepository.findById(projectUpdateDto.getId())
				.orElseThrow(() -> new RuntimeException("Project not found"));

		// 제목 업데이트
		if (projectUpdateDto.getTitle() != null && !projectUpdateDto.getTitle().isEmpty()) {
			project.setTitle(projectUpdateDto.getTitle());
		}

		// 카테고리 업데이트
		if (projectUpdateDto.getCategoryId() != null) {
			Category category = categoryRepository.findById(projectUpdateDto.getCategoryId())
					.orElseThrow(() -> new RuntimeException("Category not found"));
			project.setCategory(category);
		}

		// 서브 카테고리 업데이트
		if (projectUpdateDto.getSubcategoryId() != null) {
			SubCategory subCategory = subCategoryRepository.findById(projectUpdateDto.getSubcategoryId())
					.orElseThrow(() -> new RuntimeException("SubCategory not found"));
			project.setSubCategory(subCategory);
		}

		// 썸네일이 있는 경우에만 업데이트
		if (projectUpdateDto.getThumbnailMultipartFile() != null
				&& !projectUpdateDto.getThumbnailMultipartFile().isEmpty()) {
			try {
				// 기존 썸네일 삭제
				gcsService.deleteThumbnailFile(project.getThumbnailUrl());

				// 새 썸네일 업로드
				String url = gcsService.uploadFile(projectUpdateDto.getThumbnailMultipartFile(), project.getId());
				project.setThumbnailUrl(url);
			} catch (IOException e) {
				throw new RuntimeException("Failed to upload new thumbnail to GCS", e);
			}
		}

		// 사진이 있는 경우 업데이트
		if (projectUpdateDto.getPhotoMultipartFiles() != null && projectUpdateDto.getPhotoMultipartFiles().length > 0) {
			photoService.updatePhotos(projectUpdateDto);
		}

		projectRepository.save(project);
	}

	// 프로젝트 삭제
	@Transactional
	public void deleteProject(Long id) {
		Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
		// GCS 썸네일과 관련 사진들 삭제
		try {
			gcsService.deleteThumbnailFile(project.getThumbnailUrl());
			photoService.deletePhotosByProjectId(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		projectRepository.delete(project);
	}

//	public void getProject{
//	
//}
}
