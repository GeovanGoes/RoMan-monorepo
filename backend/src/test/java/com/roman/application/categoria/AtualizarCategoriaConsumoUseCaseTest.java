package com.roman.application.categoria;

import com.roman.domain.entity.CategoriaConsumo;
import com.roman.domain.exception.CategoriaConsumoNotFoundException;
import com.roman.domain.repository.CategoriaConsumoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarCategoriaConsumoUseCaseTest {

    @Mock
    private CategoriaConsumoRepository repository;

    @InjectMocks
    private AtualizarCategoriaConsumoUseCase useCase;

    @Test
    void deve_atualizar_categoria_com_sucesso() {
        UUID id = UUID.randomUUID();
        CategoriaConsumo categoria = CategoriaConsumo.criar("Bebidas", "Descricao antiga");
        when(repository.findById(id)).thenReturn(Optional.of(categoria));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CategoriaConsumo result = useCase.execute(id, "Bebidas Alcoolicas", "Nova descricao");

        assertThat(result.getNome()).isEqualTo("Bebidas Alcoolicas");
        assertThat(result.getDescricao()).isEqualTo("Nova descricao");
        verify(repository).findById(id);
        verify(repository).save(categoria);
    }

    @Test
    void deve_lancar_excecao_quando_categoria_nao_encontrada() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id, "Novo Nome", "Nova descricao"))
                .isInstanceOf(CategoriaConsumoNotFoundException.class);

        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }
}
