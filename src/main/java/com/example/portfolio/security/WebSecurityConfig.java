package com.example.portfolio.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// csrf 비활성화
			.csrf(
				(csrf) -> csrf
							.disable()
			)
			.cors(
				(cors) -> cors
							.configurationSource(corsConfigurationSource())
			)
			.authorizeHttpRequests(
				(auth) -> auth
							// 특정 요청 보안 설정
							.requestMatchers("/Admin/**").authenticated()
							// permitAll() : 인증 없이 접근 허용 
							.anyRequest().permitAll()	
			)
			.formLogin(
				(form) -> form
					
								// 로그인 성공 시 핸들러
//								.successHandler(loginSuccessHandler())
								.defaultSuccessUrl("http://localhost:9090/Admin/ManageImages", true)
								.permitAll()
			);
		return http.build();
	}
	
	// 로그인 성공 시 핸들러
	@Bean
	public AuthenticationSuccessHandler loginSuccessHandler() {
		return (request, response, authentication) -> {
			// 프론트 리다이렉트
			response.sendRedirect("http://localhost:9090/Admin/ManageImages");
		};
	}
	
	// CORS 설정
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true); // 쿠키 허용
		config.setAllowedOrigins(List.of("http://localhost:9090")); // 프론트 port
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		
		// url 경로마다 cors 적용 가능
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config); // 동일하게 적용 
		return source;
	}

	// 암호화 알고리즘
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}