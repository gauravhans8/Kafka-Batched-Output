package com.batchedkafka.webhook.controller;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.batchedkafka.webhook.service.KafKaProducerService;

@RestController
@RequestMapping(value = "/webhook")
public class KafkaProducerController {
	private final KafKaProducerService producerService;

	@Autowired
	public KafkaProducerController(KafKaProducerService producerService) {
		this.producerService = producerService;
	}

	@PostMapping(value = "/send")
	public ResponseEntity<String> sendMessageToKafkaTopic(@RequestBody String jsonString) {
		try{
			producerService.sendMessage(jsonString);
			return new ResponseEntity<>("Successfully Sent", HttpStatus.OK);
		} catch (IllegalArgumentException | JSONException e) {
			//bad request if json is deformed or does not contain event type field
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e){
			//server error if any other error
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}