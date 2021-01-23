package com.batchedkafka.consumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.kafka")
public class YAMLEventConfig {

    //Maps consumer properties to Event class -> contains thresholds, group ids and topic names

    private Map<String, Event> consumer;

    public Map<String, Event> getConsumer() {
        return consumer;
    }

    public void setConsumer(Map<String, Event> consumer) {
        this.consumer = consumer;
    }

    public static class Event {

        private String groupId;

        private String topic;

        private int sizeThreshold;

        private int timeThreshold;

        private String filePattern;

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public int getSizeThreshold() {
            return sizeThreshold;
        }

        public void setSizeThreshold(int sizeThreshold) {
            this.sizeThreshold = sizeThreshold;
        }

        public int getTimeThreshold() {
            return timeThreshold;
        }

        public void setTimeThreshold(int timeThreshold) {
            this.timeThreshold = timeThreshold;
        }

        public String getFilePattern() {
            return filePattern;
        }

        public void setFilePattern(String filePattern) {
            this.filePattern = filePattern;
        }
    }
}