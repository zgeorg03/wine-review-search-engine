package com.zgeorg03.api.services;

import com.zgeorg03.StemmerService;
import com.zgeorg03.StopListService;
import com.zgeorg03.core.*;
import com.zgeorg03.exceptions.*;
import com.zgeorg03.models.CollectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class DocumentCollectionService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentCollectionService.class);

    private final CollectionsManager collectionsManager;

    private StopListService stopListService;
    @Autowired
    public void setStopListService(StopListService stopListService){
        this.stopListService = stopListService;
    }
    private StemmerService stemmerService;

    @Autowired
    public void setStemmerService(StemmerService stemmerService) {
        this.stemmerService = stemmerService;
    }

    @Autowired
    public DocumentCollectionService(CollectionsManager collectionsManager) {
        this.collectionsManager = collectionsManager;
    }

    /**
     * Create a new collection
     * @param name
     * @return
     * @throws CollectionAlreadyExists
     */
    public boolean createNewCollection(String name) throws CollectionAlreadyExists, NoPermissionsException {

        // Load the collection to memory
        collectionsManager.addNewCollection(name);

        return true;
    }

    /**
     *Delete an existing collection
     * @param name
     * @return
     * @throws CollectionNotFound
     */
    public boolean deleteCollection(String name) throws CollectionNotFound, FileNotDeleted, NoPermissionsException {
        logger.info("Deleting new collection: " +name);
        collectionsManager.deleteCollection(name);
        return true;
    }

    /**
     *Insert document to an existing collection
     * @param collectionName
     * @param file
     * @return
     */
    public boolean insertDocument(String collectionName, MultipartFile file) throws IOException, DocumentAlreadyExists, CollectionNotFound, DocumentFormatNotValid {
        return collectionsManager.insertDocument(collectionName, file);
    }

    public long insertDocuments(String collection, File dir) throws CollectionNotFound {
     return collectionsManager.insertDocuments(collection,dir);
    }
    /**
     *Delete document from an existing collection
     * @param collectionName
     * @param document
     * @return
     */
    public boolean deleteDocument(String collectionName, String document) throws CollectionNotFound, DocumentNotExists {
        logger.info("Deleting document "+document +" from collection: " +collectionName);
        collectionsManager.deleteDocument(collectionName,document);
        return true;
    }


    public List<CollectionInfo> getCollections() {
        return collectionsManager.getCollections();
    }

    /**
     * Return the documents from a collection
     * @param collection
     * @return
     * @throws CollectionNotFound
     */
    public Collection<String> getDocuments(String collection) throws CollectionNotFound {
        return collectionsManager.getCollectionDocuments(collection);
    }

    /**
     *
     * @param collection
     * @return
     */
    public CollectionIndex getCollectionIndex(String collection) throws CollectionNotFound {
        return collectionsManager.getCollectionIndex(collection);
    }

    public Set<String> getCollectionDictionary(String collection) throws CollectionNotFound {
        return collectionsManager.getCollectionDictionary(collection);

    }

    public SearchResponse search(String collection, String q) throws CollectionNotFound, QueryFormatNotValid, IOException, DocumentNotExists {

        List<DocumentInfo> documentInfoList = new LinkedList<>();

        CollectionIndex collectionIndex = collectionsManager.getCollectionIndex(collection);

        long time = System.currentTimeMillis();

        Query query = new Query(q);
        for(Integer docId: collectionIndex.search(query,stopListService,stemmerService)){
            DocumentInfo documentInfo = collectionsManager.getDocumentContent(collection,docId);
            documentInfoList.add(documentInfo);
        }
        time = System.currentTimeMillis()-time;

        SearchResponse searchResponse = new SearchResponse("Ok");
        searchResponse.setQuery(q);
        searchResponse.setCount(documentInfoList.size());
        searchResponse.setContent(documentInfoList);
        searchResponse.setResponseTime(time);
        return searchResponse;
    }

}
