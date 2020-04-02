package io.factorialsystems.fotojugbackend.repository;


import io.factorialsystems.fotojugbackend.model.auth.Role;
import io.factorialsystems.fotojugbackend.model.auth.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleType(RoleType type);
}
