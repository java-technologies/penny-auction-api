package com.pennyauction.pennyauction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pennyauction.pennyauction.kafka.KafkaSender;
import com.pennyauction.pennyauction.model.Bid;
import com.pennyauction.pennyauction.model.Lot;
import com.pennyauction.pennyauction.model.Product;
import com.pennyauction.pennyauction.repository.contract.LotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class LotsController {

    private static ObjectMapper mapper = new ObjectMapper();
    private LotsRepository lotsRepository;

    @Autowired
    KafkaSender kafkaSender;

    @Autowired
    public LotsController(LotsRepository lotsRepository) {
        this.lotsRepository = lotsRepository;
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }


    @GetMapping("/lots")
    public String list() {
        List<Lot> lots = lotsRepository.getLotsList();
        ArrayNode array = lotsToJson(lots);

        try {
            return mapper.writeValueAsString(array);
        }
        catch (Exception ex) {
            return "{\"error\":\"exception raised converting lots list to json\"}";
        }
    }

    @GetMapping("/users/{uid}/lots")
    public String listByUser(@PathVariable String uid) {
        List<Lot> lots = lotsRepository.getLotsListByUser(uid);
        ArrayNode array = lotsToJson(lots);

        try {
            return mapper.writeValueAsString(array);
        }
        catch (Exception ex) {
            return "{\"error\":\"exception raised converting lots list to json\"}";
        }
    }

    @GetMapping("/self/lots")
    public String listByUser(Authentication authentication) {
        System.out.println(authentication);
        List<Lot> lots = lotsRepository.getLotsListByUser(authentication.getPrincipal().toString());
        ArrayNode array = lotsToJson(lots);

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
            ObjectNode node = mapper.valueToTree(lot);
            node.remove("product");
            Product product = lot.getProduct();
            node.put("product_id", product.getId());
            node.put("product_name", product.getName());
            node.put("product_description", product.getDescription());
            node.put("photo", product.getPhoto());
            node.putPOJO("category", product.getCategory());

            return new ResponseEntity<>(node, HttpStatus.OK);
        }
        catch (Exception ex) {
            String result = "{\"error\":\"exception raised converting lot " + id + "to json\"}";
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/lots", method = RequestMethod.POST)
    public ResponseEntity post(Authentication authentication, @RequestBody ObjectNode node) {
        Lot lot = new Lot();
        lot.setUserUid(authentication.getPrincipal().toString());
        lot.setStartPrice((float) node.get("start_price").asDouble());
        lot.setFinalPrice(lot.getStartPrice());
        lot.setState("created");
        lot.setStartDate(new Date());
        Product product = new Product();
        product.setName(node.get("product_name").asText());
        product.setDescription(node.get("product_description").asText());
        product.setCategoryId(node.get("category_id").asInt());
        product.setPhoto(node.get("photo").asText());

        int id = lotsRepository.save(lot, product);

        kafkaSender.send("lot_creator-topic", "{lot_id: " + id + "}");

        return get(id);
    }

    @RequestMapping(value = "/lots/{lotId}/bids", method = RequestMethod.GET)
    public ResponseEntity getBids(@PathVariable int lotId) {
        List<Bid> bids = lotsRepository.getBidsList(lotId);
        if (bids == null) {
            return new ResponseEntity<>("{\"error\":\"Lot with such id does not exist\"}", HttpStatus.NOT_FOUND);
        }
        try {
            String result = mapper.writeValueAsString(bids);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch (Exception ex) {
            String result = "{\"error\":\"exception raised converting bids for lot " + lotId + "to json\"}";
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/lots/{lotId}/bids", method = RequestMethod.POST)
    public ResponseEntity post(@PathVariable int lotId, @RequestBody Bid bid) {
        // Check if lot exists
        Lot lot = lotsRepository.getLotById(lotId);
        if (lot == null) {
            return new ResponseEntity<>("{\"error\":\"Lot with such id does not exist\"}", HttpStatus.NOT_FOUND);
        }

        // TODO: 11/17/18 Check if lot is active

        // Place bid
        bid.setId(0);
        bid.setLotId(lotId);
        int id = lotsRepository.saveBid(bid);
        bid.setId(id);

//        not sure if we need this
        kafkaSender.send("bid_creator-topic", "{bid_id: " + id + "}");

        return new ResponseEntity<>(bid, HttpStatus.OK);
    }

    private ArrayNode lotsToJson(List<Lot> lots) {
        ArrayNode array = mapper.createArrayNode();

        for (Lot lot : lots) {
            ObjectNode node = mapper.valueToTree(lot);
            node.remove("product");
            Product product = lot.getProduct();
            node.put("product_id", product.getId());
            node.put("product_name", product.getName());
            node.put("product_description", product.getDescription());
            node.put("photo", product.getPhoto());
            node.putPOJO("category", product.getCategory());
            array.add(node);
        }

        return array;
    }
}
