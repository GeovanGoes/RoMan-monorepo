package com.roman.application.auth;

import com.roman.domain.entity.RefreshToken;
import com.roman.domain.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private LogoutUseCase useCase;

    private RefreshToken criarRefreshTokenValido(UUID usuarioId) {
        return new RefreshToken(UUID.randomUUID(), usuarioId,
                LoginUseCase.sha256("refresh-token-raw"),
                LocalDateTime.now().plusDays(7), false, LocalDateTime.now());
    }

    @Test
    void deve_revogar_refresh_token_com_sucesso() {
        UUID usuarioId = UUID.randomUUID();
        RefreshToken rt = criarRefreshTokenValido(usuarioId);
        String tokenRaw = "refresh-token-raw";
        String tokenHash = LoginUseCase.sha256(tokenRaw);

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(rt));
        when(refreshTokenRepository.save(rt)).thenReturn(rt);

        useCase.execute(tokenRaw);

        assertThat(rt.isRevogado()).isTrue();
        verify(refreshTokenRepository).save(rt);
    }

    @Test
    void deve_ignorar_quando_token_nao_encontrado() {
        String tokenRaw = "token-inexistente";
        String tokenHash = LoginUseCase.sha256(tokenRaw);

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

        useCase.execute(tokenRaw);

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void deve_salvar_token_revogado_no_repositorio() {
        UUID usuarioId = UUID.randomUUID();
        RefreshToken rt = criarRefreshTokenValido(usuarioId);
        String tokenRaw = "refresh-token-raw";
        String tokenHash = LoginUseCase.sha256(tokenRaw);

        when(refreshTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(rt));
        when(refreshTokenRepository.save(rt)).thenReturn(rt);

        useCase.execute(tokenRaw);

        verify(refreshTokenRepository, times(1)).save(rt);
    }
}
