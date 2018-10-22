package com.pennyauction.pennyauction.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pennyauction.pennyauction.model.Lot;
import com.pennyauction.pennyauction.repository.contract.LotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.List;

@RestController
public class LotsController {

    private LotsRepository lotsRepository;

    @Autowired
    public LotsController(LotsRepository lotsRepository) {
        this.lotsRepository = lotsRepository;
    }

    @GetMapping("/lots")
    public String list() {
        List<Lot> lots = lotsRepository.getLotsList();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        try {
            return mapper.writeValueAsString(lots);
        }
        catch (Exception ex) {
            return "{\"error\":\"exception raised converting lots list to json\"}";
        }
    }
}
