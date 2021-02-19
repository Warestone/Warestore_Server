package org.warestore.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.warestore.model.dto.TypesDTO;

@Repository
public interface TypesDAO extends JpaRepository<TypesDTO, Integer> { }
