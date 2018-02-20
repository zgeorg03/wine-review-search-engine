package com.zgeorg03.api.controllers;

import com.zgeorg03.api.services.DocumentCollectionService;
import com.zgeorg03.core.CollectionIndex;
import com.zgeorg03.exceptions.*;
import com.zgeorg03.models.CollectionInfo;
import com.zgeorg03.models.InfoObjectResponse;
import com.zgeorg03.models.InfoResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/collections")
@Api(tags = "Collections Service", description = "Collection Service Description")
public class DocumentCollectionController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentCollectionController.class);

    private DocumentCollectionService documentCollectionService;

    public DocumentCollectionService getDocumentCollectionService() {
        return documentCollectionService;
    }

    @Autowired()
    public void setDocumentCollectionService(DocumentCollectionService documentCollectionService) {
        this.documentCollectionService = documentCollectionService;
    }

    /**
     * List available Collections
     * @return
     */
    @RequestMapping(value = "/",method = RequestMethod.GET)
    @ApiOperation(value = "List available collections")
    public List<CollectionInfo> listCollections(){
        return documentCollectionService.getCollections();
    }


    /**
     * List documents from a collection
     * @param collection
     * @return
     */
    @RequestMapping(value = "/{collection}",method = RequestMethod.GET)
    @ApiOperation(value = "List documents from a collection")
    public ResponseEntity<InfoObjectResponse<Collection<String>>> listDocumentsFromCollection(@PathVariable String collection){
        try {
            return ResponseEntity.ok(new InfoObjectResponse<>("Collection Found",
                    documentCollectionService.getDocuments(collection)));
        } catch (CollectionNotFound e) {
            logger.info(e.getLocalizedMessage());
            return ResponseEntity.ok(new InfoObjectResponse <>("Collection "+collection+" already exists", new LinkedList<String>()));
        }
    }

    /**
     * Retrieve Collection's index
     * @param collection
     * @return
     */
    @RequestMapping(value = "/{collection}/index",method = RequestMethod.GET)
    @ApiOperation(value = "Retrieve Collection's index")
    public ResponseEntity<InfoObjectResponse<CollectionIndex>> getCollectionIndex(@PathVariable String collection){
        try {
            CollectionIndex index = documentCollectionService.getCollectionIndex(collection);
            return ResponseEntity.ok(new InfoObjectResponse<>("Collection Index Found",
                    index));
        } catch (CollectionNotFound e) {
            logger.info(e.getLocalizedMessage());
            return ResponseEntity.ok(new InfoObjectResponse <>("Collection "+collection+" not found",
                    null));
        }
    }
    /**
     * Retrieve Collection's dictionary
     * @param collection
     * @return
     */
    @RequestMapping(value = "/{collection}/dictionary",method = RequestMethod.GET)
    @ApiOperation(value = "Retrieve Collection's dictionary")
    public ResponseEntity<InfoObjectResponse<Set<String>>> getCollectionDictionary(@PathVariable String collection){
        try {
            Set<String> dictionary = documentCollectionService.getCollectionDictionary(collection);
            return ResponseEntity.ok(new InfoObjectResponse<>("Total terms:"+dictionary.size(), dictionary));
        } catch (CollectionNotFound e) {
            logger.info(e.getLocalizedMessage());
            return ResponseEntity.ok(new InfoObjectResponse <>("Collection "+collection+" not found",
                    null));
        }
    }


    /**
     * Create a new collection
     * @param collection
     * @return
     */
    @RequestMapping(value = "/{collection}",method = RequestMethod.POST)
    @ApiOperation(value = "Create a new collection")
    public ResponseEntity<InfoResponse> createNewCollection(@PathVariable String collection){
        try {

            documentCollectionService.createNewCollection(collection);
            return new ResponseEntity<>(new InfoResponse("Collection " + collection + " created"), HttpStatus.CREATED);
        } catch (CollectionAlreadyExists collectionAlreadyExists) {
            logger.info(collectionAlreadyExists.getLocalizedMessage());
            return ResponseEntity.ok(new InfoResponse("Collection "+collection+" already exists"));
        } catch (NoPermissionsException e) {
            logger.info(e.getLocalizedMessage());
            return ResponseEntity.ok(new InfoResponse("No permissions to write "+collection));
        }
    }


    /**
     * Delete a collection
     * @param collection
     * @return
     */
    @RequestMapping(value = "/{collection}",method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete an existing collection")
    public ResponseEntity<InfoResponse> deleteCollection(@PathVariable String collection){
        try {

            documentCollectionService.deleteCollection(collection);
            return ResponseEntity.ok(new InfoResponse("Collection "+collection+" deleted"));

        } catch (CollectionNotFound e) {
            logger.info(e.getLocalizedMessage());
            return ResponseEntity.ok(new InfoResponse("Collection "+collection+" not found"));
        } catch (FileNotDeleted e) {
            logger.info(e.getLocalizedMessage());
            return ResponseEntity.ok(new InfoResponse("Files couldn't be deleted for collection "+collection));
        } catch (NoPermissionsException e) {
            logger.info(e.getLocalizedMessage());
            return ResponseEntity.ok(new InfoResponse("No permissions to write "+collection));
        }
    }

    @RequestMapping(value = "/{collection}/document",method = RequestMethod.POST)
    @ApiOperation(value = "Insert document to an existing collection")

    public ResponseEntity<InfoResponse> insertDocument(@PathVariable String collection, @RequestParam("file") MultipartFile file){
        String name = file.getName();
        try {
            documentCollectionService.insertDocument(collection, file);
            return ResponseEntity.ok(new InfoResponse("Document "+name+" uploaded successfully"));
        } catch (IOException e) {
            logger.info(e.getLocalizedMessage());
            return ResponseEntity.ok(new InfoResponse("Document "+name+" couldn't be added"));
        } catch (DocumentAlreadyExists e) {
            logger.info(e.getLocalizedMessage());
            return ResponseEntity.ok(new InfoResponse("Document "+name+" already exists"));
        } catch (CollectionNotFound e) {
            logger.info(e.getLocalizedMessage());
            return ResponseEntity.ok(new InfoResponse("Collection "+collection+" not found"));
        } catch (DocumentFormatNotValid e) {
            logger.info(e.getLocalizedMessage());
            return ResponseEntity.ok(new InfoResponse("Document "+name+" format is not valid"));
        }
    }

    @RequestMapping(value = "/{collection}/documents",method = RequestMethod.POST)
    @ApiOperation(value = "Insert multiple documents to an existing collection")
    public ResponseEntity<InfoResponse> insertDocuments(@PathVariable String collection, @RequestParam("directory") String path){
        File dir = Paths.get(path).toFile();
        if(!dir.isDirectory())
            return ResponseEntity.ok(new InfoResponse("Path "+collection+" is not a directory"));

        int time = 0;
        try {
            time = (int) (documentCollectionService.insertDocuments(collection, dir));
        } catch (CollectionNotFound e) {
            return ResponseEntity.ok(new InfoResponse("Collection "+collection+" not found"));
        }

        return ResponseEntity.ok(new InfoResponse("Documents have been uploaded in "+time+"ms"));

    }

    @RequestMapping(value = "/{collection}/document",method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete document from a collection")
    public ResponseEntity<InfoResponse> deleteDocument(@PathVariable String collection, @RequestParam("document") String document){
        try{
            documentCollectionService.deleteDocument(collection,document);
            return ResponseEntity.ok(new InfoResponse("Document "+document+" deleted from collection "+collection));
        } catch (CollectionNotFound e) {
            return ResponseEntity.ok(new InfoResponse("Collection "+collection+" not found"));
        } catch (DocumentNotExists e) {
            return ResponseEntity.ok(new InfoResponse(e.getLocalizedMessage()));
        }
    }

}
