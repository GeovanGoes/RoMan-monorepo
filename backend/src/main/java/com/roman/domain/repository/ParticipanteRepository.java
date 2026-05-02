package com.roman.domain.repository;

import com.roman.domain.entity.Participante;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipanteRepository {
    Participante save(Participante participante);
    Optional<Participante> findById(UUID id);
    List<Participante> findAllAtivos();
    boolean existsByUsernameAtivo(String username);
    boolean existsByUsernameAtivoAndIdNot(String username, UUID id);
}
