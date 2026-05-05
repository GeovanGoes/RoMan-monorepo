-- Permite senha_hash nula para convidados (sem conta)
ALTER TABLE usuario ALTER COLUMN senha_hash DROP NOT NULL;

-- Migra participantes existentes para a tabela usuario com perfil CONVIDADO
INSERT INTO usuario (id, nome, username, perfil, senha_provisoria, created_at, updated_at, deleted_at)
SELECT id, nome, username, 'CONVIDADO', false, created_at, updated_at, deleted_at
FROM participante;

-- Atualiza evento_participante: renomeia participante_id para usuario_id e atualiza FK
ALTER TABLE evento_participante DROP CONSTRAINT evento_participante_participante_id_fkey;
ALTER TABLE evento_participante DROP CONSTRAINT uq_evento_participante;
ALTER TABLE evento_participante RENAME COLUMN participante_id TO usuario_id;
ALTER TABLE evento_participante
    ADD CONSTRAINT evento_participante_usuario_id_fkey
        FOREIGN KEY (usuario_id) REFERENCES usuario(id);
ALTER TABLE evento_participante
    ADD CONSTRAINT uq_evento_usuario UNIQUE (evento_id, usuario_id);

-- Atualiza compra_pagador: renomeia participante_id para usuario_id e atualiza FK
ALTER TABLE compra_pagador DROP CONSTRAINT compra_pagador_participante_id_fkey;
ALTER TABLE compra_pagador DROP CONSTRAINT compra_pagador_pkey;
ALTER TABLE compra_pagador RENAME COLUMN participante_id TO usuario_id;
ALTER TABLE compra_pagador ADD PRIMARY KEY (compra_id, usuario_id);
ALTER TABLE compra_pagador
    ADD CONSTRAINT compra_pagador_usuario_id_fkey
        FOREIGN KEY (usuario_id) REFERENCES usuario(id);

-- Remove a tabela participante (agora unificada em usuario)
DROP TABLE participante;
