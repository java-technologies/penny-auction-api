package com.pennyauction.pennyauction.repository.contract;

import com.pennyauction.pennyauction.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends CrudRepository<Product, Integer> {
}
