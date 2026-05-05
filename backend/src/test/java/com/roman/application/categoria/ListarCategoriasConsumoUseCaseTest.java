package com.roman.application.categoria;

import com.roman.domain.entity.CategoriaConsumo;
import com.roman.domain.repository.CategoriaConsumoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarCategoriasConsumoUseCaseTest {

    @Mock
    private CategoriaConsumoRepository repository;

    @InjectMocks
    private ListarCategoriasConsumoUseCase useCase;

    @Test
    void deve_retornar_lista_de_categorias() {
        CategoriaConsumo bebidas = CategoriaConsumo.criar("Bebidas", "Bebidas em geral");
        CategoriaConsumo comidas = CategoriaConsumo.criar("Comidas", "Alimentos em geral");
        when(repository.findAll()).thenReturn(List.of(bebidas, comidas));

        List<CategoriaConsumo> result = useCase.execute();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CategoriaConsumo::getNome)
                .containsExactly("Bebidas", "Comidas");
        verify(repository).findAll();
    }

    @Test
    void deve_retornar_lista_vazia_quando_nao_ha_categorias() {
        when(repository.findAll()).thenReturn(List.of());

        List<CategoriaConsumo> result = useCase.execute();

        assertThat(result).isEmpty();
        verify(repository).findAll();
    }
}
