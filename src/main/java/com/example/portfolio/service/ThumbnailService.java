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

import com.example.portfolio.dto.ProjectUpdateDto;
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
	
	public String uploadImageToGCS(ThumbnailCreateDto thumbnailDto,String projectName) throws IOException {
		// Google Cloud 인증에 사용되는 서비스 계정 키 파일을 스트림 형태로 읽어야 동작
		//fromStream() 메소드가 InputStream을 매개변수로 받기 때문에 키 파일을 스트림 형태로 읽어와야함
		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
		String uuid = UUID.randomUUID().toString(); 
		String extension = thumbnailDto.getMultipartFile().getContentType();
		String objectName = projectName+"/"+uuid+"."+extension.split("/")[1];

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(extension)
                .build();
        
        //이미지 데이터를 클라우드에 저장 
        storage.createFrom(blobInfo, thumbnailDto.getMultipartFile().getInputStream());
        return "https://storage.googleapis.com/"+ bucketName+"/"+objectName;
	}
	
	@Transactional
	public void createThumbnail(ThumbnailCreateDto thumbnailCreateDTO,Long id) {
		try {
			String projectName = projectRepository.findById(id).get().getTitle();
			String url = uploadImageToGCS(thumbnailCreateDTO, projectName);
			Thumbnail thumbnail = new Thumbnail(url, id);
			thumbnailRepository.save(thumbnail);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Transactional
	public void updateThumbnail(ThumbnailCreateDto thumbnailCreateDTO, Long id,Project updatedProject) {
		// TODO: 여기서 project 아이디를 먼저 저장하고 id 값을 받아와서 저장해줘야함
		MultipartFile image = thumbnailCreateDTO.getMultipartFile();
		thumbnailCreateDTO.setTimgoname(image.getOriginalFilename());
		thumbnailCreateDTO.setTimgtype(image.getContentType());
		Thumbnail thumbnail = thumbnailRepository.findById(id).get();
		
		thumbnail.setImageUrl(thumbnailCreateDTO.getTimgoname());
		// 저장되어 있는 값 넣어줘야함 이후에
		thumbnail.setProjectId(updatedProject.getId());
		
		thumbnailRepository.save(thumbnail);
	}

	
	@Transactional
	public void deleteThumbnail(Long id) throws FileNotFoundException, IOException {
		//fromStream() 메소드가 InputStream을 매개변수로 받기 때문에 키 파일을 스트림 형태로 읽어와야함
		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
		
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();
        
		Thumbnail thumbnail = thumbnailRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Thumbnail not found"));
		String url= thumbnail.getImageUrl();
		int index = url.indexOf("minography_gcs/") + "minography_gcs/".length();

		//인덱스부터 끝까지 substring으로 추출
		String objectName = url.substring(index);
		System.out.println(objectName);
		if(thumbnail.getId() != null) {
			thumbnailRepository.deleteById(id);
			System.out.println("삭제 완료");
		}
		  Blob blob = storage.get(bucketName, objectName);
		  if(blob!=null) {
				storage.delete(bucketName, objectName);
		  }else {
			  System.out.println("없음");
		  }	   
		
	}
	@Transactional
	public List<Thumbnail> getThumbnail(Long categoryId) {
		//categoryId로 프로젝트 찾아오기
		 List<Project> projects = projectRepository.findByCategory_Id(categoryId);

		    List<Thumbnail> thumbnails = new ArrayList<>();
		    for (Project project : projects) {
		    	//프로젝트 아이디로 썸네일 찾아오기
		        thumbnails.addAll(thumbnailRepository.findByProjectId(project.getId()));
		    }

		return thumbnails.stream().toList();
	}

}
