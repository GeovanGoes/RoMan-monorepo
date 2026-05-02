package com.roman.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "evento_participante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoParticipanteJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "evento_id", nullable = false, columnDefinition = "uuid")
    private UUID eventoId;

    @Column(name = "participante_id", nullable = false, columnDefinition = "uuid")
    private UUID participanteId;

    @Column(name = "menor_de_idade", nullable = false)
    private boolean menorDeIdade;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "evento_participante_exclusao_categoria",
            joinColumns = @JoinColumn(name = "evento_participante_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @Builder.Default
    private Set<CategoriaConsumoJpaEntity> categoriasExcluidas = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
