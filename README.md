# Dionysos Wine Review Search Engine
A search engine for Dionysos wine reviews

## Query Language

A very simple query language that supports *_N_* terms. Terms can negated 
using character *_-_* before the term

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
