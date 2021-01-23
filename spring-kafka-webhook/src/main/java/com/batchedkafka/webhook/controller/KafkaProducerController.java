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
			return new ResponseEntity<String>("Successfully Sent", HttpStatus.OK);
		} catch (IllegalArgumentException | JSONException e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} catch (Exception e){
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		//this.producerService.sendMessage();
	}
}