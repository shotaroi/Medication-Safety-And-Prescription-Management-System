package com.shotaroi.medsafety;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base for integration tests. Uses H2 in-memory by default (application-test.yml).
 * For PostgreSQL with Testcontainers, extend IntegrationTestBasePostgres instead.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationTestBase {
}
