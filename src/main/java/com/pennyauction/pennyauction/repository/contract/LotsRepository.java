package com.pennyauction.pennyauction.repository.contract;

import com.pennyauction.pennyauction.model.Lot;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotsRepository {
    List<Lot> getLotsList();
    Lot getLotById(int id);
}
