package org.warestore.repository;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;
import org.warestore.model.Order;

@Repository
public interface OrderRepository extends CouchbaseRepository<Order, Integer> { }
