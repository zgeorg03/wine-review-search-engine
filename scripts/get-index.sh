#!/usr/bin/env bash
HOST="http://localhost:8192"

COLLECTION="test"

curl -X GET "$HOST/collections/$COLLECTION/index"
