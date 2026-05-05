package com.roman.application.evento;

import com.roman.domain.entity.CategoriaConsumo;
import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.repository.CategoriaConsumoRepository;
import com.roman.domain.repository.EventoParticipanteRepository;
import com.roman.domain.repository.EventoRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListarParticipantesDoEventoUseCase {

    private final EventoRepository eventoRepository;
    private final EventoParticipanteRepository epRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaConsumoRepository categoriaRepository;

    public ListarParticipantesDoEventoUseCase(EventoRepository eventoRepository,
                                              EventoParticipanteRepository epRepository,
                                              UsuarioRepository usuarioRepository,
                                              CategoriaConsumoRepository categoriaRepository) {
        this.eventoRepository = eventoRepository;
        this.epRepository = epRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<EventoParticipanteDetalhe> execute(UUID eventoId) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new EventoNotFoundException(eventoId);
        }

        List<EventoParticipante> participantes = epRepository.findByEventoId(eventoId);

        List<UUID> usuarioIds = participantes.stream().map(EventoParticipante::getUsuarioId).toList();
        Map<UUID, Usuario> usuarioMap = usuarioRepository.findAllByIds(usuarioIds)
                .stream().collect(Collectors.toMap(Usuario::getId, u -> u));

        Map<UUID, String> categoriaMap = categoriaRepository.findAll()
                .stream().collect(Collectors.toMap(CategoriaConsumo::getId, CategoriaConsumo::getNome));

        return participantes.stream().map(ep -> {
            Usuario u = usuarioMap.get(ep.getUsuarioId());
            String nome = u != null ? u.getNome() : "Desconhecido";
            String username = u != null ? u.getUsername() : "";

            Set<EventoParticipanteDetalhe.CategoriaInfo> cats = ep.getCategoriasExcluidas().stream()
                    .map(cId -> new EventoParticipanteDetalhe.CategoriaInfo(
                            cId, categoriaMap.getOrDefault(cId, "")))
                    .collect(Collectors.toSet());

            return new EventoParticipanteDetalhe(ep.getId(), ep.getEventoId(), ep.getUsuarioId(),
                    nome, username, ep.isMenorDeIdade(), cats, ep.getCreatedAt());
        }).toList();
    }
}
