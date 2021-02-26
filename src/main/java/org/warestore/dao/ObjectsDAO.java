package org.warestore.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.warestore.model.dto.ObjectsDTO;

@Repository
public interface ObjectsDAO extends JpaRepository<ObjectsDTO, Integer> { }