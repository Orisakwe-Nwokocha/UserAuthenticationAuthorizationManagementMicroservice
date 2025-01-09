package dev.orisha.user_service.data.models;

import dev.orisha.user_service.data.enums.Authority;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

import static jakarta.persistence.EnumType.STRING;
import static java.time.LocalDateTime.now;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection
    @Enumerated(STRING)
    private Set<Authority> authorities;

    @Setter(AccessLevel.NONE)
    private LocalDateTime dateRegistered;

    @Setter(AccessLevel.NONE)
    private LocalDateTime dateUpdated;


    @PrePersist
    private void setDateRegistered(){
        dateRegistered = now();
    }

    @PreUpdate
    private void setDateUpdated(){
        dateUpdated = now();
    }
}
