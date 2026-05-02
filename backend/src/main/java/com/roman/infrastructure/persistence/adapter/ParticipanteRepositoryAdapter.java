package com.roman.infrastructure.persistence.adapter;

import com.roman.domain.entity.Participante;
import com.roman.domain.repository.ParticipanteRepository;
import com.roman.infrastructure.persistence.entity.ParticipanteJpaEntity;
import com.roman.infrastructure.persistence.repository.ParticipanteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ParticipanteRepositoryAdapter implements ParticipanteRepository {

    private final ParticipanteJpaRepository jpaRepository;

    public ParticipanteRepositoryAdapter(ParticipanteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Participante save(Participante participante) {
        return toDomain(jpaRepository.save(toEntity(participante)));
    }

    @Override
    public Optional<Participante> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Participante> findAllAtivos() {
        return jpaRepository.findAllByDeletedAtIsNull().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByUsernameAtivo(String username) {
        return jpaRepository.existsByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public boolean existsByUsernameAtivoAndIdNot(String username, UUID id) {
        return jpaRepository.existsByUsernameAndDeletedAtIsNullAndIdNot(username, id);
    }

    private ParticipanteJpaEntity toEntity(Participante p) {
        return ParticipanteJpaEntity.builder()
                .id(p.getId())
                .nome(p.getNome())
                .username(p.getUsername())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(p.getDeletedAt())
                .build();
    }

    private Participante toDomain(ParticipanteJpaEntity e) {
        return new Participante(e.getId(), e.getNome(), e.getUsername(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt());
    }
}
