package org.warestore.repository;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;
import org.warestore.model.Target;

@Repository
public interface TargetRepository extends CouchbaseRepository<Target, Integer> { }
