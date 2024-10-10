package com.example.portfolio.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ThumbnailCreateDto;
import com.example.portfolio.model.Project;
import com.example.portfolio.model.Thumbnail;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.ThumbnailRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import jakarta.transaction.Transactional;

@Service
public class ThumbnailService {

	// 여기서 생성자 주입을 하지 않고 필드 주입을 하는게 좋은건지?
	@Autowired
	private ThumbnailRepository thumbnailRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Value("${spring.cloud.gcp.storage.project-id}")
	private String projectId;

	@Value("${spring.cloud.gcp.storage.credentials.location}")
	private String keyFileName;

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;

	public String uploadImageToGCS(ThumbnailCreateDto thumbnailDto, String projectName) throws IOException {
		// Google Cloud 인증에 사용되는 서비스 계정 키 파일을 스트림 형태로 읽어야 동작
		// fromStream() 메소드가 InputStream을 매개변수로 받기 때문에 키 파일을 스트림 형태로 읽어와야함
		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
		String uuid = UUID.randomUUID().toString();
		String extension = thumbnailDto.getMultipartFile().getContentType();
		String objectName = projectName + "/" + uuid + "." + extension.split("/")[1];

		Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(keyFile)).build()
				.getService();

		BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).setContentType(extension).build();

		// 이미지 데이터를 클라우드에 저장
		storage.createFrom(blobInfo, thumbnailDto.getMultipartFile().getInputStream());
		return "https://storage.googleapis.com/" + bucketName + "/" + objectName;
	}

	@Transactional
	public void createThumbnail(ThumbnailCreateDto thumbnailCreateDTO, Long id) {
		try {
			String projectName = projectRepository.findById(id).get().getTitle();
			String url = uploadImageToGCS(thumbnailCreateDTO, projectName);
			Thumbnail thumbnail = new Thumbnail(url, id);
			System.out.println(url);
			thumbnailRepository.save(thumbnail);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Transactional
	public void updateThumbnail(ThumbnailCreateDto thumbnailCreateDTO, Long id,Project updatedProject) {
		try {
			MultipartFile image = thumbnailCreateDTO.getMultipartFile();
			//사진을 먼저 삭제 한 후 다시 insert 
			deleteThumbnail(id);
			
			String url = uploadImageToGCS(thumbnailCreateDTO, updatedProject.getTitle());
			thumbnailCreateDTO.setTimgoname(image.getOriginalFilename());
			thumbnailCreateDTO.setTimgtype(image.getContentType());

			Thumbnail thumbnail = new Thumbnail();
			thumbnail.setImageUrl(url);
			thumbnail.setProjectId(id);

			thumbnailRepository.save(thumbnail);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Transactional
	public void deleteThumbnail(Long id) throws FileNotFoundException, IOException {
		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();

		Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(keyFile)).build()
				.getService();

		Thumbnail thumbnail = thumbnailRepository.findByProjectId(id);

		// 썸네일이 없는 경우 예외 처리
		if (thumbnail == null) {
			throw new RuntimeException("Thumbnail not found");
		}

		String url = thumbnail.getImageUrl();
		int index = url.indexOf("minography_gcs/") + "minography_gcs/".length();

		// 인덱스부터 끝까지 substring으로 추출
		String objectName = url.substring(index);
		System.out.println(objectName);

		// 썸네일 ID가 있으면 삭제
		if (thumbnail.getId() != null) {
			thumbnailRepository.deleteById(thumbnail.getId());
			System.out.println("삭제 완료");
		}

		// GCS에서 파일 삭제
		Blob blob = storage.get(bucketName, objectName);
		if (blob != null) {
			storage.delete(bucketName, objectName);
		} else {
			System.out.println("파일이 없습니다.");
		}
	}

	@Transactional
	public List<ThumbnailCreateDto> getThumbnailByCategory(Long categoryId, Long subCategoryId) {
		List<Project> projects = new ArrayList<>();

		if (subCategoryId == null) {
			System.out.println("null");
			// categoryId로 프로젝트 찾아오기
			projects = projectRepository.findByCategory_Id(categoryId);
		} else {
			System.out.println("not null");
			// subcategoryId로 프로젝트 찾아오기
			projects = projectRepository.findBySubCategory_Id(subCategoryId);
		}

		List<Thumbnail> thumbnails = new ArrayList<>();
		for (Project project : projects) {
			// 프로젝트 아이디로 썸네일 찾아오기
			Thumbnail thumbnail = thumbnailRepository.findByProjectId(project.getId());
			if (thumbnail != null) {
				thumbnails.add(thumbnail);
			}
		}

		return thumbnails.stream().map(this::thumbnailEntityToDto).toList();
	}

	// Entity -> DTO 변환
	private ThumbnailCreateDto thumbnailEntityToDto(Thumbnail thumbnail) {
		ThumbnailCreateDto thumbnailCreateDto = new ThumbnailCreateDto();
		thumbnailCreateDto.setId(thumbnail.getId());
		thumbnailCreateDto.setTimgsname(thumbnail.getImageUrl());
		thumbnailCreateDto.setProjectId(thumbnail.getProjectId());
		return thumbnailCreateDto;
	}

}
