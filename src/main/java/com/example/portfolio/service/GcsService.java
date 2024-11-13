package com.example.portfolio.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public GcsService() {
        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            this.storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        } catch (IOException e) {
            System.err.println("Failed to initialize GCS credentials: " + e.getMessage());
            throw new RuntimeException("Failed to initialize GCS credentials", e);
        }
    }

    // WebP 파일 업로드 메서드
    public String uploadWebpFile(MultipartFile multipartFile, Long projectId) {
        try {
            String uuid = UUID.randomUUID().toString();
            String objectName = projectId + "/" + uuid + ".webp";

            // Load the image
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            if (image == null) {
                throw new IOException("Failed to load image from uploaded file.");
            }

            // Configure WebP writer
            ImageWriter writer = null;
            try {
                writer = ImageIO.getImageWritersByFormatName("webp").next();
            } catch (Exception e) {
                throw new IOException("No WebP writer available. Ensure TwelveMonkeys library is included.");
            }

            ImageWriteParam param = writer.getDefaultWriteParam();

            // Convert to WebP
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(image, null, null), param);
            } finally {
                writer.dispose();
            }

            // Check for conversion issues
            byte[] webpBytes = outputStream.toByteArray();
            if (webpBytes.length == 0) {
                throw new IOException("Conversion to WebP resulted in empty data.");
            }

            // Prepare for GCS upload
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                    .setContentType("image/webp")
                    .build();

            // Upload to GCS
            storage.create(blobInfo, webpBytes);
            return "https://storage.googleapis.com/" + bucketName + "/" + objectName;

        } catch (IOException e) {
            System.err.println("Failed to upload file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    // 썸네일 파일 삭제
    public void deleteThumbnailFile(String thumbnailUrl) {
        String objectName = getObjectNameFromUrl(thumbnailUrl);
        Blob blob = storage.get(bucketName, objectName);

        if (blob != null) {
            storage.delete(bucketName, objectName);
        } else {
            System.err.println("Blob not found: " + objectName);
            throw new RuntimeException("Blob not found: " + objectName);
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
                System.err.println("File not found in storage: " + objectName);
            }
        }
    }

    // url로 object 이름 가져오는 메서드
    public String getObjectNameFromUrl(String url) {
        int index = url.indexOf("minography_gcs/") + "minography_gcs/".length();
        return url.substring(index);
    }
}
