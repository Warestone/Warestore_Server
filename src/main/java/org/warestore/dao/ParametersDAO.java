package org.warestore.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.warestore.model.dto.ParametersDTO;

@Repository
public interface ParametersDAO extends JpaRepository<ParametersDTO, Integer> { }