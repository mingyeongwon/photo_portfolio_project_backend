package com.example.portfolio.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.model.Photo;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

//구글 클라우드 스토리지 관련 로직
@Service
public class GcsService {
	
	@Value("${spring.cloud.gcp.storage.project-id}")
	private String projectId;

	@Value("${spring.cloud.gcp.storage.credentials.location}")
	private String keyFileName;

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;
	
	// GCS 업로드 메소드
	public String uploadFile(MultipartFile multipartFile, Long projectId) throws IOException {
		// Google Cloud 인증에 사용되는 서비스 계정 키 파일을 스트림 형태로 읽어야 동작
		// fromStream() 메소드가 InputStream을 매개변수로 받기 때문에 키 파일을 스트림 형태로 읽어와야함		
		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
		String uuid = UUID.randomUUID().toString();
		String extension = multipartFile.getContentType();
		String objectName = projectId + "/" + uuid + "." + extension.split("/")[1];

		Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(keyFile)).build()
				.getService();

		BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).setContentType(extension).build();

		storage.createFrom(blobInfo, multipartFile.getInputStream());
		return "https://storage.googleapis.com/" + bucketName + "/" + objectName;
	}

	// 썸네일 파일 삭제 
	public void deleteThumbnailFile(String thumbnailUrl) throws IOException {
		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
		Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(keyFile)).build()
				.getService();
		
		String objectName = getObjectNameFromUrl(thumbnailUrl);
		
		Blob blob = storage.get(bucketName, objectName);
		if (blob != null) {
			storage.delete(bucketName, objectName);
		} else {
			System.out.println("Blob not found: " + objectName);
		}
	}
	
	// photo 파일 삭제
	public void deletePhotoToGcs(List<Photo> photos) throws FileNotFoundException, IOException {

		// fromStream() 메소드가 InputStream을 매개변수로 받기 때문에 키 파일을 스트림 형태로 읽어와야함
		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();

		Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(keyFile)).build()
				.getService();

		// 사진들에서 url만 뽑아서 다시 리스트로 만듬
		List<String> urls = photos.stream().map(p -> p.getImageUrl()).collect(Collectors.toList());

		for (String url : urls) {
			// minography_gcs가 처음 찾아지는 0 과 minography_gcs/의 길이 15를 합쳐서 15 값 저장
			String objectName = getObjectNameFromUrl(url); // 인덱스로 잘라서 objectName을 만들고

			Blob blob = storage.get(bucketName, objectName); // 사진이 있는지 확인
			if (blob != null) {
				storage.delete(bucketName, objectName); // gcs에서 삭제하는 로직
			} else {
				System.out.println("없음");
			}
		}
	}

	// url로 object 이름 가져오는 메소드
	public String getObjectNameFromUrl(String url) {
		int index = url.indexOf("minography_gcs/") + "minography_gcs/".length();
		return url.substring(index);
	}
}
