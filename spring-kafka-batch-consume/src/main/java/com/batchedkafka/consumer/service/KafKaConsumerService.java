package com.batchedkafka.consumer.service;

import com.batchedkafka.consumer.Constants.AppConstants;
import com.batchedkafka.consumer.config.YAMLEventConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafKaConsumerService 
{
	private final Logger logger = LoggerFactory.getLogger(KafKaConsumerService.class);

	@Autowired
	private YAMLEventConfig configMap;

	@Autowired
	private FileSystemService fileSystem;

	//different kafka listeners for each topic
	@KafkaListener(topics = "${spring.kafka.consumer.update-event.topic}", groupId = "${spring.kafka.consumer.update-event.groupId}")
	public void consumeUpdateEvents(List<String> messages, Acknowledgment acknowledgment) {
		try {
			fileSystem.mergeAndProcessMessages(messages,
					configMap.getConsumer().get(AppConstants.UPDATE_EVENT).getFilePattern(),
					configMap.getConsumer().get(AppConstants.UPDATE_EVENT).getSizeThreshold(),
					configMap.getConsumer().get(AppConstants.UPDATE_EVENT).getTimeThreshold());
			acknowledgment.acknowledge();
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@KafkaListener(topics = "${spring.kafka.consumer.create-event.topic}", groupId = "${spring.kafka.consumer.create-event.groupId}")
	public void consumeCreateEvents(List<String> messages, Acknowledgment acknowledgment) {
		try {
			fileSystem.mergeAndProcessMessages(messages,
					configMap.getConsumer().get(AppConstants.CREATE_EVENT).getFilePattern(),
					configMap.getConsumer().get(AppConstants.CREATE_EVENT).getSizeThreshold(),
					configMap.getConsumer().get(AppConstants.CREATE_EVENT).getTimeThreshold());
			acknowledgment.acknowledge();
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@KafkaListener(topics = "${spring.kafka.consumer.delete-event.topic}", groupId = "${spring.kafka.consumer.delete-event.groupId}")
	public void consumeDeleteEvents(List<String> messages, Acknowledgment acknowledgment) {
		try {
			fileSystem.mergeAndProcessMessages(messages,
					configMap.getConsumer().get(AppConstants.DELETE_EVENT).getFilePattern(),
					configMap.getConsumer().get(AppConstants.DELETE_EVENT).getSizeThreshold(),
					configMap.getConsumer().get(AppConstants.DELETE_EVENT).getTimeThreshold());
			acknowledgment.acknowledge();
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
