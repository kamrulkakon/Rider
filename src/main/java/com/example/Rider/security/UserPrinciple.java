package com.example.Rider.security;

import com.example.Rider.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserPrinciple implements UserDetails {
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    private String name;

    private boolean isEnabled;

    private List<GrantedAuthority> authorities;

    public UserPrinciple(User user) {
        this.id = user.getId();
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getDisplayName();
        this.isEnabled = user.getIsEnabled();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name())).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
