package com.roman.infrastructure.persistence.adapter;

import com.roman.domain.entity.RefreshToken;
import com.roman.domain.repository.RefreshTokenRepository;
import com.roman.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.roman.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    private RefreshTokenJpaEntity toJpa(RefreshToken rt) {
        RefreshTokenJpaEntity e = new RefreshTokenJpaEntity();
        e.setId(rt.getId());
        e.setUsuarioId(rt.getUsuarioId());
        e.setTokenHash(rt.getTokenHash());
        e.setExpiresAt(rt.getExpiresAt());
        e.setRevogado(rt.isRevogado());
        e.setCreatedAt(rt.getCreatedAt());
        return e;
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity e) {
        return new RefreshToken(e.getId(), e.getUsuarioId(), e.getTokenHash(),
                e.getExpiresAt(), e.isRevogado(), e.getCreatedAt());
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return toDomain(jpaRepository.save(toJpa(token)));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public void revokeAllByUsuarioId(UUID usuarioId) {
        jpaRepository.revokeAllByUsuarioId(usuarioId);
    }

    @Override
    public void deleteExpired() {
        jpaRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
