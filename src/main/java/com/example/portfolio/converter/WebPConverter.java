package com.example.portfolio.converter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class WebPConverter {
    public static void main(String[] args) {
        String inputFilePath = "input.png";  // 변환할 입력 파일 경로
        String bucketName = "your-bucket-name";  // GCS 버킷 이름
        String projectId = "your-project-id";  // 프로젝트 ID

        try {
            // WebP 변환
            BufferedImage inputImage = ImageIO.read(new File(inputFilePath)); // 입력 이미지 읽기

            // WebP 형식으로 변환하기
            ByteArrayOutputStream webpOutputStream = new ByteArrayOutputStream();
            ImageIO.write(inputImage, "WEBP", webpOutputStream);
            byte[] webpBytes = webpOutputStream.toByteArray();

            // 고유한 파일 이름 생성
            String objectName = projectId + "/" + UUID.randomUUID().toString() + ".webp";

            // GCS 업로드
            uploadToGCS(webpBytes, bucketName, objectName);

            System.out.println("WebP image uploaded to: https://storage.googleapis.com/" + bucketName + "/" + objectName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadToGCS(byte[] webpBytes, String bucketName, String objectName) {
        // Google Cloud Storage 클라이언트 생성
        Storage storage = StorageOptions.getDefaultInstance().getService();

        // Blob 정보 생성
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType("image/webp")
                .build();

        // GCS에 업로드
        Blob blob = storage.create(blobInfo, webpBytes);
    }
}
