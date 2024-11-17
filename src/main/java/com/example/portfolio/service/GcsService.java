package com.example.portfolio.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.exception.CustomException;
import com.example.portfolio.exception.ErrorCode;
import com.example.portfolio.model.Photo;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class GcsService {

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    private final Storage storage;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

//    public GcsService(@Value("${spring.cloud.gcp.storage.credentials.location}") String keyFileName) throws IOException {
//        InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();
//        this.storage = StorageOptions.newBuilder()
//                .setCredentials(GoogleCredentials.fromStream(keyFile))
//                .build()
//                .getService();
//    }
    public GcsService() {
        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            this.storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize GCS credentials", e);
        }
    }


    public String uploadWebpFile(MultipartFile multipartFile, Long projectId) {
        try {
            // 임시 파일 생성
            String uuid = UUID.randomUUID().toString();
            String objectName = projectId + "/" + uuid + ".webp";
            File tempFile = File.createTempFile(uuid, ".png"); // 원본 이미지를 임시 파일로 저장
            multipartFile.transferTo(tempFile);

            // WebP 변환을 위한 출력 파일 경로 설정
            File webpFile = new File(tempFile.getParent(), uuid + ".webp");

            // cwebp 명령어 실행 (품질 80으로 설정)
            ProcessBuilder processBuilder = new ProcessBuilder(
                "cwebp", "-q", "80", tempFile.getAbsolutePath(), "-o", webpFile.getAbsolutePath()
            );
            
            // 프로세스 실행 및 결과 확인
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                throw new IOException("Failed to convert image to WebP format. Exit code: " + exitCode);
            }

            // 변환된 WebP 파일을 GCS에 업로드
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                    .setContentType("image/webp")
                    .build();
            
            // WebP 파일을 GCS에 업로드
            byte[] webpBytes = Files.readAllBytes(webpFile.toPath());
            storage.create(blobInfo, webpBytes);

            // 임시 파일 삭제
            tempFile.delete();
            webpFile.delete();

            return "https://storage.googleapis.com/" + bucketName + "/" + objectName;

        } catch (IOException e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.STORAGE_IO_ERROR,
                    "Failed to upload file: " + e.getMessage()
            );
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.STORAGE_IO_ERROR,
                    "File upload failed due to async exception: " + e.getMessage()
            );
        }
    }
    
//    private BufferedImage resizeImage(BufferedImage originalImage) {
//        int targetWidth = 1024; // 적절한 크기로 조정
//        int targetHeight = (int) (originalImage.getHeight() * ((double) targetWidth / originalImage.getWidth()));
//        
//        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
//        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
//        
//        Graphics2D graphics2D = resizedImage.createGraphics();
//        graphics2D.drawImage(resultingImage, 0, 0, null);
//        graphics2D.dispose();
//        
//        return resizedImage;
//    }

    // 썸네일 파일 삭제
    public void deleteThumbnailFile(String thumbnailUrl) {
        String objectName = getObjectNameFromUrl(thumbnailUrl);
        Blob blob = storage.get(bucketName, objectName);

        if (blob != null) {
            storage.delete(bucketName, objectName);
        } else {
            throw new CustomException(
                    HttpStatus.NOT_FOUND,
                    ErrorCode.STORAGE_FILE_NOT_FOUND,
                    "Blob not found: " + objectName
            );
        }
    }

    // photo 파일 삭제
    public void deletePhotoToGcs(List<Photo> photos) {
        List<String> urls = photos.stream()
                .map(Photo::getImageUrl)
                .collect(Collectors.toList());

        for (String url : urls) {
            String objectName = getObjectNameFromUrl(url);
            Blob blob = storage.get(bucketName, objectName);

            if (blob != null) {
                storage.delete(bucketName, objectName);
            } else {
                throw new CustomException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.STORAGE_FILE_NOT_FOUND,
                        "File not found in storage: " + objectName
                );
            }
        }
    }

    // url로 object 이름 가져오는 메서드
    public String getObjectNameFromUrl(String url) {
        int index = url.indexOf("minography_gcs/") + "minography_gcs/".length();
        return url.substring(index);
    }
}
