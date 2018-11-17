package com.pennyauction.pennyauction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pennyauction.pennyauction.model.Lot;
import com.pennyauction.pennyauction.model.Product;
import com.pennyauction.pennyauction.repository.contract.LotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;

@RestController
public class LotsController {

    private static ObjectMapper mapper = new ObjectMapper();
    private LotsRepository lotsRepository;

    @Autowired
    public LotsController(LotsRepository lotsRepository) {
        this.lotsRepository = lotsRepository;
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @GetMapping("/lots")
    public String list() {
        List<Lot> lots = lotsRepository.getLotsList();
        ArrayNode array = mapper.createArrayNode();

        for (Lot lot : lots) {
            ObjectNode node = mapper.valueToTree(lot);
            node.remove("product");
            Product product = lot.getProduct();
            node.put("product_id", product.getId());
            node.put("product_name", product.getName());
            node.put("product_description", product.getDescription());
            node.putPOJO("category", product.getCategory());
            array.add(node);
        }

        try {
            return mapper.writeValueAsString(array);
        }
        catch (Exception ex) {
            return "{\"error\":\"exception raised converting lots list to json\"}";
        }
    }

    @RequestMapping(value = "/lots/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable int id) {
        Lot lot = lotsRepository.getLotById(id);
        if (lot == null) {
            return new ResponseEntity<>("{\"error\":\"Lot with such id not found\"}", HttpStatus.NOT_FOUND);
        }
        try {
            String result = mapper.writeValueAsString(lot);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch (Exception ex) {
            String result = "{\"error\":\"exception raised converting lot " + id + "to json\"}";
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
