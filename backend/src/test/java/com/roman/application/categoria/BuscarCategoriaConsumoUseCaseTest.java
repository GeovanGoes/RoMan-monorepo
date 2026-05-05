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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarCategoriaConsumoUseCaseTest {

    @Mock
    private CategoriaConsumoRepository repository;

    @InjectMocks
    private BuscarCategoriaConsumoUseCase useCase;

    @Test
    void deve_retornar_categoria_quando_id_existe() {
        UUID id = UUID.randomUUID();
        CategoriaConsumo categoria = CategoriaConsumo.criar("Bebidas", "Bebidas em geral");
        when(repository.findById(id)).thenReturn(Optional.of(categoria));

        CategoriaConsumo result = useCase.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Bebidas");
        assertThat(result.getDescricao()).isEqualTo("Bebidas em geral");
        verify(repository).findById(id);
    }

    @Test
    void deve_lancar_excecao_quando_categoria_nao_encontrada() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(CategoriaConsumoNotFoundException.class);

        verify(repository).findById(id);
    }
}
