package com.example.portfolio.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ThumbnailCreateDTO;

import com.example.portfolio.model.Thumbnail;
import com.example.portfolio.repository.CategoryRepository;
import com.example.portfolio.repository.SubCategoryRepository;
import com.example.portfolio.repository.ThumbnailRepository;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;



@Service
public class ThumbnailService {
	// 여기서 생성자 주입을 하지 않고 필드 주입을 하는게 좋은건지?
	@Autowired
	private ThumbnailRepository thumbnailRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private SubCategoryRepository subCategoryRepository;

    @Value("${minography-gcs}") // application.yml에 써둔 bucket 이름
    private String bucketName;
    
	public String uploadImageToGCS(MultipartFile file,String category, String subCategory) throws IOException {
	    // GCS 설정
	    Storage storage = StorageOptions.getDefaultInstance().getService();
	    // 이미지 업로드 관련 부분    
        String uuid = UUID.randomUUID().toString(); // Google Cloud Storage에 저장될 파일 이름

	    // 이미지 업로드
	    BlobInfo blobInfo = storage.create(
	        BlobInfo.newBuilder(bucketName, uuid).build(),
	        file.getBytes()
	    );    
	    // 파일 URL 반환
	    return String.format("https://storage.googleapis.com/%s/%s/%s/%s", bucketName,category, subCategory, uuid);
	}
	
	public void insertThumbnail(ThumbnailCreateDTO thumbnailCreateDTO) {
		
		try {
			MultipartFile image = thumbnailCreateDTO.getMultipartFile();
			
			String category = categoryRepository.findById(8L).get().getName();
			String subCategory = subCategoryRepository.findById(10L).get().getName();

			String url = uploadImageToGCS(image,category,subCategory);
			// 여기서 project 아이디를 먼저 저장하고 id 값을 받아와서 저장해줘야함
			Thumbnail thumbnail = new Thumbnail();
		
			thumbnail.setImageUrl(url);
			// 저장되어 있는 값 넣어줘야함 이후에
			thumbnail.setProjectId(1L);
			thumbnailRepository.save(thumbnail);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
