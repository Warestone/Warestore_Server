package org.warestore.repository;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.warestore.model.Category;

public interface CategoryRepository extends CouchbaseRepository<Category, Integer> { }
