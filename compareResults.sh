#!/bin/bash
WORKING_DIR="/home/hans/batchedKafkaOutput"
echo "number of json data in : "
echo "Create Events batch files :" `cat $WORKING_DIR/create_events_* | wc -l`
echo "Update Events batch files :" `cat $WORKING_DIR/update_events_* | wc -l`
echo "Delete Events batch files :" `cat $WORKING_DIR/delete_events_* | wc -l`

echo "number of json data in original test file :: "
echo "Create events : " $( cat ~/Desktop/Kafka-Batch-Output/loadTest.json | grep 'create-event' | wc -l )
echo "Update events : " $( cat ~/Desktop/Kafka-Batch-Output/loadTest.json | grep 'update-event' | wc -l )
echo "Delete events : " $( cat ~/Desktop/Kafka-Batch-Output/loadTest.json | grep 'delete-event' | wc -l )
