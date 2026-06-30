package com.minimarket;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class MinimarketApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void main_debeEjecutarSpringApplication() {
		try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
			String[] args = {"--server.port=0"};

			MinimarketApplication.main(args);

			springApplication.verify(() -> SpringApplication.run(MinimarketApplication.class, args));
		}
	}
}
