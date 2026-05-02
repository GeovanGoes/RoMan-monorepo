package com.roman.infrastructure.persistence.adapter;

import com.roman.domain.entity.PasswordResetToken;
import com.roman.domain.repository.PasswordResetTokenRepository;
import com.roman.infrastructure.persistence.entity.PasswordResetTokenJpaEntity;
import com.roman.infrastructure.persistence.repository.PasswordResetTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final PasswordResetTokenJpaRepository jpaRepository;

    public PasswordResetTokenRepositoryAdapter(PasswordResetTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    private PasswordResetTokenJpaEntity toJpa(PasswordResetToken prt) {
        PasswordResetTokenJpaEntity e = new PasswordResetTokenJpaEntity();
        e.setId(prt.getId());
        e.setUsuarioId(prt.getUsuarioId());
        e.setTokenHash(prt.getTokenHash());
        e.setExpiresAt(prt.getExpiresAt());
        e.setUsado(prt.isUsado());
        e.setCreatedAt(prt.getCreatedAt());
        return e;
    }

    private PasswordResetToken toDomain(PasswordResetTokenJpaEntity e) {
        return new PasswordResetToken(e.getId(), e.getUsuarioId(), e.getTokenHash(),
                e.getExpiresAt(), e.isUsado(), e.getCreatedAt());
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        return toDomain(jpaRepository.save(toJpa(token)));
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public void invalidateAllByUsuarioId(UUID usuarioId) {
        jpaRepository.invalidateAllByUsuarioId(usuarioId);
    }
}
