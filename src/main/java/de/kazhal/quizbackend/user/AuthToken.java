package de.kazhal.quizbackend.user;

import jakarta.persistence.*;
import java.time.Instant;
@Entity
@Table(name = "auth_token")
public class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant expiresAt;
    @Column(nullable = false)
    private boolean revoked;
    protected AuthToken() {
        // für JPA
    }
    public AuthToken(String tokenHash, AppUser user, Instant createdAt, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
    public boolean isValidAt(Instant now) {
        return !revoked && now.isBefore(expiresAt);
    }
    public void revoke() {
        this.revoked = true;
    }
    public Long getId() { return id; }
    public String getTokenHash() { return tokenHash; }
    public AppUser getUser() { return user; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
}
