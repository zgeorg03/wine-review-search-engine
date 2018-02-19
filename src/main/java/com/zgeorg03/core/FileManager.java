package com.zgeorg03.core;


import com.zgeorg03.exceptions.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class FileManager {
    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);
    private final File root;

    private final Map<String,String> cache;

    @Autowired
    public FileManager(@Value("${root.path}") String path) throws Exception {
        this.root = Paths.get(path).toFile();
        this.cache = new HashMap<>();
        logger.info("FileManager initialized in path:"+path);

        File file = Paths.get(path).toFile();
        if(!file.exists()){
            try {
                Files.createDirectories(file.toPath());
                logger.info("Directory:" + file.toPath() +" has been created");
            } catch (IOException e) {
                logger.error(path+" couldn't be created");
                throw new Exception(path+" couldn't be created");
            }
        }
        if(!file.isDirectory()) {
            logger.error("Path:"+file.getAbsolutePath()+" is not a directory");
            throw new Exception(file.getAbsolutePath()+" is not a directory");
        }
    }


    /**
     *
     * @param name
     * @return Absolute Path
     * @throws CollectionAlreadyExists
     * @throws NoPermissionsException
     */
    public String createCollectionDirectory(String name) throws CollectionAlreadyExists, NoPermissionsException {
        logger.trace("Creating directory:"+name);
        File dir = Paths.get(root.getAbsolutePath(),name).toFile();

        if(dir.isDirectory()){
            throw new CollectionAlreadyExists(name);
        }

        try {
            dir.mkdir();
            return dir.getAbsolutePath();
        }catch (SecurityException ex){
            throw new  NoPermissionsException(name);
        }
    }

    /**
     *
     * @param name
     * @return
     * @throws CollectionAlreadyExists
     * @throws NoPermissionsException
     */
    public boolean deleteCollectionDirectory(String name) throws FileNotDeleted, CollectionNotFound, NoPermissionsException {
        logger.trace("Deleting directory:"+name);
        File dir = Paths.get(root.getAbsolutePath(),name).toFile();

        if(!dir.isDirectory()){
            throw new CollectionNotFound(name);
        }

        try {
            dir.mkdir();
            File files[] = dir.listFiles();
            for(File file : files)
                Files.delete(file.toPath());
            //Delete
            dir.delete();
            return true;
        }catch (SecurityException ex){
            throw new  NoPermissionsException(name);
        } catch (IOException e) {
            throw new FileNotDeleted(name);
        }
    }

    /**
     * Insert a document
     * @param collectionName
     * @param file
     * @param inputStream
     * @throws DocumentAlreadyExists
     * @throws IOException
     */
    public File insertDocumentFile(String collectionName, String file, InputStream inputStream) throws DocumentAlreadyExists, IOException {
        File fp = Paths.get(root.getAbsolutePath(),collectionName,file).toFile();

        if(fp.exists())
            throw new DocumentAlreadyExists(file);

        OutputStream outputStream = new FileOutputStream(fp);
        IOUtils.copy(inputStream, outputStream);
        outputStream.flush();
        return fp;
    }

    public String getDocumentContents(String collectionName,String file) throws DocumentNotExists, IOException {
        File fp = Paths.get(root.getAbsolutePath(),collectionName,file).toFile();

        if(!fp.exists())
            throw new DocumentNotExists(file);

        String path = fp.getAbsolutePath();

        // HIT
        if(cache.containsKey(path))
            return cache.get(path);

        // MISS
        String content=Files.readAllLines(fp.toPath()).stream().collect(Collectors.joining("\n"));
        cache.put(path,content);

        return content;

    }

    /**
     *
     * @param name
     * @return
     */
    public boolean collectionExists(String name){
        return Paths.get(root.getAbsolutePath(),name).toFile().exists();
    }


    /**
     * Return all folders
     * @return
     */
    public List<String> readAllExistingCollections(){
        return Arrays.stream(root.list((x, y) -> x.isDirectory())).collect(Collectors.toList());
    }

}
