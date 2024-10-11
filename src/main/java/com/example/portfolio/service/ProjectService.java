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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.exception.CustomException;
import com.example.portfolio.exception.ErrorCode;
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
	
	@Value("${spring.cloud.gcp.storage.project-id}") 
    private String projectId;

	@Value("${spring.cloud.gcp.storage.credentials.location}") 
    private String keyFileName;
	
	@Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;
	
	public String uploadImageToGCS(MultipartFile multipartFile,String projectName) throws IOException {
		// Google Cloud 인증에 사용되는 서비스 계정 키 파일을 스트림 형태로 읽어야 동작
		//fromStream() 메소드가 InputStream을 매개변수로 받기 때문에 키 파일을 스트림 형태로 읽어와야함
		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
		String uuid = UUID.randomUUID().toString(); 
		String extension = multipartFile.getContentType();
		String objectName = projectName+"/"+uuid+"."+extension.split("/")[1];

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(extension)
                .build();
        
        //이미지 데이터를 클라우드에 저장 
        storage.createFrom(blobInfo, multipartFile.getInputStream());
        return "https://storage.googleapis.com/"+ bucketName+"/"+objectName;
	}
	
	// 프로젝트 생성
	@Transactional
	public void createProject(ProjectCreateDto projectCreateDto) {
		
		// 프로젝트 객체 생성
		Project project = new Project();
		project.setId(projectCreateDto.getId());
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
		
		for(int i =0; i < multipartFiles.length; i++) {
			try {
				Photo photo = new Photo();
				MultipartFile multipartFile = multipartFiles[i];
				String url = uploadImageToGCS(multipartFile, savedProject.getTitle());
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
	
	// 프로젝트 업데이트
	@Transactional
	public void updateProject(ProjectUpdateDto projectUpdateDto) {
		
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
		
		// 새로 받아온 사진들
		MultipartFile[] multipartFiles = projectUpdateDto.getMultipartFiles();
		
		// 프로젝트 아이디로 맞는 사진 전체 불러오기
		List<Photo> existingPhotos = photoRepository.findByProjectId(projectUpdateDto.getId());
		
		// 새로운 사진 저장 및 이미있는 사진 건너뛰기
		for(MultipartFile multipartFile : multipartFiles) {
			Photo newPhoto = createPhoto(multipartFile, projectUpdateDto.getId());
			
			// 기존에 없는 사진만 통과
			if(existingPhotos.stream().noneMatch(p -> p.equals(newPhoto))) {
				
				// 새로운 사진 저장 & gcs 저장
				try {
					String url = uploadImageToGCS(multipartFile, projectUpdateDto.getTitle());
					newPhoto.setImageUrl(url);
					newPhoto.setImgoname(multipartFile.getOriginalFilename());
					newPhoto.setImgtype(multipartFile.getContentType());
					photoRepository.save(newPhoto);
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
		
		// 삭제된 사진 DB 삭제 및 gcs 삭제
		List<Photo> deletePhotos = existingPhotos.stream()
        .filter(existingPhoto -> Arrays.stream(multipartFiles)
                .map(file -> createPhoto(file, project.getId()))
                .noneMatch(newPhoto -> newPhoto.equals(existingPhoto)))
        .collect(Collectors.toList());
			
		photoRepository.deleteAll(deletePhotos);
		
		try {
			deletePhotoToGcs(deletePhotos);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	// 프로젝트 삭제
	@Transactional
	public void deleteProject(Long id)  {
		// 이전의 조회 후 삭제하는 방식은 2번의 DB 조회가 필요해서 오버헤드가 발생하여 바로 삭제하는 방식으로 대체
		Project project = projectRepository.findById(id)
//				.orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FIND_PROJECT));
				.orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FIND_PROJECT, "이것도 에러"));
		
		projectRepository.deleteById(id);
        
		// 프로젝트 아이디에 맞는 모든 사진을 받아옴
        List<Photo> photos = photoRepository.findAllByProjectId(id);
        
		// gcs 삭제 메서드 호출
		try {
			deletePhotoToGcs(photos);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deletePhotoToGcs (List<Photo> photos) throws FileNotFoundException, IOException {
		
		//fromStream() 메소드가 InputStream을 매개변수로 받기 때문에 키 파일을 스트림 형태로 읽어와야함
		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
		
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();
        
        // 사진들에서 url만 뽑아서 다시 리스트로 만듬
        List<String> urls = photos.stream()
							        		.map(p -> p.getImageUrl())
							        		.collect(Collectors.toList());
        
        for(String url : urls) {
        	// minography_gcs가 처음 찾아지는 0 과 minography_gcs/의 길이 15를 합쳐서 15 값 저장
        	int index = url.indexOf("minography_gcs") + "minography_gcs/".length();
        	String objectName = url.substring(index); // 인덱스로 잘라서 objectName을 만들고
        	
        	Blob blob = storage.get(bucketName, objectName); // 사진이 있는지 확인
        	if(blob != null) {
        		storage.delete(bucketName, objectName); // gcs에서 삭제하는 로직
        	} else {
        		System.out.println("없음");
        	}
        }
	}
	
	private Photo createPhoto(MultipartFile file, Long projectId) {
		Photo photo = new Photo();
		photo.setImgoname(file.getOriginalFilename());
		photo.setProjectId(projectId);
		photo.setImgtype(file.getContentType());
		return photo;
	}


}
