#!/usr/bin/env bash
HOST="http://localhost:8192"

COLLECTION="test"

# Create a collection
curl -X POST "$HOST/collections/$COLLECTION"

# Add multiple files
curl -X POST "$HOST/collections/$COLLECTION/documents" -d "directory=/home/zgeorg03/git-projects/Msc/dionysos-wine-review-search-engine/data/example/"

