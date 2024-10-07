package com.example.portfolio.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.controller.ProjectUpdateDto;
import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.Photo;
import com.example.portfolio.model.Project;
import com.example.portfolio.model.SubCategory;
import com.example.portfolio.repository.CategoryRepository;
import com.example.portfolio.repository.PhotoRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.SubCategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class ProjectService {
	
	// 여기서 생성자 주입을 하지 않고 필드 주입을 하는게 좋은건지?
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	private SubCategoryRepository subCategoryRepository;

	@Autowired
	private PhotoRepository photoRepository;
	
	// 프로젝트 생성
	@Transactional
	public void insertProject(ProjectCreateDto projectCreateDto) {
		
		// 프로젝트 객체 생성
		Project project = new Project();
		project.setTitle(projectCreateDto.getTitle());
		
		// 카테고리 이름 검색
		Category category = categoryRepository.findById(projectCreateDto.getCategoryId()).get();
		project.setCategory(category);
		
		// 서브 카테고리 이름 검색
		SubCategory subCategory = subCategoryRepository.findById(projectCreateDto.getSubcategoryId()).get();
		project.setSubCategory(subCategory);
		
		// DB에 저장
		Project savedProject = projectRepository.save(project);
		
		// 다중 이미지 전체
		MultipartFile[] multipartFiles = projectCreateDto.getMultipartFiles();
		
		// 사진 전체 저장
		for(int i =0; i < multipartFiles.length; i++) {
			Photo photo = new Photo();
			MultipartFile multipartFile = multipartFiles[i];
			// TODO : oname, type도 저장해야함
			photo.setImageUrl(multipartFile.getOriginalFilename());
			photo.setProjectId(savedProject.getId());
			photoRepository.save(photo);
		}
	}
	
	// 프로젝트 업데이트
	@Transactional
	public void updateProject(ProjectUpdateDto projectUpdateDto) {
		
		// 프로젝트 객체 생성
		Project project = new Project();
		project.setTitle(projectUpdateDto.getTitle());
		
		// 카테고리 이름 검색
		Category category = categoryRepository.findById(projectUpdateDto.getCategoryId()).get();
		project.setCategory(category);
		
		// 서브 카테고리 이름 검색
		SubCategory subCategory = subCategoryRepository.findById(projectUpdateDto.getSubcategoryId()).get();
		project.setSubCategory(subCategory);
		
		// 새로 받아온 사진들
		MultipartFile[] multipartFiles = projectUpdateDto.getMultipartFiles();
		
		// 프로젝트 아이디로 맞는 사진 전체 불러오기
		List<Photo> existingPhotos = photoRepository.findByProjectId(projectUpdateDto.getId());
		
		// 새로운 사진 저장 및 이미있는 사진 건너뛰기
		for(MultipartFile multipartFile : multipartFiles) {
			Photo newPhoto = createPhoto(multipartFile, projectUpdateDto.getId());
			
			// 기존에 없는 사진만 통과
			if(existingPhotos.stream().noneMatch(p -> p.equals(newPhoto))) {
				// 새로운 사진 저장
				// TODO : gcs에 저장 및 위치 저장 imageUrl 저장
				photoRepository.save(newPhoto);
			}
		}
		
		// 삭제된 사진 처리
		List<Photo> deletePhotos = existingPhotos.stream()
        .filter(existingPhoto -> Arrays.stream(multipartFiles)
                .map(file -> createPhoto(file, project.getId()))
                .noneMatch(newPhoto -> newPhoto.equals(existingPhoto)))
        .collect(Collectors.toList());
			
		photoRepository.deleteAll(deletePhotos);
			
		
	}
	
	public void getProject() {
		
	}
	
	// 프로젝트 삭제
	@Transactional
	public void deleteProject(Long id) {
		// 이전의 조회 후 삭제하는 방식은 2번의 DB 조회가 필요해서 오버헤드가 발생하여 바로 삭제하는 방식으로 대체
		projectRepository.deleteById(id);
	}
	
	private Photo createPhoto(MultipartFile file, Long projectId) {
		Photo photo = new Photo();
		photo.setImgoname(file.getOriginalFilename());
		photo.setId(projectId);
		photo.setImgtype(file.getContentType());
		return photo;
	}


}
