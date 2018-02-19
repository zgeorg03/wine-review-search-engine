#!/usr/bin/env bash
HOST="http://localhost:8192"

COLLECTION="test"

curl -X GET "$HOST/search/$COLLECTION?q=wine" | echo "Results:$(jq -r '.count')" 
curl -X GET "$HOST/search/$COLLECTION?q=wine.blue" | echo "Results:$(jq -r '.count')" 
curl -X GET "$HOST/search/$COLLECTION?q=wine.-blue" | echo "Results:$(jq -r '.count')" 
curl -X GET "$HOST/search/$COLLECTION?q=wine,blue" | echo "Results:$(jq -r '.count')" 

