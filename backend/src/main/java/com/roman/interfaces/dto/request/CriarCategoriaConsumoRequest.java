package com.roman.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarCategoriaConsumoRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 255)
        String nome,

        String descricao
) {}
