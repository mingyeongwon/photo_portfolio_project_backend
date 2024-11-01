package com.example.portfolio.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.PhotoListDto;
import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.model.Photo;
import com.example.portfolio.repository.PhotoRepository;
import com.example.portfolio.repository.ProjectRepository;

import jakarta.transaction.Transactional;

@Service
public class PhotoService {

	private final GcsService gcsService;
	private final PhotoRepository photoRepository;
	private final ProjectRepository projectRepository;

	//생성자 주입
	public PhotoService(GcsService gcsService, PhotoRepository photoRepository, ProjectRepository projectRepository) {
		this.gcsService = gcsService;
		this.photoRepository = photoRepository;
		this.projectRepository = projectRepository;
	}

	// 사진 생성
	public void createPhotos(ProjectCreateDto projectCreateDto, Long projectId) {
	    MultipartFile[] multipartFiles = projectCreateDto.getPhotoMultipartFiles();

	    for (MultipartFile multipartFile : multipartFiles) {
	        try {
	            Photo photo = new Photo();
	            // WebP 형식으로 변환된 이미지를 GCS에 업로드
	            String url = gcsService.uploadWebpFile(multipartFile, projectId); // WebP 형식 업로드 메서드 호출
	            photo.setImageUrl(url);
	            photo.setImgoname(multipartFile.getOriginalFilename());
	            photo.setImgtype("image/webp"); // 변환 후 이미지 형식을 webp로 설정
	            photo.setProjectId(projectId);
	            photoRepository.save(photo);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}


	// 사진 업데이트
	public void updatePhotos(ProjectUpdateDto projectUpdateDto) {
		MultipartFile[] multipartFiles = projectUpdateDto.getPhotoMultipartFiles();

		for (MultipartFile multipartFile : multipartFiles) {
			Photo newPhoto = createPhoto(multipartFile, projectUpdateDto.getId());

				try {
					String url = gcsService.uploadWebpFile(multipartFile, projectUpdateDto.getId());
					newPhoto.setImageUrl(url);
					newPhoto.setImgoname(multipartFile.getOriginalFilename());
					newPhoto.setImgtype(multipartFile.getContentType());
					photoRepository.save(newPhoto);
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	// 사진 삭제
	public void deletePhotosByProjectId(Long projectId) {
		List<Photo> photos = photoRepository.findAllByProjectId(projectId);
		photoRepository.deleteAll(photos);

		try {
			gcsService.deletePhotoToGcs(photos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 있다면 true, 없다면 false
	private boolean isPhotoInFiles(Photo existingPhoto, MultipartFile[] multipartFiles, Long projectId) {
		for (MultipartFile multipartFile : multipartFiles) {
			Photo newPhoto = createPhoto(multipartFile, projectId);
			if (existingPhoto.equals(newPhoto)) {
				return true;
			}
		}
		return false;
	}

	// 기존에 사진이 존재하는지 확인
	private Photo createPhoto(MultipartFile file, Long projectId) {
		Photo photo = new Photo();
		photo.setImgoname(file.getOriginalFilename());
		photo.setProjectId(projectId);
		photo.setImgtype(file.getContentType());
		return photo;
	}

	@Transactional
	public Slice<PhotoListDto> getPhotoList(Pageable pageable, Long projectId) {
		// view count +1 로직
		projectRepository.updateViewCount(projectId);
		
		Slice<PhotoListDto> photos = photoRepository.findByPhotosProjectId(projectId,pageable);
		String thumbnailUrl = projectRepository.getById(projectId).getThumbnailUrl();
		List<PhotoListDto> totalPhotos = new ArrayList<>();
		totalPhotos.add(new PhotoListDto(null, thumbnailUrl));
		totalPhotos.addAll(photos.getContent());
		return new SliceImpl<>(totalPhotos, pageable, false);
	}
	
	// edit에서 삭제
	public void deleteSelectedPhotos(List<Long> deletedPhotoIds) {
		List<Photo> selecetedPhotos = photoRepository.findAllById(deletedPhotoIds);
		photoRepository.deleteAll(selecetedPhotos);

		try {
			gcsService.deletePhotoToGcs(selecetedPhotos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
