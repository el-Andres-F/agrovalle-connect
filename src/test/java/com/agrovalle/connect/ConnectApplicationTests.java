package com.agrovalle.connect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"spring.datasource.url=${TEST_DB_URL:jdbc:h2:mem:agrovalle-context;MODE=PostgreSQL;DB_CLOSE_DELAY=-1}",
	"spring.datasource.username=${TEST_DB_USERNAME:sa}",
	"spring.datasource.password=${TEST_DB_PASSWORD:}",
	"spring.sql.init.mode=never"
})
class ConnectApplicationTests {

	@Test
	void elContextoDeSpringCarga() {
	}

}
