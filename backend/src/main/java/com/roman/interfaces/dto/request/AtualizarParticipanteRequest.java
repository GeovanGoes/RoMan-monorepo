package com.roman.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizarParticipanteRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 255)
        String nome,

        @NotBlank(message = "Username é obrigatório")
        @Size(min = 3, max = 100)
        @Pattern(regexp = "^[a-z0-9_]+$", message = "Username deve conter apenas letras minúsculas, números e underscore")
        String username
) {}
