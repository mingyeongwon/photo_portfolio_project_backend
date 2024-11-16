package com.example.portfolio.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebPConverter {
	public static void main(String[] args) {
        // 입력 및 출력 파일 경로를 /tmp/ 디렉터리로 설정
        String inputFile = "/tmp/input.png";
        String outputFile = "/tmp/output.webp";

        // cwebp 명령어 경로
        String cwebpPath = "/usr/bin/cwebp"; 

        ProcessBuilder processBuilder = new ProcessBuilder(cwebpPath, inputFile, "-o", outputFile);
        processBuilder.redirectErrorStream(true);

        try {
            // cwebp 실행
            Process process = processBuilder.start();

            // 출력 로그 확인
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
     
            // 프로세스 종료 코드 확인
            int exitCode = process.waitFor();
           
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}