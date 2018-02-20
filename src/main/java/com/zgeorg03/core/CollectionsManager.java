package com.zgeorg03.core;


import com.zgeorg03.StemmerService;
import com.zgeorg03.StopListService;
import com.zgeorg03.exceptions.*;
import com.zgeorg03.models.CollectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

@Configuration
public class CollectionsManager implements Runnable, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(CollectionsManager.class);
    private final String rootPath;
    private final File file;


    private final Map<String,Collection> collections = new HashMap<>();


    private final FileManager fileManager;

    private  StopListService stopListService;
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
    public CollectionsManager(@Value("${root.path}") String rootPath, FileManager fileManager) {
        this.rootPath = rootPath;
        this.file = Paths.get(rootPath,".collections-manager.zgi").toFile();
        this.fileManager = fileManager;
    }


    /**
     * Load  collections and indexes
     * @param file
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CollectionManagerNotExist
     */
    public void load(File file) throws IOException, ClassNotFoundException, CollectionManagerNotExist {
        if(!file.exists()){
            throw new CollectionManagerNotExist();
        }
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        int count = in.readInt();
        for(int i=0;i<count;i++){
            String key = in.readUTF();
            Collection collection = (Collection) in.readObject();
            this.collections.put(key,collection);
            logger.info("Loaded collection: "+key+"(docs="+collection.getDocuments().size()+")");
        }
        in.close();
    }

    /**
     * Store the state of the index
     * @param file
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CollectionManagerNotExist
     */
    public void writeTo(File file) throws IOException, ClassNotFoundException, CollectionManagerNotExist {
        /**
        if(file.exists()){
            throw new CollectionManagerNotExist();
        }
         **/
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeInt(collections.size());
        collections.entrySet().stream().forEachOrdered(entry->{
            String key = entry.getKey();
            Collection collection = entry.getValue();
            try {
                out.writeUTF(key);
                out.writeObject(collection);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        out.flush();
        out.close();
    }


    /**
     * Add a new collection
     * @param name
     * @return
     */
    public boolean addNewCollection(String name) throws NoPermissionsException, CollectionAlreadyExists {
        if(collections.containsKey(name)){
            throw new CollectionAlreadyExists(name);
        }
        logger.info("Creating new collection: " +name);
        String path = fileManager.createCollectionDirectory(name);

        Collection collection = new Collection(name, path);
        this.collections.put(name, collection);
        logger.info("Collection:"+name+" loaded");

        try {
            this.writeTo(this.file);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (CollectionManagerNotExist collectionManagerNotExist) {
            collectionManagerNotExist.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Delete a collection
     * @param name
     * @return
     * @throws CollectionNotFound
     * @throws FileNotDeleted
     * @throws NoPermissionsException
     */
    public boolean deleteCollection(String name) throws CollectionNotFound, FileNotDeleted, NoPermissionsException {
        collections.remove(name);
        fileManager.deleteCollectionDirectory(name);
        try {
            this.writeTo(this.file);
        } catch (ClassNotFoundException e) {
            logger.info(e.getLocalizedMessage());
        } catch (CollectionManagerNotExist e) {
            logger.info(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.info(e.getLocalizedMessage());
        }
        return true;
    }



    public boolean insertDocument(String collectionName, MultipartFile file) throws IOException, DocumentAlreadyExists, CollectionNotFound, DocumentFormatNotValid {

        String documentName = file.getOriginalFilename();

        if(!fileManager.collectionExists(collectionName))
            throw new CollectionNotFound(collectionName);

        logger.info("Inserting document"+ documentName +" to directory: " +collectionName);
        //Write to the disk
        File fp =fileManager.insertDocumentFile(collectionName, documentName, file.getInputStream());

        Collection collection = collections.get(collectionName);

        //Perform indexing
        try {
            collection.addDocument(fp, stopListService, stemmerService);
        }catch (DocumentFormatNotValid e){
            throw new DocumentFormatNotValid(documentName);
        }

        try {
            this.writeTo(this.file);
        } catch (ClassNotFoundException e) {
            logger.info(e.getLocalizedMessage());
        } catch (CollectionManagerNotExist e) {
            logger.info(e.getLocalizedMessage());
        }


        return true;
    }

    public long insertDocuments(String collectionName, File dir) throws CollectionNotFound {
        long start = System.currentTimeMillis();

        if(!fileManager.collectionExists(collectionName))
            throw new CollectionNotFound(collectionName);
        File files[] = dir.listFiles();

        Collection collection = collections.get(collectionName);
        for(File file:files){
            String documentName = file.getName();
            try {
                InputStream in = new FileInputStream(file);
                logger.info("Inserting document "+ documentName +" to directory: " +collectionName);
                //Write to the disk
                File fp =fileManager.insertDocumentFile(collectionName, documentName, in);
                //Perform indexing
                collection.addDocument(fp,stopListService,stemmerService);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentAlreadyExists documentAlreadyExists) {
                logger.info("Document "+documentName+" already exists");
            } catch (DocumentFormatNotValid documentFormatNotValid) {
                logger.info("Document "+documentName+" is invalid");
            }

        }




        //In the end save
        try {
            this.writeTo(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (CollectionManagerNotExist collectionManagerNotExist) {
            collectionManagerNotExist.printStackTrace();
        }

        long elapsed = System.currentTimeMillis()-start;

        return elapsed;

    }
    @Override
    public void run() {

        try {
            load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (CollectionManagerNotExist collectionManagerNotExist) {
            //Create new collections manager
        }

        /**
        while (true){
            logger.info(file.getAbsolutePath());
            try {
                writeTo(file);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (CollectionManagerNotExist collectionManagerNotExist) {
                collectionManagerNotExist.printStackTrace();
            }

            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
         }
**/
    }

    public List<CollectionInfo> getCollections() {
        List<CollectionInfo> collectionInfoList = new LinkedList<>();
        collections.entrySet().stream().forEach((e)->{
            String collection  = e.getKey();
            int size = e.getValue().getDocuments().size();
            collectionInfoList.add(new CollectionInfo(collection,size));
        });
        return collectionInfoList;
    }

    /**
     *
     * @param name
     * @return
     */
    public boolean collectionExists(String name){
        return fileManager.collectionExists(name);
    }

    public java.util.Collection<String> getCollectionDocuments(String collectionName) throws CollectionNotFound {
        Collection collection = collections.get(collectionName);
        if(collection==null)
            throw new CollectionNotFound(collectionName);
        return collection.getDocuments();
    }

    public CollectionIndex getCollectionIndex(String collection) throws CollectionNotFound {
        Collection col = collections.get(collection);
        if(col==null)
            throw new CollectionNotFound(collection);
        return col.getCollectionIndex();
    }

    public DocumentInfo getDocumentContent(String collectionName, Integer docId) throws CollectionNotFound, IOException, DocumentNotExists {
        Collection collection = collections.get(collectionName);
        if(collection==null)
            throw new CollectionNotFound(collectionName);
        String filename = collection.getFilesMap().get(docId);
        if(filename==null)
            throw  new DocumentNotExists(docId+"");
        String content = fileManager.getDocumentContents(collectionName,filename);
        return new DocumentInfo(docId, filename,content);

    }

    public void deleteDocument(String collectionName, String document) throws CollectionNotFound, DocumentNotExists {
        Collection collection = collections.get(collectionName);
        if(collection==null)
            throw new CollectionNotFound(collectionName);
        collection.removeDocument(document);
        fileManager.deleteDocument(collectionName,document);

        try {
            this.writeTo(this.file);
        } catch (ClassNotFoundException e) {
            logger.info(e.getLocalizedMessage());
        } catch (CollectionManagerNotExist e) {
            logger.info(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.info(e.getLocalizedMessage());
        }

    }

    public Set<String> getCollectionDictionary(String collectionName) throws CollectionNotFound {
        Collection collection = collections.get(collectionName);
        if(collection==null)
            throw new CollectionNotFound(collectionName);

        return collection.getCollectionIndex().getDictionary().keySet();
    }
}
