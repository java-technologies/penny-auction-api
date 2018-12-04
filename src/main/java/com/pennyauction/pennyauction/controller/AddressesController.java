package com.pennyauction.pennyauction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennyauction.pennyauction.model.Address;
import com.pennyauction.pennyauction.repository.contract.AddressesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@RestController
public class AddressesController {

    private static ObjectMapper mapper = new ObjectMapper();
    private AddressesRepository repository;

    static {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Autowired
    public AddressesController(AddressesRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/addresses")
    public String list() {
        List<Address> addresses = (List<Address>) repository.findAll();

        try {
            return mapper.writeValueAsString(addresses);
        }
        catch (Exception ex) {
            return "{\"error\":\"exception raised converting addresses list to json\"}";
        }
    }

    @RequestMapping(value = "/addresses/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable int id) {
        Optional<Address> addressOptional = repository.findById(id);
        if (!addressOptional.isPresent()) {
            return new ResponseEntity<>("{\"error\":\"Address with such id not found\"}", HttpStatus.NOT_FOUND);
        }
        try {
            String result = mapper.writeValueAsString(addressOptional.get());
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch (Exception ex) {
            String result = "{\"error\":\"exception raised converting address " + id + "to json\"}";
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/addresses", method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody Address address) {
        address.setId(0);
        address = repository.save(address);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @RequestMapping(value = "/addresses/{id}", method = RequestMethod.PUT)
    public ResponseEntity put(@PathVariable int id, @RequestBody Address address) {
        address.setId(id);
        address = repository.save(address);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @RequestMapping(value = "/addresses/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable int id) {
        repository.deleteById(id);
        String result = "{\"result\":1}";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
