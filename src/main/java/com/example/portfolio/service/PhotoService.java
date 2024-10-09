package com.example.portfolio.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.model.Photo;
import com.example.portfolio.model.Project;
import com.example.portfolio.repository.PhotoRepository;

@Service
public class PhotoService {

    @Autowired
    private GcsService gcsService;

    @Autowired
    private PhotoRepository photoRepository;

    public void createPhotos(ProjectCreateDto projectCreateDto, Project savedProject) {
        MultipartFile[] multipartFiles = projectCreateDto.getMultipartFiles();

        for (MultipartFile multipartFile : multipartFiles) {
            try {
                Photo photo = new Photo();
                String url = gcsService.uploadFile(multipartFile, savedProject.getTitle());
                photo.setImageUrl(url);
                photo.setImgoname(multipartFile.getOriginalFilename());
                photo.setImgtype(multipartFile.getContentType());
                photo.setProjectId(savedProject.getId());
                photoRepository.save(photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updatePhotos(ProjectUpdateDto projectUpdateDto) {
        MultipartFile[] multipartFiles = projectUpdateDto.getMultipartFiles();
        List<Photo> existingPhotos = photoRepository.findByProjectId(projectUpdateDto.getId());

        for (MultipartFile multipartFile : multipartFiles) {
            Photo newPhoto = createPhoto(multipartFile, projectUpdateDto.getId());

            if (existingPhotos.stream().noneMatch(p -> p.equals(newPhoto))) {
                try {
                    String url = gcsService.uploadFile(multipartFile, projectUpdateDto.getTitle());
                    newPhoto.setImageUrl(url);
                    newPhoto.setImgoname(multipartFile.getOriginalFilename());
                    newPhoto.setImgtype(multipartFile.getContentType());
                    photoRepository.save(newPhoto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        List<Photo> deletePhotos = existingPhotos.stream()
                .filter(existingPhoto -> !isPhotoInFiles(existingPhoto, multipartFiles, projectUpdateDto.getId()))
                .collect(Collectors.toList());

        photoRepository.deleteAll(deletePhotos);

        try {
            gcsService.deletePhotoToGcs(deletePhotos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deletePhotosByProjectId(Long projectId) {
        List<Photo> photos = photoRepository.findAllByProjectId(projectId);
        photoRepository.deleteAll(photos);

        try {
            gcsService.deletePhotoToGcs(photos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isPhotoInFiles(Photo existingPhoto, MultipartFile[] multipartFiles, Long projectId) {
        for (MultipartFile multipartFile : multipartFiles) {
            Photo newPhoto = createPhoto(multipartFile, projectId);
            if (existingPhoto.equals(newPhoto)) {
                return true;
            }
        }
        return false;
    }

    private Photo createPhoto(MultipartFile file, Long projectId) {
        Photo photo = new Photo();
        photo.setImgoname(file.getOriginalFilename());
        photo.setProjectId(projectId);
        photo.setImgtype(file.getContentType());
        return photo;
    }
}
