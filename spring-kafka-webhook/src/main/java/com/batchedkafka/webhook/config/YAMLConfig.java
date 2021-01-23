package com.batchedkafka.webhook.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring")
public class YAMLConfig {

    //get event to topic mapping
    Map<String,String> eventTopicMap;

    public Map<String, String> getEventTopicMap() {
        return eventTopicMap;
    }

    public void setEventTopicMap(Map<String, String> eventTopicMap) {
        this.eventTopicMap = eventTopicMap;
    }
}
