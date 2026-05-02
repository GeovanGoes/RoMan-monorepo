package com.roman.interfaces.dto.response;

import com.roman.application.evento.RateioItem;

import java.math.BigDecimal;
import java.util.UUID;

public record RateioItemResponse(
        UUID participanteId,
        String nomeParticipante,
        BigDecimal totalDevido,
        BigDecimal totalPago,
        BigDecimal saldo
) {
    public static RateioItemResponse from(RateioItem item) {
        return new RateioItemResponse(
                item.participanteId(),
                item.nomeParticipante(),
                item.totalDevido(),
                item.totalPago(),
                item.saldo()
        );
    }
}
