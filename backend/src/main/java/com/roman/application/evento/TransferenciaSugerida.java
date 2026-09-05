package com.roman.application.evento;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferenciaSugerida(
        UUID deId,
        String nomeDe,
        UUID paraId,
        String nomePara,
        BigDecimal valor
) {}
