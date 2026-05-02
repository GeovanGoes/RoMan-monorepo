package com.roman.application.auth;

import com.roman.application.port.EmailPort;
import com.roman.domain.entity.PasswordResetToken;
import com.roman.domain.entity.Usuario;
import com.roman.domain.repository.PasswordResetTokenRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
public class SolicitarRecuperacaoSenhaUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final EmailPort emailPort;
    private final String frontendUrl;
    private final SecureRandom secureRandom = new SecureRandom();

    public SolicitarRecuperacaoSenhaUseCase(UsuarioRepository usuarioRepository,
                                            PasswordResetTokenRepository resetTokenRepository,
                                            EmailPort emailPort,
                                            @Value("${app.frontend.url}") String frontendUrl) {
        this.usuarioRepository = usuarioRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.emailPort = emailPort;
        this.frontendUrl = frontendUrl;
    }

    public void execute(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email)
                .filter(u -> u.isAtivo() && u.getEmail() != null);

        if (usuarioOpt.isEmpty()) return; // silent — no user enumeration

        Usuario usuario = usuarioOpt.get();
        resetTokenRepository.invalidateAllByUsuarioId(usuario.getId());

        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String tokenRaw = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        String tokenHash = LoginUseCase.sha256(tokenRaw);

        PasswordResetToken prt = PasswordResetToken.criar(usuario.getId(), tokenHash);
        resetTokenRepository.save(prt);

        String link = frontendUrl + "/auth/redefinir-senha?token=" + tokenRaw;
        emailPort.enviarEmailRecuperacaoSenha(email, link);
    }
}
