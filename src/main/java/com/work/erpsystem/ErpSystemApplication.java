package com.work.erpsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Properties;

@SpringBootApplication
public class ErpSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpSystemApplication.class, args);
	}

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.yandex.ru");
		mailSender.setPort(587);

		mailSender.setUsername("nikolaushki@yandex.ru");
		mailSender.setPassword("bfyosntikevlwmpc");

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");

		return mailSender;
	}

//	@Bean
//	public CommandLineRunner cmd() {
//		return args -> {
//			JavaMailSender mailSender = javaMailSender();
//
//			SimpleMailMessage message = new SimpleMailMessage();
//			message.setFrom("nikolaushki@yandex.ru");
//			message.setTo("nikolaushki@yandex.ru");
//			message.setText("This is simple text for testing mailSender");
//
//			mailSender.send(message);
//		};
//	}

}
