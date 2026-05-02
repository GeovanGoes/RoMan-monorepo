package com.roman.interfaces.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record AdicionarCompraRequest(
        @NotBlank(message = "Descrição é obrigatória")
        @Size(min = 2, max = 500)
        String descricao,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal valor,

        @NotNull(message = "Categoria é obrigatória")
        UUID categoriaId,

        @NotEmpty(message = "Ao menos um pagador é obrigatório")
        Set<UUID> pagadoresIds
) {}
