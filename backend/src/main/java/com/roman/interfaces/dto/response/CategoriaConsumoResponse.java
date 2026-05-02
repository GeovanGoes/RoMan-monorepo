package com.roman.interfaces.dto.response;

import com.roman.domain.entity.CategoriaConsumo;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoriaConsumoResponse(
        UUID id,
        String nome,
        String descricao,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CategoriaConsumoResponse from(CategoriaConsumo c) {
        return new CategoriaConsumoResponse(c.getId(), c.getNome(), c.getDescricao(),
                c.getCreatedAt(), c.getUpdatedAt());
    }
}
