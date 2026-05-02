package com.roman.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AtualizarEventoRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 255)
        String nome,

        @NotBlank(message = "Local é obrigatório")
        @Size(min = 2, max = 500)
        String local,

        @NotNull(message = "Data de início é obrigatória")
        LocalDate dataInicio,

        @NotNull(message = "Data de fim é obrigatória")
        LocalDate dataFim
) {}
