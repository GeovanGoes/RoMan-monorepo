package com.roman.infrastructure.security;

import com.roman.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SpringUserDetails implements UserDetails {

    private final UsuarioJpaEntity entity;

    public SpringUserDetails(UsuarioJpaEntity entity) { this.entity = entity; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + entity.getPerfil().name()));
    }

    @Override
    public String getPassword() { return entity.getSenhaHash(); }

    @Override
    public String getUsername() { return entity.getUsername(); }

    @Override
    public boolean isEnabled() { return entity.getDeletedAt() == null; }
}
