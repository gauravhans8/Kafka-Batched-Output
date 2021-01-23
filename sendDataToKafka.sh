#!/bin/bash
var=0
while IFS= read -r line; do
    #echo $line
    #echo "########################"
    curl -d "$line" -H 'Content-Type: application/json' http://localhost:9000/webhook/send
    var=$((var+1))
    if [ $var -eq 25 ]; then
	var=0
	sleep 2	
    fi
done < loadTest.json
