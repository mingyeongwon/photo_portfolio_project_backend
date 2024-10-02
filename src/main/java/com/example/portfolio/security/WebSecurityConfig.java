package com.example.portfolio.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
			.authorizeHttpRequests(
				(auth) -> auth
							.requestMatchers("/Admin/AdminUpload", "/Admin/ManageCategory", 
									"/Admin/ManageImages").authenticated()
							.anyRequest().permitAll()
			)
			.formLogin(
				(form) -> form
			//					withDefaults()
								.loginPage("/Admin/Login")
//								.loginProcessingUrl(null)
								.defaultSuccessUrl("/Admin/ManageImages")
								.permitAll()
				);
//			.logout((logout) -> logout.permitAll());

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails userDetails = createNewUser("user", "password");

		return new InMemoryUserDetailsManager(userDetails);
	}

	private UserDetails createNewUser(String username, String password) {
		Function<String, String> passwordEncoder
				= input -> passwordEncoder().encode(input);

		UserDetails userDetails = User.builder()
									.username(username)
									.password(password)
									.passwordEncoder(str -> passwordEncoder().encode(str))
									.build();
		return userDetails;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}