package org.warestore.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.warestore.model.dto.UsersDTO;

@Repository
public interface UsersDAO extends JpaRepository<UsersDTO, Integer> { }
