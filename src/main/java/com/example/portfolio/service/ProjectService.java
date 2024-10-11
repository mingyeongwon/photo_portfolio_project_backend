package com.example.portfolio.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.exception.CustomException;
import com.example.portfolio.exception.ErrorCode;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.Project;
import com.example.portfolio.model.SubCategory;
import com.example.portfolio.repository.CategoryRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.SubCategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class ProjectService {
  
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Transactional
    public Project createProject(ProjectCreateDto projectCreateDto) {
        Project project = new Project();
        project.setTitle(projectCreateDto.getTitle());

        // 카테고리 설정
        Category category = categoryRepository.findById(projectCreateDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        project.setCategory(category);

        // 서브 카테고리 설정
        SubCategory subCategory = subCategoryRepository.findById(projectCreateDto.getSubcategoryId())
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
        project.setSubCategory(subCategory);

        // 프로젝트 저장
        return projectRepository.save(project);
    }
    
//	public void getProject{
//    	
//    }

    @Transactional
    public Project updateProject(ProjectUpdateDto projectUpdateDto) {
        Project project = projectRepository.findById(projectUpdateDto.getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setTitle(projectUpdateDto.getTitle());

        // 카테고리 업데이트
        Category category = categoryRepository.findById(projectUpdateDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        project.setCategory(category);

        // 서브 카테고리 업데이트
        SubCategory subCategory = subCategoryRepository.findById(projectUpdateDto.getSubcategoryId())
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
        project.setSubCategory(subCategory);

        // 프로젝트 업데이트
       return projectRepository.save(project);
    }

 
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }
}
