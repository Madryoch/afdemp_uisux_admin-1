package org.afdemp.uisux.repository;

import org.afdemp.uisux.domain.security.Role;
import org.springframework.data.repository.CrudRepository;


public interface RoleRepository extends CrudRepository<Role, Long> {
	Role findByname(String name);
}
