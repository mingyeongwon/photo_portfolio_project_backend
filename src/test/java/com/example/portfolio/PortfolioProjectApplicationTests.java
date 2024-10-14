package com.example.portfolio;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.portfolio.dto.CategoryCreateDto;
import com.example.portfolio.mapper.CategoryMapper;
import com.example.portfolio.model.Category;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") 
class PortfolioProjectApplicationTests {

	@Autowired
	private MockMvc mockMvc; // MockMvc 객체 자동 주입
	
	@Test
	public void categoryMapperTest() {
	    //given
	    Category category = new Category(33L, "testCategory", null);
	 
	    //when
	    CategoryCreateDto categoryCreateDto = CategoryMapper.INSTANCE.categoryToCreateDto(category);
	 
	    //then
	    assertThat( categoryCreateDto ).isNotNull();
	    assertThat( categoryCreateDto.getId() ).isEqualTo( 33L );
	    assertThat( categoryCreateDto.getName() ).isEqualTo( "testCategory" );
	}

//	@Test
//	public void createProjectTest() throws Exception {
//	    // 프로젝트 파일
//	    MockMultipartFile projectFile = new MockMultipartFile(
//	        "multipartFiles", "projectImage.jpg", "image/jpeg", "test image".getBytes());
//
//	    // 썸네일 파일
//	    MockMultipartFile thumbnailFile = new MockMultipartFile(
//	        "multipartFile", "thumbnail.jpg", "image/jpeg", "test thumbnail".getBytes());
//
//	    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/create/project")
//	        .file(projectFile) // 프로젝트 파일 전달
//	        .file(thumbnailFile) // 썸네일 파일 전달
//	        .param("title", "새 프로젝트")
//	        .param("categoryId", "8")
//	        .param("subcategoryId", "10"))
//	        .andExpect(status().isOk()); // 성공 여부 확인
//	}
//
//	@Test
//	public void updateProjectTest() throws Exception {
//	    // 프로젝트 파일
//	    MockMultipartFile projectFile = new MockMultipartFile(
//	        "multipartFiles", "projectImage.jpg", "image/jpeg", "test image".getBytes());
//
//	    // 썸네일 파일
//	    MockMultipartFile thumbnailFile = new MockMultipartFile(
//	        "multipartFile", "thumbnail.jpg", "image/jpeg", "test thumbnail".getBytes());
//
//	    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/update/project/{id}", 2L)
//	        .file(projectFile) // 프로젝트 파일 전달
//	        .file(thumbnailFile) // 썸네일 파일 전달
//	        .param("title", "수정된 프로젝트 제목")
//	        .param("categoryId", "8")
//	        .param("subcategoryId", "10")
//	        .with(request -> {
//	            request.setMethod("PUT"); // HTTP 메서드를 PUT으로 설정
//	            return request;
//	        }))
//	        .andExpect(status().isOk()); // PUT 요청으로 성공 여부 확인
//	}


}
