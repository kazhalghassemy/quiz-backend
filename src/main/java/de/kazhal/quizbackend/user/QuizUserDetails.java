package de.kazhal.quizbackend.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record QuizUserDetails(Long id, String email, String passwordHash, Role role) implements
        UserDetails {
    public static QuizUserDetails from(AppUser user) {
        return new QuizUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(),
                user.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
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
        return true;
    }

    /**
     * Der Hash soll nie in einem Log auftauchen.
     */
    @Override
    public String toString() {
        return "QuizUserDetails[id=" + id + ", email=" + email + ", role=" + role + "]";
    }
}
