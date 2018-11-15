package com.pennyauction.pennyauction.repository.contract;

import com.pennyauction.pennyauction.model.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressesRepository extends CrudRepository<Address, Integer> {
}
