package com.roman.application.evento;

import com.roman.domain.entity.CategoriaConsumo;
import com.roman.domain.entity.EventoParticipante;
import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.repository.CategoriaConsumoRepository;
import com.roman.domain.repository.EventoParticipanteRepository;
import com.roman.domain.repository.EventoRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarParticipantesDoEventoUseCaseTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private EventoParticipanteRepository epRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private CategoriaConsumoRepository categoriaRepository;

    @InjectMocks
    private ListarParticipantesDoEventoUseCase useCase;

    private static Usuario usuarioComId(UUID id, String nome, String username) {
        LocalDateTime now = LocalDateTime.now();
        return new Usuario(id, nome, username, null, null, null,
                PerfilUsuario.CONVIDADO, false, now, now, null);
    }

    private static CategoriaConsumo categoriaComId(UUID id, String nome) {
        return new CategoriaConsumo(id, nome, "", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void deve_listar_participantes_com_nome_e_username() {
        UUID eventoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        EventoParticipante ep = new EventoParticipante(UUID.randomUUID(), eventoId,
                usuarioId, false, Set.of(), now);
        Usuario usuario = usuarioComId(usuarioId, "João Silva", "joaosilva");

        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(epRepository.findByEventoId(eventoId)).thenReturn(List.of(ep));
        when(usuarioRepository.findAllByIds(List.of(usuarioId))).thenReturn(List.of(usuario));
        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<EventoParticipanteDetalhe> result = useCase.execute(eventoId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nomeUsuario()).isEqualTo("João Silva");
        assertThat(result.get(0).usernameUsuario()).isEqualTo("joaosilva");
        assertThat(result.get(0).usuarioId()).isEqualTo(usuarioId);
        assertThat(result.get(0).menorDeIdade()).isFalse();
        assertThat(result.get(0).categoriasExcluidas()).isEmpty();
    }

    @Test
    void deve_mapear_categorias_excluidas_com_nome() {
        UUID eventoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        EventoParticipante ep = new EventoParticipante(UUID.randomUUID(), eventoId,
                usuarioId, false, Set.of(categoriaId), now);
        Usuario usuario = usuarioComId(usuarioId, "Maria", "maria");
        CategoriaConsumo categoria = categoriaComId(categoriaId, "Bebidas Alcoólicas");

        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(epRepository.findByEventoId(eventoId)).thenReturn(List.of(ep));
        when(usuarioRepository.findAllByIds(List.of(usuarioId))).thenReturn(List.of(usuario));
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<EventoParticipanteDetalhe> result = useCase.execute(eventoId);

        Set<EventoParticipanteDetalhe.CategoriaInfo> cats = result.get(0).categoriasExcluidas();
        assertThat(cats).hasSize(1);
        assertThat(cats.iterator().next().nome()).isEqualTo("Bebidas Alcoólicas");
        assertThat(cats.iterator().next().id()).isEqualTo(categoriaId);
    }

    @Test
    void deve_usar_nome_desconhecido_quando_usuario_nao_encontrado() {
        UUID eventoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        EventoParticipante ep = new EventoParticipante(UUID.randomUUID(), eventoId,
                usuarioId, false, Set.of(), now);

        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(epRepository.findByEventoId(eventoId)).thenReturn(List.of(ep));
        when(usuarioRepository.findAllByIds(List.of(usuarioId))).thenReturn(List.of());
        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<EventoParticipanteDetalhe> result = useCase.execute(eventoId);

        assertThat(result.get(0).nomeUsuario()).isEqualTo("Desconhecido");
        assertThat(result.get(0).usernameUsuario()).isEmpty();
    }

    @Test
    void deve_retornar_lista_vazia_quando_evento_nao_tem_participantes() {
        UUID eventoId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(epRepository.findByEventoId(eventoId)).thenReturn(List.of());
        when(usuarioRepository.findAllByIds(List.of())).thenReturn(List.of());
        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<EventoParticipanteDetalhe> result = useCase.execute(eventoId);

        assertThat(result).isEmpty();
        verify(eventoRepository).existsById(eventoId);
        verify(epRepository).findByEventoId(eventoId);
    }

    @Test
    void deve_lancar_excecao_quando_evento_nao_encontrado() {
        UUID eventoId = UUID.randomUUID();
        when(eventoRepository.existsById(eventoId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(eventoId))
                .isInstanceOf(EventoNotFoundException.class);

        verify(eventoRepository).existsById(eventoId);
        verify(epRepository, never()).findByEventoId(any());
    }

    @Test
    void deve_listar_multiplos_participantes_com_dados_corretos() {
        UUID eventoId = UUID.randomUUID();
        UUID uid1 = UUID.randomUUID();
        UUID uid2 = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        EventoParticipante ep1 = new EventoParticipante(UUID.randomUUID(), eventoId,
                uid1, false, Set.of(), now);
        EventoParticipante ep2 = new EventoParticipante(UUID.randomUUID(), eventoId,
                uid2, true, Set.of(), now);
        Usuario u1 = usuarioComId(uid1, "Ana", "ana");
        Usuario u2 = usuarioComId(uid2, "Pedro Menor", "pedromenor");

        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        when(epRepository.findByEventoId(eventoId)).thenReturn(List.of(ep1, ep2));
        when(usuarioRepository.findAllByIds(List.of(uid1, uid2))).thenReturn(List.of(u1, u2));
        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<EventoParticipanteDetalhe> result = useCase.execute(eventoId);

        assertThat(result).hasSize(2);
        assertThat(result.stream().map(EventoParticipanteDetalhe::nomeUsuario))
                .containsExactlyInAnyOrder("Ana", "Pedro Menor");
        assertThat(result.stream().filter(EventoParticipanteDetalhe::menorDeIdade).count()).isEqualTo(1);
    }
}
