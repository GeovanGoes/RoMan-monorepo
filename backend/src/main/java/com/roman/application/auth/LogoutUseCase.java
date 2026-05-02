package com.roman.application.auth;

import com.roman.domain.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void execute(String refreshTokenRaw) {
        String hash = LoginUseCase.sha256(refreshTokenRaw);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
            rt.revogar();
            refreshTokenRepository.save(rt);
        });
    }
}
