package com.roman.interfaces.dto.response;

import com.roman.application.evento.TransferenciaSugerida;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferenciaSugeridaResponse(
        UUID deId,
        String nomeDe,
        UUID paraId,
        String nomePara,
        BigDecimal valor
) {
    public static TransferenciaSugeridaResponse from(TransferenciaSugerida t) {
        return new TransferenciaSugeridaResponse(
                t.deId(),
                t.nomeDe(),
                t.paraId(),
                t.nomePara(),
                t.valor()
        );
    }
}
