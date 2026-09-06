package com.roman.infrastructure.logging;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class SensitiveDataMaskerTest {

    private final SensitiveDataMasker masker = new SensitiveDataMasker(new JsonMapper());

    // --- maskJsonBody ---

    @Test
    void mascara_campo_senha_em_objeto_simples() {
        String json = """
                {"username": "joao", "senha": "minhaSenha123"}
                """;

        String masked = masker.maskJsonBody(json);

        assertThat(masked).contains("\"username\":\"joao\"");
        assertThat(masked).contains("\"senha\":\"***MASKED***\"");
        assertThat(masked).doesNotContain("minhaSenha123");
    }

    @Test
    void mascara_multiplos_campos_sensiveis() {
        String json = """
                {"senhaAtual": "atual123", "novaSenha": "nova456"}
                """;

        String masked = masker.maskJsonBody(json);

        assertThat(masked).doesNotContain("atual123").doesNotContain("nova456");
        assertThat(masked).contains("\"senhaAtual\":\"***MASKED***\"");
        assertThat(masked).contains("\"novaSenha\":\"***MASKED***\"");
    }

    @Test
    void mascara_tokens() {
        String json = """
                {"accessToken": "abc.def.ghi", "refreshToken": "xyz-123", "token": "tok-1"}
                """;

        String masked = masker.maskJsonBody(json);

        assertThat(masked).doesNotContain("abc.def.ghi").doesNotContain("xyz-123").doesNotContain("tok-1");
    }

    @Test
    void mascaramento_e_case_insensitive() {
        String json = """
                {"Senha": "abc123"}
                """;

        String masked = masker.maskJsonBody(json);

        assertThat(masked).doesNotContain("abc123");
    }

    @Test
    void mascara_campo_sensivel_em_objeto_aninhado() {
        String json = """
                {"usuario": {"nome": "Ana", "senha": "segredo"}}
                """;

        String masked = masker.maskJsonBody(json);

        assertThat(masked).contains("\"nome\":\"Ana\"");
        assertThat(masked).doesNotContain("segredo");
    }

    @Test
    void mascara_campo_sensivel_dentro_de_array() {
        String json = """
                [{"username": "a", "senha": "s1"}, {"username": "b", "senha": "s2"}]
                """;

        String masked = masker.maskJsonBody(json);

        assertThat(masked).doesNotContain("s1").doesNotContain("s2");
        assertThat(masked).contains("\"username\":\"a\"");
        assertThat(masked).contains("\"username\":\"b\"");
    }

    @Test
    void nao_mexe_em_campos_nao_sensiveis() {
        String json = """
                {"nome": "Ana", "email": "ana@example.com", "idade": 30}
                """;

        String masked = masker.maskJsonBody(json);

        assertThat(masked).contains("\"nome\":\"Ana\"");
        assertThat(masked).contains("\"email\":\"ana@example.com\"");
        assertThat(masked).contains("\"idade\":30");
    }

    @Test
    void body_vazio_retorna_marcador_sem_lancar_excecao() {
        assertThat(masker.maskJsonBody("")).isEqualTo("[body vazio]");
        assertThat(masker.maskJsonBody(null)).isEqualTo("[body vazio]");
        assertThat(masker.maskJsonBody("   ")).isEqualTo("[body vazio]");
    }

    @Test
    void body_nao_json_retorna_marcador_sem_lancar_excecao() {
        String naoJson = "isso não é json nenhum {{{";

        String result = masker.maskJsonBody(naoJson);

        assertThat(result).startsWith("[body não-JSON");
        assertThat(result).doesNotContain("isso não é json nenhum");
    }

    @Test
    void trunca_body_grande() {
        String valorGrande = "x".repeat(5000);
        String json = "{\"descricao\": \"" + valorGrande + "\"}";

        String masked = masker.maskJsonBody(json);

        assertThat(masked.length()).isLessThan(json.length());
        assertThat(masked).endsWith("...(truncated)");
    }

    // --- maskHeaderValue ---

    @Test
    void mascara_header_authorization_case_insensitive() {
        assertThat(masker.maskHeaderValue("Authorization", "Bearer abc.def.ghi")).isEqualTo("***MASKED***");
        assertThat(masker.maskHeaderValue("authorization", "Bearer abc.def.ghi")).isEqualTo("***MASKED***");
        assertThat(masker.maskHeaderValue("AUTHORIZATION", "Bearer abc.def.ghi")).isEqualTo("***MASKED***");
    }

    @Test
    void mascara_headers_de_cookie() {
        assertThat(masker.maskHeaderValue("Cookie", "JSESSIONID=abc123")).isEqualTo("***MASKED***");
        assertThat(masker.maskHeaderValue("Set-Cookie", "JSESSIONID=abc123")).isEqualTo("***MASKED***");
    }

    @Test
    void nao_mascara_headers_normais() {
        assertThat(masker.maskHeaderValue("Content-Type", "application/json")).isEqualTo("application/json");
        assertThat(masker.maskHeaderValue("User-Agent", "curl/8.0")).isEqualTo("curl/8.0");
    }
}
