package com.poseman.backend.security;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User 엔티티의 ID(UUID)와 username, authorities를 함께 담아
 * Spring Security가 관리하도록 하는 커스텀 UserDetails 구현체.
 */
public class UserPrincipal implements UserDetails {
    private final UUID userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(UUID userId, String username, String password,
                         Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public UUID getUserId() {
        return userId;
    }

    // UserDetails 인터페이스 구현 메서드들
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
