package com.roman.infrastructure.persistence.adapter;

import com.roman.domain.entity.Usuario;
import com.roman.domain.repository.UsuarioRepository;
import com.roman.infrastructure.persistence.entity.UsuarioJpaEntity;
import com.roman.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;
    private final byte[] hmacKey;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository,
                                    @Value("${app.encryption.email-hmac-key}") String hmacKeyBase64) {
        this.jpaRepository = jpaRepository;
        this.hmacKey = Base64.getDecoder().decode(hmacKeyBase64);
    }

    private String hmacEmail(String email) {
        if (email == null || email.isBlank()) return null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacKey, "HmacSHA256"));
            byte[] hash = mac.doFinal(email.toLowerCase().trim().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao calcular HMAC do email", e);
        }
    }

    private UsuarioJpaEntity toJpa(Usuario u) {
        UsuarioJpaEntity e = new UsuarioJpaEntity();
        e.setId(u.getId());
        e.setNome(u.getNome());
        e.setUsername(u.getUsername());
        e.setEmail(u.getEmail());
        e.setEmailHash(hmacEmail(u.getEmail()));
        e.setTelefone(u.getTelefone());
        e.setSenhaHash(u.getSenhaHash());
        e.setPerfil(u.getPerfil());
        e.setSenhaProvisoria(u.isSenhaProvisoria());
        e.setCreatedAt(u.getCreatedAt());
        e.setUpdatedAt(u.getUpdatedAt());
        e.setDeletedAt(u.getDeletedAt());
        return e;
    }

    private Usuario toDomain(UsuarioJpaEntity e) {
        return new Usuario(e.getId(), e.getNome(), e.getUsername(), e.getEmail(), e.getTelefone(),
                e.getSenhaHash(), e.getPerfil(), e.isSenhaProvisoria(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt());
    }

    @Override
    public Usuario save(Usuario usuario) {
        return toDomain(jpaRepository.save(toJpa(usuario)));
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        String hash = hmacEmail(email);
        if (hash == null) return Optional.empty();
        return jpaRepository.findByEmailHash(hash).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, UUID id) {
        return jpaRepository.existsByUsernameAndIdNot(username, id);
    }

    @Override
    public List<Usuario> findAllAtivos() {
        return jpaRepository.findAllByDeletedAtIsNull().stream().map(this::toDomain).toList();
    }
}
