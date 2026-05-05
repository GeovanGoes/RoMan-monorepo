package com.roman.application.evento;

import com.roman.domain.exception.CompraNotFoundException;
import com.roman.domain.repository.CompraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoverCompraUseCaseTest {

    @Mock
    private CompraRepository repository;

    @InjectMocks
    private RemoverCompraUseCase useCase;

    @Test
    void deve_remover_compra_com_sucesso() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        useCase.execute(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void deve_lancar_excecao_quando_compra_nao_encontrada() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(CompraNotFoundException.class);

        verify(repository).existsById(id);
        verify(repository, never()).deleteById(any());
    }
}
