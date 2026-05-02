package com.roman.application.port;

public interface EmailPort {
    void enviarEmailRecuperacaoSenha(String destinatario, String linkRecuperacao);
}
