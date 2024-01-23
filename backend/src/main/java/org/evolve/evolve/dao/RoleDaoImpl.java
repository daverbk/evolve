package org.evolve.evolve.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.evolve.evolve.entity.Role;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDaoImpl implements RoleDao {

	private final EntityManager entityManager;

	public RoleDaoImpl(EntityManager theEntityManager) {
		entityManager = theEntityManager;
	}

	@Override
	public Role findRoleByName(String roleName) {

		TypedQuery<Role> theQuery = entityManager.createQuery("from Role where name=:role", Role.class);
		theQuery.setParameter("role", roleName);

		Role result = null;

		try {
			result = theQuery.getSingleResult();
		} catch (Exception ignored) {
        }

		return result;
	}
}
