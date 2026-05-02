CREATE TABLE usuario (
    id               UUID         PRIMARY KEY,
    nome             VARCHAR(255) NOT NULL,
    username         VARCHAR(100) NOT NULL,
    email            TEXT,
    email_hash       VARCHAR(64),
    telefone         TEXT,
    senha_hash       VARCHAR(255) NOT NULL,
    perfil           VARCHAR(20)  NOT NULL,
    senha_provisoria BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL,
    deleted_at       TIMESTAMP,
    CONSTRAINT uq_usuario_username  UNIQUE (username),
    CONSTRAINT uq_usuario_email_hash UNIQUE (email_hash)
);
