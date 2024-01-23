package org.evolve.evolve.dao;


import org.evolve.evolve.entity.Role;

public interface RoleDao {

	public Role findRoleByName(String theRoleName);
}
