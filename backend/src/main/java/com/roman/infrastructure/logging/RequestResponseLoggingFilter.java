package com.roman.infrastructure.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Loga toda requisição e resposta (método, URI, headers, body — com dados sensíveis
 * mascarados via {@link SensitiveDataMasker}) e propaga um correlation ID por requisição:
 * reaproveita o header {@value #CORRELATION_ID_HEADER} recebido, ou gera um novo; devolve
 * o mesmo valor no header da resposta; e o mantém no MDC durante todo o processamento, pra
 * aparecer automaticamente em qualquer outro log da requisição (ver {@code logging.pattern.correlation}
 * em application.yml).
 */
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    static final String MDC_KEY = "correlationId";
    private static final int MAX_CACHED_BODY_BYTES = 100_000;

    private final SensitiveDataMasker masker;

    public RequestResponseLoggingFilter(SensitiveDataMasker masker) {
        this.masker = masker;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = resolveCorrelationId(request);
        MDC.put(MDC_KEY, correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_CACHED_BODY_BYTES);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            logRequest(wrappedRequest, correlationId);
            logResponse(wrappedResponse, correlationId, durationMs);
            wrappedResponse.copyBodyToResponse();
            MDC.remove(MDC_KEY);
        }
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        String incoming = request.getHeader(CORRELATION_ID_HEADER);
        return (incoming != null && !incoming.isBlank()) ? incoming : UUID.randomUUID().toString();
    }

    private void logRequest(ContentCachingRequestWrapper request, String correlationId) {
        log.info("HTTP request  correlationId={} method={} uri={} remoteAddr={} headers={} body={}",
                correlationId,
                request.getMethod(),
                fullUri(request),
                request.getRemoteAddr(),
                requestHeaders(request),
                masker.maskJsonBody(bodyOf(request.getContentAsByteArray())));
    }

    private void logResponse(ContentCachingResponseWrapper response, String correlationId, long durationMs) {
        log.info("HTTP response correlationId={} status={} durationMs={} headers={} body={}",
                correlationId,
                response.getStatus(),
                durationMs,
                responseHeaders(response),
                masker.maskJsonBody(bodyOf(response.getContentAsByteArray())));
    }

    private String fullUri(HttpServletRequest request) {
        String query = request.getQueryString();
        return query != null ? request.getRequestURI() + "?" + query : request.getRequestURI();
    }

    private String requestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                headers.put(name, masker.maskHeaderValue(name, request.getHeader(name)));
            }
        }
        return headers.toString();
    }

    private String responseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (String name : response.getHeaderNames()) {
            headers.put(name, masker.maskHeaderValue(name, response.getHeader(name)));
        }
        return headers.toString();
    }

    private String bodyOf(byte[] content) {
        return content.length == 0 ? "" : new String(content, StandardCharsets.UTF_8);
    }
}
