package com.roman.application.evento;

import java.math.BigDecimal;
import java.util.UUID;

public record RateioItem(
        UUID usuarioId,
        String nomeParticipante,
        BigDecimal totalDevido,
        BigDecimal totalPago,
        BigDecimal saldo
) {}
