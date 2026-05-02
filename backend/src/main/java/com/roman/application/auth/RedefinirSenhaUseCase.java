package com.roman.application.auth;

import com.roman.application.port.PasswordEncoderPort;
import com.roman.domain.entity.PasswordResetToken;
import com.roman.domain.entity.Usuario;
import com.roman.domain.exception.SenhaFracaException;
import com.roman.domain.exception.TokenInvalidoException;
import com.roman.domain.exception.UsuarioNotFoundException;
import com.roman.domain.repository.PasswordResetTokenRepository;
import com.roman.domain.repository.RefreshTokenRepository;
import com.roman.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class RedefinirSenhaUseCase {

    private final PasswordResetTokenRepository resetTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;

    public RedefinirSenhaUseCase(PasswordResetTokenRepository resetTokenRepository,
                                 UsuarioRepository usuarioRepository,
                                 RefreshTokenRepository refreshTokenRepository,
                                 PasswordEncoderPort passwordEncoder) {
        this.resetTokenRepository = resetTokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void execute(String tokenRaw, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 8 || !novaSenha.matches(".*\\d.*")) {
            throw new SenhaFracaException();
        }

        String tokenHash = LoginUseCase.sha256(tokenRaw);
        PasswordResetToken prt = resetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(TokenInvalidoException::new);

        if (!prt.isValido()) throw new TokenInvalidoException();

        Usuario usuario = usuarioRepository.findById(prt.getUsuarioId())
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new UsuarioNotFoundException(prt.getUsuarioId()));

        usuario.alterarSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        prt.marcarComoUsado();
        resetTokenRepository.save(prt);
        refreshTokenRepository.revokeAllByUsuarioId(usuario.getId());
    }
}
