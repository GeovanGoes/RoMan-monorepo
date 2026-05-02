package com.roman.domain.exception;

public class UrlProibidaException extends DomainException {
    public UrlProibidaException(String url) { super("URL não permitida: " + url); }
}
