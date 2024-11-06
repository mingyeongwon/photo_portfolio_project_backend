package com.example.portfolio.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.model.Photo;
import com.example.portfolio.repository.PhotoRepository;
import com.example.portfolio.repository.ProjectRepository;

@Service
public class PhotoService {

	private final GcsService gcsService;
	private final PhotoRepository photoRepository;
	private final ProjectRepository projectRepository;
	private final ExecutorService executorService = Executors.newFixedThreadPool(4); // 스레드 풀을 필드에 선언하여 재사용

	//생성자 주입
	public PhotoService(GcsService gcsService, PhotoRepository photoRepository, ProjectRepository projectRepository) {
		this.gcsService = gcsService;
		this.photoRepository = photoRepository;
		this.projectRepository = projectRepository;
	}


    public void createPhotos(ProjectCreateDto projectCreateDto, Long projectId) {
        MultipartFile[] multipartFiles = projectCreateDto.getPhotoMultipartFiles();
        List<CompletableFuture<Photo>> futures = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            // CompletableFuture를 사용하여 비동기 처리
            CompletableFuture<Photo> future = CompletableFuture.supplyAsync(() -> {
                Photo photo = new Photo();
                String url = gcsService.uploadWebpFile(multipartFile, projectId);
                photo.setImageUrl(url);
                photo.setImgoname(multipartFile.getOriginalFilename());
                photo.setImgtype("image/webp"); 
                photo.setProjectId(projectId);
                return photo;
            }, executorService);

            futures.add(future);
        }

        List<Photo> photos = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        // 한번에 저장
        photoRepository.saveAll(photos);
    }

	// 사진 업데이트
	public void updatePhotos(ProjectUpdateDto projectUpdateDto) {
		MultipartFile[] multipartFiles = projectUpdateDto.getPhotoMultipartFiles();

		for (MultipartFile multipartFile : multipartFiles) {
			Photo newPhoto = createPhoto(multipartFile, projectUpdateDto.getId());
			String url = gcsService.uploadWebpFile(multipartFile, projectUpdateDto.getId());
			newPhoto.setImageUrl(url);
			newPhoto.setImgoname(multipartFile.getOriginalFilename());
			newPhoto.setImgtype(multipartFile.getContentType());
			photoRepository.save(newPhoto);
		
		}
	}

	// 사진 삭제
	public void deletePhotosByProjectId(Long projectId) {
		List<Photo> photos = photoRepository.findAllByProjectId(projectId);
		photoRepository.deleteAll(photos);
		gcsService.deletePhotoToGcs(photos);
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
	
	// edit에서 삭제
	public void deleteSelectedPhotos(List<Long> deletedPhotoIds) {
		List<Photo> selecetedPhotos = photoRepository.findAllById(deletedPhotoIds);
		photoRepository.deleteAll(selecetedPhotos);
		gcsService.deletePhotoToGcs(selecetedPhotos);
	}
}
