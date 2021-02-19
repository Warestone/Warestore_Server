package org.warestore.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.warestore.model.dto.LinksDTO;

@Repository
public interface LinksDAO extends JpaRepository<LinksDTO, Integer> { }
