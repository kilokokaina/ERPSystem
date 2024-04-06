package com.work.erpsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@SpringBootApplication
public class ErpSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpSystemApplication.class, args);
	}

	@GetMapping("test")
	public @ResponseBody String test(@RequestParam(value = "name", required = false) String name) {
		return Objects.nonNull(name) ? String.format("Hi, %s!", name) : "Hi, Stranger!";
	}

}
