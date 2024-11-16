package com.example.portfolio.converter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WebPConverter {

    public static void main(String[] args) {
        try {
            // 절대 경로를 사용하여 cwebp 명령어 실행
            String inputFilePath = "/path/to/input.png";  // 입력 파일의 절대 경로
            String outputFilePath = "/path/to/output.webp";  // 출력 파일의 절대 경로

            // ProcessBuilder를 사용하여 외부 명령어 실행
            ProcessBuilder builder = new ProcessBuilder(
                "/usr/bin/cwebp", "-q", "8080", inputFilePath, "-o", outputFilePath
            );
            
            // 프로세스 시작
            Process process = builder.start();
            
            // 프로세스의 출력 읽기 (정상 출력)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
            // 에러 스트림 읽기 (에러가 발생할 경우)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
            
            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


