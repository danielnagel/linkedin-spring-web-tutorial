package linkedin.web;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
public class DirtySecretsRepositoryTests {

    @Autowired
    private DirtySecretsRepository repository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Test
    public void shouldSaveSecrets() {
        // create a new secret
        var secret = new DirtySecret();
        secret.setName("test");
        secret.setSecret("blabla");

        // save the secret
        var savedSecret = this.repository.save(secret);

        // assert that the secret was saved
        assertNotNull(savedSecret.getId() != null);
    }
    
}
