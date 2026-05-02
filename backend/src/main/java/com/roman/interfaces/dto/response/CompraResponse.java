package com.roman.interfaces.dto.response;

import com.roman.domain.entity.Compra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record CompraResponse(
        UUID id,
        String descricao,
        BigDecimal valor,
        UUID eventoId,
        UUID categoriaId,
        Set<UUID> pagadoresIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CompraResponse from(Compra c) {
        return new CompraResponse(c.getId(), c.getDescricao(), c.getValor(), c.getEventoId(),
                c.getCategoriaId(), c.getPagadoresIds(), c.getCreatedAt(), c.getUpdatedAt());
    }
}
