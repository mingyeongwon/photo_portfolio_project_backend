package com.example.portfolio.security;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(
				(csrfConfig) -> csrfConfig
										.disable()
			)
			.authorizeHttpRequests(
				(auth) -> auth
							.requestMatchers("/Admin/**").authenticated()
							.anyRequest().permitAll()
			)
			.formLogin(
				(form) -> form
								.loginPage("/Adminlogin")
								.usernameParameter("id")
								.loginProcessingUrl("/api/login")
								.defaultSuccessUrl("/Admin/ManageImages")
								.permitAll()
			)
			.logout(
				(logoutConfig) -> logoutConfig
												.logoutSuccessUrl("/")
			);

		return http.build();
	}

//	@Bean
//	public UserDetailsService userDetailsService() {
//		UserDetails userDetails = createNewUser("user", "password");
//
//		return new InMemoryUserDetailsManager(userDetails);
//	}
//
//	private UserDetails createNewUser(String username, String password) {
//		Function<String, String> passwordEncoder
//				= input -> passwordEncoder().encode(input);
//
//		UserDetails userDetails = User.builder()
//									.username(username)
//									.password(password)
//									.passwordEncoder(str -> passwordEncoder().encode(str))
//									.build();
//		return userDetails;
//	}
	
	// 암호화 알고리즘
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}