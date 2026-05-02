CREATE TABLE password_reset_token (
    id          UUID         PRIMARY KEY,
    usuario_id  UUID         NOT NULL REFERENCES usuario(id),
    token_hash  VARCHAR(64)  NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    usado       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL,
    CONSTRAINT uq_prt_hash UNIQUE (token_hash)
);
CREATE INDEX idx_prt_usuario ON password_reset_token(usuario_id);
