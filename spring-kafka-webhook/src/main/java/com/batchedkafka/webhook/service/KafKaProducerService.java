package com.batchedkafka.webhook.service;

import com.batchedkafka.webhook.config.YAMLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.json.*;

@Service
public class KafKaProducerService 
{
	private static final Logger logger = 
			LoggerFactory.getLogger(KafKaProducerService.class);
	
	@Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private YAMLConfig yamlConfig;
	
	public void sendMessage(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString);
			if (json.has("event_type")) {
				if (yamlConfig.getEventTopicMap().containsKey(json.getString("event_type"))) {
					String topic = yamlConfig.getEventTopicMap()
							.get(json.getString("event_type"));
					sendMessage(jsonString, topic);
				} else {
					logger.error("Event Type is not present in list of Authorized topics, Discarding message");
					throw new IllegalArgumentException("Not in Authorized Topic");
				}
			} else {
				logger.error("Invalid JSON, does not contain event type");
				throw new IllegalArgumentException("Invalid JSON");
			}
		} catch(JSONException j){
			j.printStackTrace();
			logger.error("Malformed JSON, cannot parse");
			throw new JSONException(j);
		} catch (IllegalArgumentException x){
			throw new IllegalArgumentException(x);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	public void sendMessage(String message, String topic){
		ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(topic, message);
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
			@Override
			public void onSuccess(SendResult<String, String> result) {
				logger.info("Sent message: " + message
						+ " with offset: " + result.getRecordMetadata().offset());
			}
			@Override
			public void onFailure(Throwable ex) {
				logger.error("Unable to send message : " + message, ex);
				throw new RuntimeException(ex);
			}
		});
	}
}
