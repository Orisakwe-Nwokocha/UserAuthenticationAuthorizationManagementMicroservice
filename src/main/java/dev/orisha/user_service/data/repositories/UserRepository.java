package dev.orisha.user_service.data.repositories;

import dev.orisha.user_service.data.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByEmail(final String email);

    Optional<User> findByEmail(final String email);

    @Query("SELECT u FROM User u WHERE u.email=:email")
    @EntityGraph(attributePaths = {"authorities"})
    Optional<User> findByEmailWithEagerRelationships(final String email);

}
