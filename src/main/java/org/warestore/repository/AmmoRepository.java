package org.warestore.repository;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;
import org.warestore.model.Ammo;

@Repository
public interface AmmoRepository extends CouchbaseRepository<Ammo, Integer> { }
