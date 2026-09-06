package com.roman.infrastructure.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestResponseLoggingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    private final RequestResponseLoggingFilter filter =
            new RequestResponseLoggingFilter(new SensitiveDataMasker(new JsonMapper()));

    @AfterEach
    void limparMdc() {
        MDC.clear();
    }

    private void stubRequisicaoBasica() throws Exception {
        lenient().when(request.getMethod()).thenReturn("GET");
        lenient().when(request.getRequestURI()).thenReturn("/api/v1/eventos");
        lenient().when(request.getQueryString()).thenReturn(null);
        lenient().when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        lenient().when(request.getHeaderNames()).thenReturn(Collections.enumeration(List.of("Content-Type")));
        lenient().when(request.getHeader("Content-Type")).thenReturn("application/json");
        lenient().when(response.getStatus()).thenReturn(200);
        lenient().when(response.getHeaderNames()).thenReturn(List.of());
    }

    @Test
    void gera_correlation_id_quando_nao_ha_header_de_entrada() throws Exception {
        stubRequisicaoBasica();
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader(eq("X-Correlation-Id"), argThatNotBlank());
    }

    @Test
    void reaproveita_correlation_id_recebido_no_header() throws Exception {
        stubRequisicaoBasica();
        when(request.getHeader("X-Correlation-Id")).thenReturn("id-existente-123");

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader("X-Correlation-Id", "id-existente-123");
    }

    @Test
    void mdc_fica_populado_durante_a_chain_e_e_removido_depois() throws Exception {
        stubRequisicaoBasica();
        when(request.getHeader("X-Correlation-Id")).thenReturn("id-mdc-teste");

        doAnswer(invocation -> {
            assertThat(MDC.get("correlationId")).isEqualTo("id-mdc-teste");
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilterInternal(request, response, chain);

        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void mdc_e_removido_mesmo_quando_a_chain_lanca_excecao() throws Exception {
        stubRequisicaoBasica();
        when(request.getHeader("X-Correlation-Id")).thenReturn("id-erro");
        doThrow(new RuntimeException("falha simulada")).when(chain).doFilter(any(), any());

        assertThatThrownBy(() -> filter.doFilterInternal(request, response, chain))
                .isInstanceOf(RuntimeException.class);

        assertThat(MDC.get("correlationId")).isNull();
    }

    private static String argThatNotBlank() {
        return org.mockito.ArgumentMatchers.argThat(s -> s != null && !s.isBlank());
    }
}
