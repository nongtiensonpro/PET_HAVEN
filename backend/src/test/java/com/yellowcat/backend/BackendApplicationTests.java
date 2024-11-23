package com.yellowcat.backend;

import com.yellowcat.backend.repository.HoadonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Testcontainers
class BackendApplicationTests {
	@Autowired
	HoadonRepository hoadonRepository;

	@Container
	private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

	@DynamicPropertySource
	static void setupPostgres(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);

		registry.add("spring.flyway.url", postgres::getJdbcUrl);
		registry.add("spring.flyway.user", postgres::getUsername);
		registry.add("spring.flyway.password", postgres::getPassword);
	}

	@Test
	void contextLoads() {
	}

	@Test
	public void testFindTopCustomers() {
		LocalDate startDate = LocalDate.of(2024, 1, 1);
		LocalDate endDate = LocalDate.of(2024, 12, 31);

		List<Object[]> result = hoadonRepository.findTopCustomers(startDate,endDate);

		for (Object[] row : result) {
			String email = (String) row[0];
			Long totalAppointments = ((Number) row[1]).longValue(); // Hoặc (Long) row[1] tùy vào kiểu dữ liệu
			Double totalAmount = ((Number) row[2]).doubleValue(); // Hoặc (BigDecimal).doubleValue()

			System.out.println("Email: " + email);
			System.out.println("Tổng số lịch hẹn: " + totalAppointments);
			System.out.println("Tổng số tiền: " + totalAmount);
		}
	}
}
