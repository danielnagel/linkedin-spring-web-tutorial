package linkedin.web;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(DirtySecretsRestController.class)
public class DirtySecretsRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DirtySecretsRepository repository;

    @Test
    public void shouldSaveSecrets() throws Exception {
        DirtySecret secret = new DirtySecret(UUID.randomUUID(), "Doug", "Ex Alcoholic");
        when(repository.save(any(DirtySecret.class))).thenReturn(secret);

        this.mockMvc.perform(
            MockMvcRequestBuilders.post("/api/dirty-secrets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Doug\", \"secret\":\"Ex Alcoholic\"}")
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Doug"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.secret").value("Ex Alcoholic"));
    }
}
