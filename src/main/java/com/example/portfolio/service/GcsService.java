package com.example.portfolio.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class GcsService {

	@Value("${spring.cloud.gcp.storage.project-id}")
	private String projectId;

	@Value("${spring.cloud.gcp.storage.credentials.location}")
	private String keyFileName;

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;

	//GCS 업로드 메소드
	public String uploadFile(MultipartFile multipartFile, String projectName) throws IOException {
		InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
		String uuid = UUID.randomUUID().toString();
		String extension = multipartFile.getContentType();
		String objectName = projectName + "/" + uuid + "." + extension.split("/")[1];

		Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(keyFile)).build()
				.getService();

		BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).setContentType(extension).build();

		storage.createFrom(blobInfo, multipartFile.getInputStream());
		return "https://storage.googleapis.com/" + bucketName + "/" + objectName;
	}
	
    // 파일 삭제 로직 추가
    public void deleteFile(String objectName) throws IOException {
        InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();

        Blob blob = storage.get(bucketName, objectName);
        if (blob != null) {
            storage.delete(bucketName, objectName);
        } else {
            System.out.println("Blob not found: " + objectName);
		}
	}

    //url로 object 이름 가져오는 메소드
	public String getObjectNameFromUrl(String url) {
		int index = url.indexOf("minography_gcs/") + "minography_gcs/".length();
		return url.substring(index);
	}
}
