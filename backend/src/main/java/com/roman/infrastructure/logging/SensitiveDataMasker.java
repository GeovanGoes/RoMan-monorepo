package com.roman.infrastructure.logging;

import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Mascara dados sensíveis antes de irem para o log: valores de campos de senha/token
 * em corpos JSON, e valores de headers como Authorization/Cookie.
 */
@Component
public class SensitiveDataMasker {

    private static final String MASK = "***MASKED***";
    private static final int MAX_LOGGED_LENGTH = 4000;

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "senha", "senhaatual", "novasenha", "token", "refreshtoken", "accesstoken", "password"
    );

    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization", "cookie", "set-cookie"
    );

    private final JsonMapper jsonMapper;

    public SensitiveDataMasker(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public String maskJsonBody(String body) {
        if (body == null || body.isBlank()) {
            return "[body vazio]";
        }
        try {
            JsonNode tree = jsonMapper.readTree(body);
            String masked = jsonMapper.writeValueAsString(maskNode(tree));
            return truncate(masked);
        } catch (JacksonException e) {
            return "[body não-JSON, len=" + body.length() + "]";
        }
    }

    public String maskHeaderValue(String headerName, String value) {
        if (headerName != null && SENSITIVE_HEADERS.contains(headerName.toLowerCase(Locale.ROOT))) {
            return MASK;
        }
        return value;
    }

    private JsonNode maskNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            for (var entry : List.copyOf(obj.properties())) {
                String key = entry.getKey();
                if (isSensitiveField(key)) {
                    obj.put(key, MASK);
                } else {
                    obj.set(key, maskNode(entry.getValue()));
                }
            }
            return obj;
        }
        if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                arr.set(i, maskNode(arr.get(i)));
            }
            return arr;
        }
        return node;
    }

    private boolean isSensitiveField(String fieldName) {
        return SENSITIVE_FIELDS.contains(fieldName.toLowerCase(Locale.ROOT));
    }

    private String truncate(String value) {
        if (value.length() <= MAX_LOGGED_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_LOGGED_LENGTH) + "...(truncated)";
    }
}
