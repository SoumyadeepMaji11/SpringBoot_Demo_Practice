package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableCaching
public class DemoApplication {

	@Autowired
	private PasswordEncoder encoder;

	@Bean
	CommandLineRunner run(UserRepository repo, PasswordEncoder encoder) {
		return args -> {
			if (repo.findByUsername("admin").isEmpty()) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(encoder.encode("admin"));
				admin.setRole("ROLE_ADMIN");
				repo.save(admin);
			}
			if (repo.findByUsername("user").isEmpty()) {
				User user = new User();
				user.setUsername("user");
				user.setPassword(encoder.encode("user"));
				user.setRole("ROLE_USER");
				repo.save(user);
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
