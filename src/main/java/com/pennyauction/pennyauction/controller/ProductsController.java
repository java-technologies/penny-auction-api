package com.pennyauction.pennyauction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennyauction.pennyauction.model.Product;
import com.pennyauction.pennyauction.repository.contract.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@RestController
public class ProductsController {

    private static ObjectMapper mapper = new ObjectMapper();
    private ProductsRepository repository;

    static {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Autowired
    public ProductsController(ProductsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/products")
    public String list() {
        List<Product> products = (List<Product>) repository.findAll();

        try {
            return mapper.writeValueAsString(products);
        }
        catch (Exception ex) {
            return "{\"error\":\"exception raised converting products list to json\"}";
        }
    }

    @RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable int id) {
        Optional<Product> productOptional = repository.findById(id);
        if (!productOptional.isPresent()) {
            return new ResponseEntity<>("{\"error\":\"Product with such id not found\"}", HttpStatus.NOT_FOUND);
        }
        try {
            String result = mapper.writeValueAsString(productOptional.get());
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch (Exception ex) {
            String result = "{\"error\":\"exception raised converting product " + id + "to json\"}";
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/products", method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody Product product) {
        product.setId(0);
        product = repository.save(product);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @RequestMapping(value = "/products/{id}", method = RequestMethod.PUT)
    public ResponseEntity put(@PathVariable int id, @RequestBody Product product) {
        product.setId(id);
        product = repository.save(product);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable int id) {
        repository.deleteById(id);
        String result = "{\"result\":1}";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
