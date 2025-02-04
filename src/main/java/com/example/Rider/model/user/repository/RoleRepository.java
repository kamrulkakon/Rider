package com.example.Rider.model.user.repository;

import com.example.Rider.model.enums.ERole;
import com.example.Rider.model.user.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByRoleName(ERole role);
}
