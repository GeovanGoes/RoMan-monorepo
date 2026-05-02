CREATE TABLE participante (
    id          UUID PRIMARY KEY,
    nome        VARCHAR(255) NOT NULL,
    username    VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    deleted_at  TIMESTAMP,
    CONSTRAINT uq_participante_username UNIQUE (username)
);

CREATE TABLE categoria_consumo (
    id          UUID PRIMARY KEY,
    nome        VARCHAR(255) NOT NULL,
    descricao   TEXT,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);

CREATE TABLE evento (
    id           UUID PRIMARY KEY,
    nome         VARCHAR(255) NOT NULL,
    local        VARCHAR(500) NOT NULL,
    data_inicio  DATE         NOT NULL,
    data_fim     DATE         NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL
);

CREATE TABLE evento_participante (
    id               UUID    PRIMARY KEY,
    evento_id        UUID    NOT NULL REFERENCES evento(id) ON DELETE CASCADE,
    participante_id  UUID    NOT NULL REFERENCES participante(id),
    menor_de_idade   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP NOT NULL,
    CONSTRAINT uq_evento_participante UNIQUE (evento_id, participante_id)
);

CREATE TABLE evento_participante_exclusao_categoria (
    evento_participante_id  UUID NOT NULL REFERENCES evento_participante(id) ON DELETE CASCADE,
    categoria_id            UUID NOT NULL REFERENCES categoria_consumo(id) ON DELETE CASCADE,
    PRIMARY KEY (evento_participante_id, categoria_id)
);

CREATE TABLE compra (
    id           UUID           PRIMARY KEY,
    descricao    VARCHAR(500)   NOT NULL,
    valor        DECIMAL(15, 2) NOT NULL,
    evento_id    UUID           NOT NULL REFERENCES evento(id) ON DELETE CASCADE,
    categoria_id UUID           NOT NULL REFERENCES categoria_consumo(id),
    created_at   TIMESTAMP      NOT NULL,
    updated_at   TIMESTAMP      NOT NULL
);

CREATE TABLE compra_pagador (
    compra_id       UUID NOT NULL REFERENCES compra(id) ON DELETE CASCADE,
    participante_id UUID NOT NULL REFERENCES participante(id),
    PRIMARY KEY (compra_id, participante_id)
);
