package com.pennyauction.pennyauction.repository.contract;

import com.pennyauction.pennyauction.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends CrudRepository<Category, Integer> {
}
