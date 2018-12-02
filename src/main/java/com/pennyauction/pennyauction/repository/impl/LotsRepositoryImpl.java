package com.pennyauction.pennyauction.repository.impl;

import com.pennyauction.pennyauction.model.Bid;
import com.pennyauction.pennyauction.model.Category;
import com.pennyauction.pennyauction.model.Lot;
import com.pennyauction.pennyauction.model.Product;
import com.pennyauction.pennyauction.repository.contract.LotsRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
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
        return lotsList(query);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Lot> getLotsListByUser(String uid) {
        Query query = entityManager.createNativeQuery("SELECT * FROM lots WHERE user_uid = :uid", Lot.class);
        query.setParameter("uid", uid);
        return lotsList(query);
    }

    private List<Lot> lotsList(Query query) {
        List<Lot> lots = query.getResultList();
        Map<Integer, Category> categoryMap = new HashMap<>(); // id to Category
        // Get products
        for (int i = 0; i < lots.size(); i++) {
            Lot lot = lots.get(i);
            Query productQuery = entityManager.createNativeQuery("SELECT * FROM products WHERE lot_id = :lid",
                    Product.class);
            productQuery.setParameter("lid", lot.getId());
            List<Product> results = productQuery.getResultList();
            if (results.size() > 0) lot.setProduct((Product) productQuery.getSingleResult());
            if (lot.getProduct() != null) {
                categoryMap.put(lot.getProduct().getCategoryId(), null);
            }
            else {
                lots.remove(i);
                i--;
            }
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

    @SuppressWarnings("unchecked")
    @Override
    public Lot getLotById(int id) {
        Query query = entityManager.createNativeQuery("SELECT * FROM lots WHERE id = :id", Lot.class);
        query.setParameter("id", id);

        // Check if such lot present
        List<Lot> lots = (List<Lot>) query.getResultList();
        if (lots == null || lots.size() == 0) return null;
        Lot lot = lots.get(0);

        // Get product
        Query productQuery = entityManager.createNativeQuery("SELECT * FROM products WHERE lot_id = :lid",
                Product.class);
        productQuery.setParameter("lid", lot.getId());
        lot.setProduct((Product) productQuery.getSingleResult());
        if (lot.getProduct() != null) {
            // Get category
            Query categoryQuery = entityManager.createNativeQuery("SELECT * FROM categories WHERE id = :cid",
                    Category.class);
            categoryQuery.setParameter("cid", lot.getProduct().getCategoryId());
            lot.getProduct().setCategory((Category) categoryQuery.getSingleResult());
        }

        return lot;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Bid> getBidsList(int lotId) {
        Query query = entityManager.createNativeQuery("SELECT * FROM bids WHERE lot_id = :id", Bid.class);
        query.setParameter("id", lotId);
        return (List<Bid>) query.getResultList();
    }

    /**
     * Create bid entry in DB
     * @param bid bid object
     * @return id of created bid, -1 if bid value is too low, -2 if another error
     */
    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public int saveBid(Bid bid) {
        entityManager.merge(bid);
        String queryStr = "SELECT * FROM bids WHERE txid = :txid ORDER BY id DESC";
        Query query = entityManager.createNativeQuery(queryStr, Bid.class);
        query.setParameter("txid", bid.getTxid());
        List<Bid> bids = (List<Bid>) query.getResultList();
        if (bids != null && bids.size() > 0) return bids.get(0).getId();
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public int save(Lot lot, Product product) {
        entityManager.merge(lot);
        String queryStr = "SELECT * FROM lots ORDER BY id DESC LIMIT 1";
        Query query = entityManager.createNativeQuery(queryStr, Lot.class);
        List<Lot> lots = (List<Lot>) query.getResultList();
        if (lots == null || lots.size() == 0) return 0;
        int id = lots.get(0).getId();
        product.setLotId(id);
        entityManager.merge(product);
        queryStr = "SELECT * FROM products ORDER BY id DESC LIMIT 1";
        query = entityManager.createNativeQuery(queryStr, Product.class);
        List<Product> products = (List<Product>) query.getResultList();
        if (products == null || products.size() == 0) return 0;
        return id;
    }
}
