package org.warestore.repository;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;
import org.warestore.model.User;

@Repository
public interface UserRepository extends CouchbaseRepository<User, Integer> { }
