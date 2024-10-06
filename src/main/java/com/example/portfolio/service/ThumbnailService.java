package com.example.portfolio.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.example.portfolio.dto.ThumbnailCreateDTO;
import com.example.portfolio.model.Thumbnail;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.ThumbnailRepository;
import com.google.auth.oauth2.GoogleCredentials;
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
	

	@Value("${spring.cloud.gcp.storage.credentials.location}") 
    private String keyFileName;
	
	@Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;
	
	public String uploadImageToGCS(ThumbnailCreateDTO thumbnailDto,String projectName) throws IOException {
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
	public void insertThumbnail(ThumbnailCreateDTO thumbnailCreateDTO) {
		try {
			Long projectId= 3L; //이후 수정해야 함 
			String projectName = projectRepository.findById(projectId).get().getTitle();
			String url = uploadImageToGCS(thumbnailCreateDTO, projectName);
			// 여기서 project 아이디를 먼저 저장하고 id 값을 받아와서 저장해줘야함
			Thumbnail thumbnail = new Thumbnail(url, 3L);// 저장되어 있는 값 넣어줘야함 이후에
			thumbnailRepository.save(thumbnail);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Transactional
	public void updateThumbnail(ThumbnailCreateDTO thumbnailCreateDTO, Long id) {
		// TODO: 여기서 project 아이디를 먼저 저장하고 id 값을 받아와서 저장해줘야함
		Thumbnail thumbnail = thumbnailRepository.findById(id).get();
		
		thumbnail.setImageUrl(thumbnailCreateDTO.getTimgoname());
		// 저장되어 있는 값 넣어줘야함 이후에
		thumbnail.setProjectId(thumbnail.getProjectId());
	}
	
	@Transactional
	public void deleteThumbnail(Long id) {
		Thumbnail thumbnail = thumbnailRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Thumbnail not found"));
		
		if(thumbnail.getId() != null) {
			thumbnailRepository.deleteById(id);
			System.out.println("삭제 완료");
		}
		
	}

}
