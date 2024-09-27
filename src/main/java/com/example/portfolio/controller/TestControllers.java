package com.example.portfolio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestControllers {

	@GetMapping("/")
	public String main() {
		return "hello, world2";
	}
}
