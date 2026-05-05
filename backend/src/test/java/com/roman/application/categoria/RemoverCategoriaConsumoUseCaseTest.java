package com.roman.application.categoria;

import com.roman.domain.exception.CategoriaConsumoNotFoundException;
import com.roman.domain.repository.CategoriaConsumoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverCategoriaConsumoUseCaseTest {

    @Mock
    private CategoriaConsumoRepository repository;

    @InjectMocks
    private RemoverCategoriaConsumoUseCase useCase;

    @Test
    void deve_remover_categoria_com_sucesso() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        useCase.execute(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void deve_lancar_excecao_quando_categoria_nao_encontrada() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(CategoriaConsumoNotFoundException.class);

        verify(repository).existsById(id);
        verify(repository, never()).deleteById(id);
    }
}
