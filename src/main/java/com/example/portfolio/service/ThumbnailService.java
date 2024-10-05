package com.example.portfolio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.portfolio.dto.ThumbnailCreateDTO;
import com.example.portfolio.model.Thumbnail;
import com.example.portfolio.repository.ThumbnailRepository;

import jakarta.transaction.Transactional;

@Service
public class ThumbnailService {
	
	// 여기서 생성자 주입을 하지 않고 필드 주입을 하는게 좋은건지?
	@Autowired
	private ThumbnailRepository thumbnailRepository;
	
	@Transactional
	public void insertThumbnail(ThumbnailCreateDTO thumbnailCreateDTO) {
		// TODO: 여기서 project 아이디를 먼저 저장하고 id 값을 받아와서 저장해줘야함
		Thumbnail thumbnail = new Thumbnail();
	
		thumbnail.setImageUrl(thumbnailCreateDTO.getTimgoname());
		// 저장되어 있는 값 넣어줘야함 이후에
		thumbnail.setProjectId(3L);
		
		thumbnailRepository.save(thumbnail);
	}

	@Transactional
	public void updateThumbnail(ThumbnailCreateDTO thumbnailCreateDTO, Long id) {
		// TODO: 여기서 project 아이디를 먼저 저장하고 id 값을 받아와서 저장해줘야함
		Thumbnail thumbnail = thumbnailRepository.findById(id).get();
		
		thumbnail.setImageUrl(thumbnailCreateDTO.getTimgoname());
		// 저장되어 있는 값 넣어줘야함 이후에
		thumbnail.setProjectId(thumbnail.getProjectId());
	}
	
	@Transactional
	public void deleteThumbnail(Long id) {
		Thumbnail thumbnail = thumbnailRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Thumbnail not found"));
		
		if(thumbnail.getId() != null) {
			thumbnailRepository.deleteById(id);
			System.out.println("삭제 완료");
		}
		
	}

}
