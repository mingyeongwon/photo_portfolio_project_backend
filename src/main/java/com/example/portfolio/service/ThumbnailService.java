package com.example.portfolio.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ThumbnailCreateDTO;
import com.example.portfolio.model.Thumbnail;
import com.example.portfolio.repository.CategoryRepository;
import com.example.portfolio.repository.SubCategoryRepository;
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
	private CategoryRepository categoryRepository;

	@Autowired
	private SubCategoryRepository subCategoryRepository;

	@Value("${spring.cloud.gcp.storage.credentials.location}") 
    private String keyFileName;
	
	@Value("${spring.cloud.gcp.storage.bucket}") // application.yml에 써둔 bucket 이름
    private String bucketName;
	
	public String uploadImageToGCS(ThumbnailCreateDTO thumbnailDto,String category, String subCategory) throws IOException {

		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
		String uuid = UUID.randomUUID().toString(); // Google Cloud Storage에 저장될 파일 이름
		String ext = thumbnailDto.getMultipartFile().getContentType();
		String extension =ext.split("/")[1];
		String objectName = String.format( "%s/%s/%s.%s",category, subCategory, uuid,extension);

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(thumbnailDto.getTimgtype())
                .build();
        
        //이미지 데이터를 클라우드에 저장 
        Blob blob = storage.createFrom(blobInfo, thumbnailDto.getMultipartFile().getInputStream());
        return String.format("https://storage.googleapis.com/"+ bucketName+"/"+objectName);
	}
	
	public void insertThumbnail(ThumbnailCreateDTO thumbnailCreateDTO) {
		
		try {
			MultipartFile image = thumbnailCreateDTO.getMultipartFile();
			
			String category = categoryRepository.findById(8L).get().getName();
			String subCategory = subCategoryRepository.findById(10L).get().getName();

			String url = uploadImageToGCS(thumbnailCreateDTO,category,subCategory);
			// 여기서 project 아이디를 먼저 저장하고 id 값을 받아와서 저장해줘야함
			Thumbnail thumbnail = new Thumbnail();
		
			thumbnail.setImageUrl(url);
			// 저장되어 있는 값 넣어줘야함 이후에
			thumbnail.setProjectId(2L);
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
