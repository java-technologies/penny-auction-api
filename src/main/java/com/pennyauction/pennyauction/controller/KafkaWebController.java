package com.pennyauction.pennyauction.controller;

import com.pennyauction.pennyauction.kafka.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class KafkaWebController {

    @Autowired
    KafkaSender kafkaSender;

    @CrossOrigin(origins = "*")
    @PostMapping("/kafka/{topicName}")
    public String sendToTopic(@PathVariable String topicName, @RequestBody String message) {
        System.out.println("try to send message to " + topicName);
        kafkaSender.send(topicName, message);
        return "Message sent";
    }

}
