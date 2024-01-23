package org.evolve.evolve.dao;


import org.evolve.evolve.entity.User;

public interface UserDao {
    User findByUserName(String userName);
}
