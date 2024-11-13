package com.example.portfolio.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
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

    public GcsService() {
        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            this.storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            
            // Verify WebP support at startup
            verifyWebPSupport();
        } catch (IOException e) {
            System.err.println("Failed to initialize GCS credentials: " + e.getMessage());
            throw new RuntimeException("Failed to initialize GCS credentials", e);
        }
    }

    private void verifyWebPSupport() {
        String[] formats = ImageIO.getWriterFormatNames();
        boolean hasWebP = false;
        for (String format : formats) {
            if (format.equalsIgnoreCase("webp")) {
                hasWebP = true;
                break;
            }
        }
        if (!hasWebP) {
            System.err.println("WebP support not found. Available formats: " + String.join(", ", formats));
            throw new RuntimeException("WebP support not available. Please check TwelveMonkeys ImageIO installation.");
        }
    }

    public String uploadWebpFile(MultipartFile multipartFile, Long projectId) {
        try {
            String uuid = UUID.randomUUID().toString();
            String objectName = projectId + "/" + uuid + ".webp";
            System.out.println("Starting upload process for: " + objectName);

            // Load and validate image
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            if (image == null) {
                throw new RuntimeException("Failed to load image from uploaded file.");
            }

            // Convert to WebP
            byte[] webpBytes = convertToWebP(image);
            
            // Upload to GCS
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                    .setContentType("image/webp")
                    .build();

            storage.create(blobInfo, webpBytes);
            
            String fileUrl = "https://storage.googleapis.com/" + bucketName + "/" + objectName;
            System.out.println("Successfully uploaded file to: " + fileUrl);
            return fileUrl;

        } catch (IOException e) {
            System.err.println("Failed to process or upload file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private byte[] convertToWebP(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (!writers.hasNext()) {
            throw new RuntimeException("WebP writer not available");
        }

        ImageWriter writer = writers.next();
        try {
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            
            // Optionally set WebP quality
            if (writeParam.canWriteCompressed()) {
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(0.8f); // 80% quality
            }

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(image, null, null), writeParam);
            }

            byte[] webpData = outputStream.toByteArray();
            if (webpData.length == 0) {
                throw new RuntimeException("WebP conversion resulted in empty data");
            }

            return webpData;
        } finally {
            writer.dispose();
            outputStream.close();
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
