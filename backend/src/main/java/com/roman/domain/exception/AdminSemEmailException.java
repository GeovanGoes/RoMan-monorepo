package com.roman.domain.exception;

public class AdminSemEmailException extends DomainException {
    public AdminSemEmailException() { super("Usuários administradores devem ter e-mail cadastrado."); }
}
