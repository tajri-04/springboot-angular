package com.tajri.jwtApp.repository;

import com.tajri.jwtApp.model.Role;
import com.tajri.jwtApp.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
