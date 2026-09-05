package com.roman.infrastructure;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
class EventoControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void deve_criar_evento_e_retornar_201() throws Exception {
        mockMvc.perform(post("/api/v1/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Churrasco",
                                  "local": "Praia de Copacabana",
                                  "dataInicio": "2026-06-01",
                                  "dataFim": "2026-06-03"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Churrasco"));
    }

    @Test
    void deve_listar_eventos() throws Exception {
        mockMvc.perform(get("/api/v1/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deve_retornar_404_para_evento_inexistente() throws Exception {
        mockMvc.perform(get("/api/v1/eventos/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deve_retornar_400_quando_datas_ausentes() throws Exception {
        mockMvc.perform(post("/api/v1/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Evento sem data", "local": "Lugar"}
                                """))
                .andExpect(status().isBadRequest());
    }

    // --- Rateio / simplificação de dívidas ---

    @Test
    void deve_retornar_404_ao_calcular_rateio_de_evento_inexistente() throws Exception {
        mockMvc.perform(get("/api/v1/eventos/00000000-0000-0000-0000-000000000000/rateio"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deve_retornar_404_ao_simplificar_dividas_de_evento_inexistente() throws Exception {
        mockMvc.perform(get("/api/v1/eventos/00000000-0000-0000-0000-000000000000/rateio/simplificado"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deve_retornar_lista_vazia_ao_simplificar_dividas_sem_compras() throws Exception {
        String eventoId = criarEvento("Evento sem compras");
        String participanteId = criarParticipante("Rateio Solo", "rateio_solo");
        vincularParticipante(eventoId, participanteId);

        mockMvc.perform(get("/api/v1/eventos/" + eventoId + "/rateio/simplificado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deve_calcular_rateio_e_simplificar_dividas_entre_dois_participantes() throws Exception {
        String eventoId = criarEvento("Rateio Dois Participantes");
        String categoriaId = criarCategoria("Categoria Rateio");
        String anaId = criarParticipante("Ana Rateio", "ana_rateio");
        String brunoId = criarParticipante("Bruno Rateio", "bruno_rateio");
        vincularParticipante(eventoId, anaId);
        vincularParticipante(eventoId, brunoId);
        adicionarCompra(eventoId, "Jantar", "100.00", categoriaId, anaId);

        mockMvc.perform(get("/api/v1/eventos/" + eventoId + "/rateio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));

        mockMvc.perform(get("/api/v1/eventos/" + eventoId + "/rateio/simplificado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].deId").value(brunoId))
                .andExpect(jsonPath("$[0].paraId").value(anaId))
                .andExpect(jsonPath("$[0].valor").value(50.00));
    }

    private String criarEvento(String nome) throws Exception {
        String json = mockMvc.perform(post("/api/v1/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "%s", "local": "Local Teste", "dataInicio": "2026-06-01", "dataFim": "2026-06-03"}
                                """.formatted(nome)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$.id");
    }

    private String criarCategoria(String nome) throws Exception {
        String json = mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "%s", "descricao": "Categoria de teste"}
                                """.formatted(nome)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$.id");
    }

    private String criarParticipante(String nome, String username) throws Exception {
        String json = mockMvc.perform(post("/api/v1/participantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "%s", "username": "%s"}
                                """.formatted(nome, username)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$.id");
    }

    private void vincularParticipante(String eventoId, String participanteId) throws Exception {
        mockMvc.perform(post("/api/v1/eventos/" + eventoId + "/participantes/" + participanteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"menorDeIdade\": false}"))
                .andExpect(status().isCreated());
    }

    private void adicionarCompra(String eventoId, String descricao, String valor, String categoriaId, String pagadorId) throws Exception {
        mockMvc.perform(post("/api/v1/eventos/" + eventoId + "/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descricao": "%s", "valor": %s, "categoriaId": "%s", "pagadoresIds": ["%s"]}
                                """.formatted(descricao, valor, categoriaId, pagadorId)))
                .andExpect(status().isCreated());
    }
}
