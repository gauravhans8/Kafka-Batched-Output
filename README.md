# Kafka-Batched-Output
A project that takes event data from a webhook, pushes it into kafka and creates batched file on a file system

## Requirements
  - JDK 1.8 or higher
  - Apache Maven 3.1.0 or higher
  - Kafka 2.7.0
## How to Run
  - Start Zookeeper and Kafka with replication 1 (for local testing only)
  - Create 3 Topics
    - fynd_create
    - fynd_update
    - fynd_delete
   - Configure Home directory in consumer module's application yaml (output directory of the project)
   - mvn package both the projects
   - run both as java -jar target/JAR-NAME
   - Execute sendToKafka.sh script
   
# Architecture
![alt text](https://github.com/gauravhans8/Kafka-Batched-Output/blob/main/ArchitectureDiag.png)

## Flow Explanation
  - Shell script takes an input file of json (mixed data of 3 type of events).
  - Posts them one by one to single webhook end point.
  - Webhook endpoint checks for data discrepancy and event type fields and its values.
  - Discards any data that does not satisfy integrity checks (prevents malformed data to be in kafka).
  - Sends through checked data forward to mapped topics per event type.
  - Consumer start consuming data in batches (currently dynamic bathces as per kafka).
  - Per topic :
    - There are three kinds of file - Stage file, Stat file and Final file.
    - Write process starts.
    - Stat File's content is checked for creation time of stage file, if the difference of time is greater than threshold
      then stage file is moved to final file.
     - Stage file is appended or created with new batch data (as per previous step)
     - Checks if the size of stage file is greater than the threshold, if yes, then rotates.
        (Handles the cases where single batch size is greater than the threshold)
     - Stat file is updated with either new stage file creation time or old creation time (in case of no rotation)

#### Additional Notes
- For simplicity size threshold is in kBs and time threshold is in seconds(so for minutes you would have to put in multiples of 60)
- Make sure the output directory is created beforehand.
- Make sure to enter that output directory in testing script too and also edit the directory where source file is kept.
- NiFi can easily be used for the whole consume process (much better fault tolerant and configurable behavior and whole flow can be
can be replicated easily for new topics)
- Apache spark was overkill as no processing needed to be done on the consume side and lesser control over output files structure and name and sizes.
- New services can be easily added in the spring project as its decoupled from kafka consumption process (services like HDFS service)

# Module Descriptions

### spring-kafka-webhook
```
A kafka producer + rest service that acsts as a webhook for clients who want to send event data. 
Clients POST json data to this service and service sends them over to kafka.
```
Features
  - Configurable authorized topics (add them through application.yaml)
  - Discards any request with malformed data
  - Does not allow any data without event_type field to pass through
  - Does not allow any data with unathorized topic to pass through
  - Automatic topic inferring through event_type field and yaml config
  
Improvements
  - Much better tuned kafka producer (ack, in-sync replica, multiple threads)
  - Cluster environment is not tested
#
### spring-kafka-batch-consume
```
A kafka consumer service that writes output in batches per topic
```
Features
  - Individual Topic configurations of size and time Thresholds
  - Output File Pattern for each topic (act as prefixes)
  - Concurrency control for kafka consumers
  - Batch Consumption
  - Manual acknowledgment (after each batch consumed, roll back if failed)
  - For any new event to be added for consumption only one kafka listener(boiler plate code) and yaml config needs to be added in code

Improvements
  - Greater tuning for individual topics (for eg : concurrency for each topic instead of one single property)
  - Schedulers that periodically rotate file as per topic (only needed if no data is received and time threshold has passed)
    - Currently as soon as new batch comes for writing, thresolds are checked and files are rotated
  - Further tuning of kafka consumers using dynamically calculated fetch max bytes and max poll records property
    - Dynamic as in based on size thresholds (it should be sufficiently small (but not too small) so that file can be roatated
      with minimal deviation from size thresholds
