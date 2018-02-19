#!/usr/bin/env bash
HOST="http://localhost:8192"

COLLECTION="test"

# Create a collection
curl -X GET "$HOST/collections/"
