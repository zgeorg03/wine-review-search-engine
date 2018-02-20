#!/usr/bin/env bash
HOST="http://localhost:8192"

COLLECTION="test-small"

# Create a collection
curl -s -X POST "$HOST/collections/$COLLECTION" | jq .

# Add multiple files
curl -s -X POST "$HOST/collections/$COLLECTION/documents" -d "directory=/home/zgeorg03/git-projects/Msc/dionysos-wine-review-search-engine/data/small/" | jq .

# Delete just a file
curl -s -X DELETE "$HOST/collections/$COLLECTION/document?document=0.txt" | jq .

# Search for wine
curl -s -X GET "$HOST/search/$COLLECTION?q=wine" | jq .

# Search for not wine
curl -s -X GET "$HOST/search/$COLLECTION?q=-wine" | jq .

# Print index
#curl -X GET "$HOST/collections/$COLLECTION/index" | jq .

# Delete the collection
#curl -X DELETE "$HOST/collections/$COLLECTION" | jq .

