package org.challenge.wit.rest.controller;

import org.challenge.wit.rest.message.KafkaConsumer;
import org.challenge.wit.rest.message.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculator")
@EnableKafka
public class CalculatorController {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private KafkaConsumer kafkaConsumer;

    @GetMapping("/sum")
    public String sum(@RequestParam double a, @RequestParam double b) {
        kafkaProducer.send("sum" + a + ":" + b);
        return kafkaConsumer.getResult();
    }

    @GetMapping("/sub")
    public String sub(@RequestParam double a, @RequestParam double b) {
        kafkaProducer.send("sub" + a + ":" + b);
        return kafkaConsumer.getResult();
    }

    @GetMapping("/multi")
    public String multi(@RequestParam double a, @RequestParam double b) {
        kafkaProducer.send("multi" + a + ":" + b);
        return kafkaConsumer.getResult();
    }

    @GetMapping("/div")
    public String div(@RequestParam double a, @RequestParam double b) {
        kafkaProducer.send("div" + a + ":" + b);
        return kafkaConsumer.getResult();
    }
}
