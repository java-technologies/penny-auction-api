package com.pennyauction.pennyauction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennyauction.pennyauction.model.Category;
import com.pennyauction.pennyauction.repository.contract.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@RestController
public class CategoriesController {

    private static ObjectMapper mapper = new ObjectMapper();
    private CategoriesRepository categoriesRepository;

    static {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Autowired
    public CategoriesController(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @GetMapping("/categories")
    public String list() {
        List<Category> categories = (List<Category>) categoriesRepository.findAll();

        try {
            return mapper.writeValueAsString(categories);
        }
        catch (Exception ex) {
            return "{\"error\":\"exception raised converting categories list to json\"}";
        }
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable int id) {
        Optional<Category> categoryOptional = categoriesRepository.findById(id);
        if (!categoryOptional.isPresent()) {
            return new ResponseEntity<>("{\"error\":\"Category with such id not found\"}", HttpStatus.NOT_FOUND);
        }
        try {
            String result = mapper.writeValueAsString(categoryOptional.get());
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch (Exception ex) {
            String result = "{\"error\":\"exception raised converting category " + id + "to json\"}";
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody Category category) {
        category.setId(0);
        category = categoriesRepository.save(category);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.PUT)
    public ResponseEntity put(@PathVariable int id, @RequestBody Category category) {
        category.setId(id);
        category = categoriesRepository.save(category);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable int id) {
        categoriesRepository.deleteById(id);
        String result = "{\"result\":1}";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
