package com.roman.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ParticipanteControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void deve_criar_participante_e_retornar_201() throws Exception {
        mockMvc.perform(post("/api/v1/participantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "João Silva", "username": "joao_silva"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.username").value("joao_silva"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deve_retornar_409_quando_username_duplicado() throws Exception {
        String body = """
                {"nome": "Maria", "username": "maria_dup"}
                """;
        mockMvc.perform(post("/api/v1/participantes")
                .contentType(MediaType.APPLICATION_JSON).content(body));

        mockMvc.perform(post("/api/v1/participantes")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deve_retornar_400_quando_campos_invalidos() throws Exception {
        mockMvc.perform(post("/api/v1/participantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "", "username": "x"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deve_listar_participantes_ativos() throws Exception {
        mockMvc.perform(get("/api/v1/participantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deve_retornar_404_quando_participante_nao_existe() throws Exception {
        mockMvc.perform(get("/api/v1/participantes/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
}
