# Dionysos Wine Review Search Engine
A search engine for Dionysos wine reviews.

## Functionality

|Endpoint                               |Description                        |
|---------------------------------------|-----------------------------------|
|**GET** /collections/                  | List all available collections        |
|**POST** /collections/{collection}     | Create a collection with the name *_collection_*  |
|**DELETE** /collections/{collection}     | Delete *_collection_*  |
|**GET** /collections/{collection}     | List all the documents from *_collection_*  |
|**GET** /collections/index                  | Retrieve the index of the  *_collection_*.|
|**GET** /collections/dictionary                  | Retrieve the dictionary of the  *_collection_*.|
|**POST** /collections/{collection}/document     | Upload a document in *_collection_*. **file** query parameter is needed.  |
|**POST** /collections/{collection}/documents     | Upload documents in *_collection_* from the **directory** query parameter.|
|**DELETE** /collections/{collection}/document     |Remove the **document** from *_collection_*  |
|**GET** /search/{collection}     | Search through the *_collection_*. It needs **q** parameter.  |


## Installation Instructions
```bash
git clone https://github.com/zgeorg03/dionysos-wine-review-search-engine.git
cd ./dionysos-wine-review-search-engine
mvn clean && mvn package
```

### How to run?
```bash
java -jar target/wine-search-engine-0.1.0.jar
```
**Important** Make sure you have changed application.properties file which is located in the root directory.
This file has 3 configuration properties:
* The port of the server: Default is 8192
* The local directory path to store collections: Default is /tmp/wines
* The path to stopwords file: Default is ./data/stoplist.txt

### How to use?
You can simply visit http://localhost:8192 which redirects to the swagger-ui. From there you 
can see and play with the REST API of the application.


### Tests
*_scripts/_* directory contains bash scripts. You can run the application and 
execute these scripts in oder to test its functionality.

## Query Language

A very simple query language that supports *_N_* query terms. Terms can be negated 
using character *_- term_*. It supports **and** and **or** operations which are represented
with *_._* and *_,_* characters respectively. 

### Examples:
* x.y.z  : Search for documents containing x and y and z terms
* x,y,z  : Search for documents containing x or y or z terms
* x.y.-z  : Search for documents containing x and y but not z terms
* x,y.z  : Search for documents containing x or y and z terms
* -x : Search for documents that do not contain the term x

### BNF Query Language:
```bash
QUERY := QUERY BOOL TERM | TERM  
TERM := STR | NEG STR
STR := [A-Za-z0-9]+
BOOL := "."  | ","
NEG  := "-"
```

