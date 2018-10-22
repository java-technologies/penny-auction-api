package com.pennyauction.pennyauction.repository.impl;

import com.pennyauction.pennyauction.model.Category;
import com.pennyauction.pennyauction.model.Lot;
import com.pennyauction.pennyauction.model.Product;
import com.pennyauction.pennyauction.repository.contract.LotsRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class LotsRepositoryImpl implements LotsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<Lot> getLotsList() {
        Query query = entityManager.createNativeQuery("SELECT * FROM lots", Lot.class);
        List<Lot> lots = query.getResultList();
        Map<Integer, Category> categoryMap = new HashMap<>(); // id to Category
        // Get products
        for (Lot lot : lots) {
            Query productQuery = entityManager.createNativeQuery("SELECT * FROM products WHERE lot_id = :lid", Product.class);
            productQuery.setParameter("lid", lot.getId());
            List<Product> results = productQuery.getResultList();
            if (results.size() > 0) lot.setProduct((Product) productQuery.getSingleResult());
            if (lot.getProduct() != null) categoryMap.put(lot.getProduct().getId(), null);
            lot.setStartDate(new Date());
        }
        // Get categories
        if (categoryMap.keySet().size() > 0) {
            StringBuilder categoriesQueryStr = new StringBuilder("SELECT * FROM categories WHERE id IN (");
            for (Integer i : categoryMap.keySet()) categoriesQueryStr.append(i).append(",");
            categoriesQueryStr.replace(categoriesQueryStr.length() - 1, categoriesQueryStr.length(), ")");
            Query categoriesQuery = entityManager.createNativeQuery(categoriesQueryStr.toString(), Category.class);
            for (Object c : categoriesQuery.getResultList()) {
                Category category = (Category) c;
                categoryMap.put(category.getId(), category);
            }
        }
        // Set categories to products
        for (Lot lot : lots) lot.getProduct().setCategory(categoryMap.get(lot.getProduct().getCategoryId()));

        // Return list
        return lots;
    }
}
