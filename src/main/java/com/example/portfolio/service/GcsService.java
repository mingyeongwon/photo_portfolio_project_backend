package com.example.portfolio.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.exception.CustomException;
import com.example.portfolio.exception.ErrorCode;
import com.example.portfolio.model.Photo;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;


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
//	public String uploadFile(MultipartFile multipartFile, Long projectId) throws IOException {
//		// Google Cloud 인증에 사용되는 서비스 계정 키 파일을 스트림 형태로 읽어야 동작
//		// fromStream() 메소드가 InputStream을 매개변수로 받기 때문에 키 파일을 스트림 형태로 읽어와야함
//		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
//		String uuid = UUID.randomUUID().toString();
//		String extension = multipartFile.getContentType();
//		String objectName = projectId + "/" + uuid + "." + extension.split("/")[1];
//
//		Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(keyFile)).build()
//				.getService();
//
//		BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).setContentType(extension).build();
//
//		storage.createFrom(blobInfo, multipartFile.getInputStream());
//		return "https://storage.googleapis.com/" + bucketName + "/" + objectName;
//	}

	// 썸네일 파일 삭제
	public void deleteThumbnailFile(String thumbnailUrl) {
		try {
		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
			Storage storage = StorageOptions.newBuilder()
					.setCredentials(GoogleCredentials.fromStream(keyFile))
					.build()
					.getService();
			
			String objectName = getObjectNameFromUrl(thumbnailUrl);
			Blob blob = storage.get(bucketName, objectName);
			
			if (blob != null) {
				storage.delete(bucketName, objectName);
			} else {
				throw new CustomException(
						HttpStatus.NOT_FOUND, // 404
		                ErrorCode.STORAGE_FILE_NOT_FOUND,
		                "Blob not found: " + objectName
						);
			}
			
		} catch (FileNotFoundException e) {
			throw new CustomException(
					HttpStatus.NOT_FOUND,  // 404
		            ErrorCode.STORAGE_KEY_FILE_NOT_FOUND,
		            "Failed to find storage key file: " + e.getMessage()
					);
			
		} catch (IOException e) {
			throw new CustomException(
		            HttpStatus.INTERNAL_SERVER_ERROR,  // 500
		            ErrorCode.STORAGE_IO_ERROR,
		            "Storage operation failed: " + e.getMessage()
		        );
		}
	}

	// photo 파일 삭제
	public void deletePhotoToGcs(List<Photo> photos){

		// fromStream() 메소드가 InputStream을 매개변수로 받기 때문에 키 파일을 스트림 형태로 읽어와야함
		try {
			InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
			Storage storage = StorageOptions.newBuilder()
					.setCredentials(GoogleCredentials.fromStream(keyFile))
					.build()
					.getService();
			
			// 사진들에서 url만 뽑아서 다시 리스트로 만듬
			List<String> urls = photos.stream()
					.map(p -> p.getImageUrl())
					.collect(Collectors.toList());
			
			for (String url : urls) {
				// minography_gcs가 처음 찾아지는 0 과 minography_gcs/의 길이 15를 합쳐서 15 값 저장
				String objectName = getObjectNameFromUrl(url); // 인덱스로 잘라서 objectName을 만들고
				Blob blob = storage.get(bucketName, objectName); // 사진이 있는지 확인
				
				if (blob != null) {
					storage.delete(bucketName, objectName); // gcs에서 삭제하는 로직
				} else {
					throw new CustomException(
		                    HttpStatus.NOT_FOUND,
		                    ErrorCode.STORAGE_FILE_NOT_FOUND,
		                    "File not found in storage: " + objectName
		                );
				}
			}
			
		} catch (FileNotFoundException e) {
			throw new CustomException(
		            HttpStatus.NOT_FOUND,
		            ErrorCode.STORAGE_KEY_FILE_NOT_FOUND,
		            "Storage key file not found: " + e.getMessage()
		        );
		} catch (IOException e) {
			throw new CustomException(
		            HttpStatus.INTERNAL_SERVER_ERROR,
		            ErrorCode.STORAGE_IO_ERROR,
		            "Storage operation failed: " + e.getMessage()
		        );
		}
	}

	// url로 object 이름 가져오는 메소드
	public String getObjectNameFromUrl(String url) {
		int index = url.indexOf("minography_gcs/") + "minography_gcs/".length();
		return url.substring(index);
	}

	public String uploadWebpFile(MultipartFile multipartFile, Long projectId) {
		try {
			InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
			String uuid = UUID.randomUUID().toString();
			String objectName = projectId + "/" + uuid + ".webp";  // WebP 확장자로 설정
			
			// Scrimage를 사용하여 WebP 형식으로 변환 및 압축 품질, 방법, 수준 설정
			ImmutableImage image = ImmutableImage.loader().fromStream(multipartFile.getInputStream());
			
			// 품질을 80, 압축 방법을 4, 데이터 압축 수준을 9로 설정하여 WebpWriter 생성
			WebpWriter writer = WebpWriter.DEFAULT.withQ(80).withM(4).withZ(9);
			
			byte[] webpBytes = image.bytes(writer); // 설정된 옵션으로 webp 이미지 생성
			
			Storage storage = StorageOptions.newBuilder()
					.setCredentials(GoogleCredentials.fromStream(keyFile))
					.build()
					.getService();
			
			BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
					.setContentType("image/webp")
					.build();
			
			// GCS에 업로드
			storage.create(blobInfo, webpBytes);
			return "https://storage.googleapis.com/" + bucketName + "/" + objectName;
			
		}  catch (IOException e) {
			throw new CustomException(
		            HttpStatus.INTERNAL_SERVER_ERROR,
		            ErrorCode.STORAGE_IO_ERROR,
		            "Failed to upload file: " + e.getMessage()
		    );
		}
	}
 
}
