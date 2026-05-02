package com.roman.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AtualizarUsuarioRequest(@NotBlank String nome, String email, String telefone) {}
