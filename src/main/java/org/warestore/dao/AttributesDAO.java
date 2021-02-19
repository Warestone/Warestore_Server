package org.warestore.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.warestore.model.dto.AttributesDTO;

@Repository
public interface AttributesDAO extends JpaRepository<AttributesDTO, Integer> { }
