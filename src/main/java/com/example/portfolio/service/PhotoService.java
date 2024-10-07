package com.example.portfolio.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.model.Photo;
import com.example.portfolio.model.Project;
import com.example.portfolio.repository.PhotoRepository;

@Service
public class PhotoService {

	@Autowired
	private GcsService gcsService;

	@Autowired
	private PhotoRepository photoRepository;

	// 사진 생성
	public void createPhoto(ProjectCreateDto projectCreateDto, Project savedProject) {
		// 다중 이미지 전체
		MultipartFile[] multipartFiles = projectCreateDto.getMultipartFiles();

		for (int i = 0; i < multipartFiles.length; i++) {
			try {
				Photo photo = new Photo();
				MultipartFile multipartFile = multipartFiles[i];
				String url = gcsService.uploadFile(multipartFile, savedProject.getTitle());
				photo.setImageUrl(url);
				photo.setImgtype(multipartFile.getOriginalFilename());
				photo.setImgtype(multipartFile.getContentType());
				photo.setProjectId(savedProject.getId());
				photoRepository.save(photo);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 사진 업데이트
	public void updatePhoto(ProjectUpdateDto projectUpdateDto) {
		// 새로 받아온 사진들
		MultipartFile[] multipartFiles = projectUpdateDto.getMultipartFiles();

		// 프로젝트 아이디로 맞는 사진 전체 불러오기
		List<Photo> existingPhotos = photoRepository.findByProjectId(projectUpdateDto.getId());

		// 새로운 사진 저장 및 이미있는 사진 건너뛰기
		for (MultipartFile multipartFile : multipartFiles) {
			Photo newPhoto = createPhoto(multipartFile, projectUpdateDto.getId());

			// 기존에 없는 사진만 통과
			if (existingPhotos.stream().noneMatch(p -> p.equals(newPhoto))) {

				// 새로운 사진 저장 & gcs 저장
				try {
					String url = gcsService.uploadFile(multipartFile, projectUpdateDto.getTitle());
					newPhoto.setImageUrl(url);
					newPhoto.setImgoname(multipartFile.getOriginalFilename());
					newPhoto.setImgtype(multipartFile.getContentType());
					photoRepository.save(newPhoto);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	// 사진 정보 가져오기
	public void getPhoto() {

	}

	// 사진 삭제
	public void deletePhoto(Long id) {

	}

	private Photo createPhoto(MultipartFile file, Long projectId) {
		Photo photo = new Photo();
		photo.setImgoname(file.getOriginalFilename());
		photo.setProjectId(projectId);
		photo.setImgtype(file.getContentType());
		return photo;
	}
}
