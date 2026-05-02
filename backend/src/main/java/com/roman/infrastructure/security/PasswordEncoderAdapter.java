package com.roman.infrastructure.security;

import com.roman.application.port.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);

    @Override
    public String encode(String raw) { return bcrypt.encode(raw); }

    @Override
    public boolean matches(String raw, String encoded) { return bcrypt.matches(raw, encoded); }
}
