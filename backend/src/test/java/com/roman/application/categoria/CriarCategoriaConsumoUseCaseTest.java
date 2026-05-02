package com.roman.application.categoria;

import com.roman.domain.entity.CategoriaConsumo;
import com.roman.domain.repository.CategoriaConsumoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarCategoriaConsumoUseCaseTest {

    @Mock
    private CategoriaConsumoRepository repository;

    @InjectMocks
    private CriarCategoriaConsumoUseCase useCase;

    @Test
    void deve_criar_categoria_com_sucesso() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CategoriaConsumo result = useCase.execute("Bebida Alcoólica", "Bebidas com teor alcoólico");

        assertThat(result.getId()).isNotNull();
        assertThat(result.getNome()).isEqualTo("Bebida Alcoólica");
        assertThat(result.getDescricao()).isEqualTo("Bebidas com teor alcoólico");
        assertThat(result.getCreatedAt()).isNotNull();
        verify(repository).save(any(CategoriaConsumo.class));
    }

    @Test
    void deve_criar_categoria_sem_descricao() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CategoriaConsumo result = useCase.execute("Alimentação", null);

        assertThat(result.getNome()).isEqualTo("Alimentação");
        assertThat(result.getDescricao()).isNull();
    }
}
