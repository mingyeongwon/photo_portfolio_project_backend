package com.example.portfolio.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.Photo;
import com.example.portfolio.model.Project;
import com.example.portfolio.model.SubCategory;
import com.example.portfolio.repository.CategoryRepository;
import com.example.portfolio.repository.PhotoRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.SubCategoryRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import jakarta.transaction.Transactional;

@Service
public class ProjectService {

	// 여기서 생성자 주입을 하지 않고 필드 주입을 하는게 좋은건지?
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private SubCategoryRepository subCategoryRepository;

	@Autowired
	private PhotoRepository photoRepository;

	@Autowired
	private GcsService gcsService;

	@Value("${spring.cloud.gcp.storage.project-id}")
	private String projectId;

	@Value("${spring.cloud.gcp.storage.credentials.location}")
	private String keyFileName;

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;

	// 프로젝트 생성
	@Transactional
	public Project createProject(ProjectCreateDto projectCreateDto) {
		// 카테고리 이름 검색
		Category category = categoryRepository.findById(projectCreateDto.getCategoryId()).get();
		// 서브 카테고리 이름 검색
		SubCategory subCategory = subCategoryRepository.findById(projectCreateDto.getSubcategoryId()).get();
		// 프로젝트 객체 생성
		Project project = new Project();
		project.setId(projectCreateDto.getId());
		project.setTitle(projectCreateDto.getTitle());
		project.setCategory(category);
		project.setSubCategory(subCategory);
		// DB에 저장
		Project savedProject = projectRepository.save(project);
		System.out.println("프로젝트 생성");
		return savedProject;
	}

	// 프로젝트 업데이트
	@Transactional
	public Project updateProject(ProjectUpdateDto projectUpdateDto) {

		// 프로젝트 객체 생성
		Project project = new Project();
		project.setId(projectUpdateDto.getId());
		project.setTitle(projectUpdateDto.getTitle());

		// 카테고리 이름 검색
		Category category = categoryRepository.findById(projectUpdateDto.getCategoryId()).get();
		project.setCategory(category);

		// 서브 카테고리 이름 검색
		SubCategory subCategory = subCategoryRepository.findById(projectUpdateDto.getSubcategoryId()).get();
		project.setSubCategory(subCategory);
		
		Project updatedProject = projectRepository.save(project);
		return updatedProject;
//		// 새로 받아온 사진들
//		MultipartFile[] multipartFiles = projectUpdateDto.getMultipartFiles();
//
//		// 프로젝트 아이디로 맞는 사진 전체 불러오기
//		List<Photo> existingPhotos = photoRepository.findByProjectId(projectUpdateDto.getId());
//
//		// 새로운 사진 저장 및 이미있는 사진 건너뛰기
//		for (MultipartFile multipartFile : multipartFiles) {
//			Photo newPhoto = createPhoto(multipartFile, projectUpdateDto.getId());
//
//			// 기존에 없는 사진만 통과
//			if (existingPhotos.stream().noneMatch(p -> p.equals(newPhoto))) {
//
//				// 새로운 사진 저장 & gcs 저장
//				try {
//					String url = gcsService.uploadFile(multipartFile, projectUpdateDto.getTitle());
//					newPhoto.setImageUrl(url);
//					newPhoto.setImgoname(multipartFile.getOriginalFilename());
//					newPhoto.setImgtype(multipartFile.getContentType());
//					photoRepository.save(newPhoto);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//		}
//
//		// 삭제된 사진 DB 삭제 및 gcs 삭제
//		List<Photo> deletePhotos = existingPhotos.stream().filter(existingPhoto -> Arrays.stream(multipartFiles)
//				.map(file -> createPhoto(file, project.getId())).noneMatch(newPhoto -> newPhoto.equals(existingPhoto)))
//				.collect(Collectors.toList());
//
//		photoRepository.deleteAll(deletePhotos);
//
//		try {
//			gcsService.deletePhotoToGcs(deletePhotos);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public void getProject() {

	}

	// 프로젝트 삭제
	@Transactional
	public void deleteProject(Long id) {
		// 이전의 조회 후 삭제하는 방식은 2번의 DB 조회가 필요해서 오버헤드가 발생하여 바로 삭제하는 방식으로 대체
		Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));

		projectRepository.deleteById(id);

	}

	private Photo createPhoto(MultipartFile file, Long projectId) {
		Photo photo = new Photo();
		photo.setImgoname(file.getOriginalFilename());
		photo.setProjectId(projectId);
		photo.setImgtype(file.getContentType());
		return photo;
	}

}
