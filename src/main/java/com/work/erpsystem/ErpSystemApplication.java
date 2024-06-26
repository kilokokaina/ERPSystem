package com.work.erpsystem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@SpringBootApplication
public class ErpSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpSystemApplication.class, args);
	}

	private @Value("${mail.username}") String jmsUsername;
	private @Value("${mail.password}") String jmsPassword;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.yandex.ru");
		mailSender.setPort(587);

		mailSender.setUsername(jmsUsername);
		mailSender.setPassword(jmsPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");

		return mailSender;
	}

}
