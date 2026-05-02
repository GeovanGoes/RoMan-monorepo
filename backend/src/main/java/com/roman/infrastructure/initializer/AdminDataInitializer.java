package com.roman.infrastructure.initializer;

import com.roman.application.port.PasswordEncoderPort;
import com.roman.domain.entity.PerfilUsuario;
import com.roman.domain.entity.Usuario;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminDataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;

    public AdminDataInitializer(UsuarioRepository usuarioRepository,
                                PasswordEncoderPort passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Usuario> admins = usuarioRepository.findAllAtivos().stream()
                .filter(u -> u.getPerfil() == PerfilUsuario.ADMIN)
                .toList();

        if (admins.isEmpty()) {
            Usuario admin = Usuario.criar(
                    "Geovan",
                    "geovan",
                    "geovansilvagoes@gmail.com",
                    null,
                    passwordEncoder.encode("12345"),
                    PerfilUsuario.ADMIN
            );
            usuarioRepository.save(admin);
        }
    }
}
