package com.roman.infrastructure;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.roman.infrastructure.logging.RequestResponseLoggingFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class RequestResponseLoggingFilterIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void anexarAppender() {
        appender = new ListAppender<>();
        appender.start();
        ((Logger) LoggerFactory.getLogger(RequestResponseLoggingFilter.class)).addAppender(appender);
    }

    @AfterEach
    void removerAppender() {
        ((Logger) LoggerFactory.getLogger(RequestResponseLoggingFilter.class)).detachAppender(appender);
    }

    @Test
    void resposta_tem_correlation_id_em_rota_publica() throws Exception {
        mockMvc.perform(get("/api/v1/eventos"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-Id"));
    }

    @Test
    void reaproveita_correlation_id_enviado_pelo_cliente() throws Exception {
        mockMvc.perform(get("/api/v1/eventos").header("X-Correlation-Id", "meu-id-customizado"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Correlation-Id", "meu-id-customizado"));
    }

    @Test
    void resposta_tem_correlation_id_mesmo_quando_nao_autenticado() throws Exception {
        // POST /api/v1/eventos é protegido; sem autenticação, o Spring Security rejeita
        // antes de chegar no controller — o header precisa aparecer mesmo assim, o que
        // prova que o filtro roda antes da SecurityFilterChain.
        mockMvc.perform(post("/api/v1/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(header().exists("X-Correlation-Id"));
    }

    @Test
    void mascara_senha_no_log_de_login_invalido() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .header("X-Correlation-Id", "id-teste-mascaramento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "usuario-inexistente-xyz", "senha": "senhaTextoPlano123"}
                                """))
                .andExpect(status().isUnauthorized());

        List<String> mensagens = appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
        String requestLog = mensagens.stream()
                .filter(m -> m.startsWith("HTTP request"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nenhuma linha de log de request encontrada: " + mensagens));

        assertThat(requestLog).doesNotContain("senhaTextoPlano123");
        assertThat(requestLog).contains("***MASKED***");
        assertThat(requestLog).contains("id-teste-mascaramento");
    }
}
