# Dionysos Wine Review Search Engine
A search engine for Dionysos wine reviews.

## Description
The program has been developed using [Spring Boot](https://projects.spring.io/spring-boot/), a framework for building
web applications. The application consists of a Restful API which exposes the core functionality of the Search Engine.

### High-Level Architecture
![High Level Architecture](https://www.cs.ucy.ac.cy/~zgeorg03/public/wine-search-engine/architecture.png)

#### REST API
This layer consists of two controllers. The **DocumentCollectionController** exposes the functionality of creating
or deleting a collection, inserting or removing documents and retrieving the index and dictionary of a collection. 
The **SearchController** exposes the functionality of querying a collection of documents.
Both controllers receive a request from a client and use the **DocumentCollectionService** to send back a respond.

#### Core Modules

The **DocumentCollectionService** exposes and implements all the functionality of the search engine, using the 
**CollectionManager**. **CollectionManager** is the most important class, implementing the core functionality of the program.
It holds all **Collections** in the main memory.

**Collections** is hashmap data structure, mapping a the name of a collection to the **Collection** class.
**Collection** is the class that contains the **CollectionIndex** and a hashmap for mapping document ids to filenames.  
**Collection** also exposes the important function of adding a document. 

The add document function works as follows:
1. The document is loaded in main memory
2. The document is parsed. The first field contains the id and the 3rd field contains the description, which is indexed.
3. The description field is tokenized (based on whitespaces and some symbols)
4. Each term is checked if is a stop word using the **StopListService**
5. If term is not a stop word then the term passes through the Porter stemmer, using the **StemmerService**
6. Finally the term is added to the **CollectionIndex**

The **CollectionIndex**, is a tree map structure having the term as a key and a **PostingList** as a value
The **PostingList** is another tree map structure having the document ID as a key and a **PositionsEntry** as a value. 
It also holds the term frequency. Finally, the **PositionsEntry** is a set of integers that holds the positions of the term 
in the document.

The **CollectionIndex** implements also the functionality of search. It support N query terms which can be negated and 
concatenated using the boolean functions __and__ and __or__. It works simply starting from the most left term and depending on the boolean 
condition it will intersect or union the two sets.

For deleting a document, the program goes through the Collection's index and remove posting entries of that document.

The **Utils** class implements 3 simple algorithms:
* The intersection of two sorted sets
* The union of two sets
* The difference between two sets


#### Data Layer
Finally, in the Data layer the **FileManager** is responsible for creating/deleting 
directories for collections and files for documents.
After inserting/deleting a collection/document the **FileManager** performs the necessary operations to keep the **CollectionsManager** class 
persistent. To achieve this, all classes used by the **CollectionsManager** implement Serializable interface.

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
**Important** Make sure you have changed application.properties file located in the root directory.
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

