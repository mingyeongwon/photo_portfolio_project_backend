package com.example.portfolio.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.model.Project;
import com.example.portfolio.repository.ProjectRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import jakarta.transaction.Transactional;

@Service
public class ThumbnailService {

	@Autowired
	private ProjectRepository projectRepository;
	
    @Autowired
    private GcsService gcsService;
	
	// 썸네일 생성 
	@Transactional
	public void createThumbnail(ProjectCreateDto projectCreateDto, Project savedProject) {
		MultipartFile multipartFile = projectCreateDto.getThumbnailMultipartFile();
		try {
			String url = gcsService.uploadFile(multipartFile, savedProject.getId());
			savedProject.setThumbnailUrl(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	@Transactional
//	public void updateThumbnail(ThumbnailCreateDto thumbnailCreateDTO, Long id,Project updatedProject) {
//		try {
//			MultipartFile image = thumbnailCreateDTO.getMultipartFile();
//			//사진을 먼저 삭제 한 후 다시 insert 
//			deleteThumbnail(id);
//			
//			String url = uploadImageToGCS(thumbnailCreateDTO, updatedProject.getTitle());
//			thumbnailCreateDTO.setTimgoname(image.getOriginalFilename());
//			thumbnailCreateDTO.setTimgtype(image.getContentType());
//
//			Thumbnail thumbnail = new Thumbnail();
//			thumbnail.setImageUrl(url);
//			thumbnail.setProjectId(id);
//
//			thumbnailRepository.save(thumbnail);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}
	
	// 썸네일 삭제
	@Transactional
	public void deleteThumbnail(Long id) {
		
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("project not found"));
		
        try {
            gcsService.deleteThumbnailFile(project.getThumbnailUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }		
	}

//	@Transactional
//	public List<ThumbnailCreateDto> getThumbnailByCategory(Long categoryId, Long subCategoryId) {
//		List<Project> projects = new ArrayList<>();
//
//		if (subCategoryId == null) {
//			// categoryId로 프로젝트 찾아오기
//			projects = projectRepository.findByCategory_Id(categoryId);
//		} else {
//			// subcategoryId로 프로젝트 찾아오기
//			projects = projectRepository.findBySubCategory_Id(subCategoryId);
//		}
//
//		System.out.println(projects);
//		List<Thumbnail> thumbnails = new ArrayList<>();
//		for (Project project : projects) {
//			// 프로젝트 아이디로 썸네일 찾아오기
//			Thumbnail thumbnail = thumbnailRepository.findByProjectId(project.getId());
//			if (thumbnail != null) {
//				thumbnails.add(thumbnail);
//			}
//		}
//
//		return thumbnails.stream().map(this::thumbnailEntityToDto).toList();
//	}
//
//	// Entity -> DTO 변환
//	private ThumbnailCreateDto thumbnailEntityToDto(Thumbnail thumbnail) {
//		ThumbnailCreateDto thumbnailCreateDto = new ThumbnailCreateDto();
//		thumbnailCreateDto.setId(thumbnail.getId());
//		thumbnailCreateDto.setTimgsname(thumbnail.getImageUrl());
//		thumbnailCreateDto.setProjectId(thumbnail.getProjectId());
//		return thumbnailCreateDto;
//	}
//
}
