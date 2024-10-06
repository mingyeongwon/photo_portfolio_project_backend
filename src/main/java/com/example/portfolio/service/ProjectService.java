package com.example.portfolio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ProjectCreateDTO;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.Photo;
import com.example.portfolio.model.Project;
import com.example.portfolio.repository.CategoryRepository;
import com.example.portfolio.repository.PhotoRepository;
import com.example.portfolio.repository.ProjectRepository;

import jakarta.transaction.Transactional;

@Service
public class ProjectService {
	
	// 여기서 생성자 주입을 하지 않고 필드 주입을 하는게 좋은건지?
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private PhotoRepository photoRepository;
	
	public void insertProject(ProjectCreateDTO projectCreateDTO) {
		
		// 프로젝트 객체 생성
		Project project = new Project();
		project.setTitle(projectCreateDTO.getTitle());
		Category category = categoryRepository.findById(projectCreateDTO.getCategoryId()).get();
		project.setCategory(category);
		
		// TODO: 테이블 변경 후 다시 손봐야 하는 로직
//		SubCategory subCategory = categoryRepository.findById(projectCreateDTO.getSubCategoryId()).get();
//		project.setSubCategory(subCategory);
		
		// DB에 저장
		Project savedProject = projectRepository.save(project);
		MultipartFile[] multipartFiles = projectCreateDTO.getMultipartFiles();
		System.out.println("사진 갯수: " + multipartFiles.length);
		
		// 사진 전체 저장
		for(int i =0; i < multipartFiles.length; i++) {
			System.out.println("for문 돈다");
			Photo photo = new Photo();
			
			MultipartFile multipartFile = multipartFiles[i];
			photo.setImageUrl(multipartFile.getOriginalFilename());
			photo.setProjectId(savedProject.getId());
			photoRepository.save(photo);
			
		}
	}

	@Transactional
	public void deleteProject(Long id) {
//		Project project = projectRepository.findById(id).get();
//		projectRepository.delete(project);
		projectRepository.deleteById(id);
	}

}
