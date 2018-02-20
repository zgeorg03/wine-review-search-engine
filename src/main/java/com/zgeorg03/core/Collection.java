package com.zgeorg03.core;

import com.zgeorg03.StemmerService;
import com.zgeorg03.StopListService;
import com.zgeorg03.exceptions.DocumentFormatNotValid;
import com.zgeorg03.exceptions.DocumentNotExists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class Collection implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Collection.class);
    private final String name;
    private final String absolutePath;
    private final CollectionIndex collectionIndex;

    Map<Integer, String> filesMap = new HashMap<>();
    Map<String, Integer> fileNamesMap = new HashMap<>();

    public Collection(String name, String absolutePath){
        this.name = name;
        this.absolutePath = absolutePath;
        this.collectionIndex = new CollectionIndex();
    }

    /**
     * Parse the document
     * @param file
     */
    public void addDocument(File file, StopListService stopListService, StemmerService stemmerService) throws DocumentFormatNotValid {

        //Remove stop words

        try {
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = "";
            int id=-1;
            line=bf.readLine();
            //Split first by commas to take the id
            String toks[] = line.split(",");
            if(toks.length<1)
                throw new DocumentFormatNotValid(file.getName());

            try {
                id = Integer.parseInt(toks[0]);
            }catch(NumberFormatException ex){
                throw new DocumentFormatNotValid(file.getName());
            }

            String descr = getDescriptionField(line);

            StringTokenizer tokenizer = new StringTokenizer(descr, " \t\n\r\f-,.:;?![]'\"()$&/”“—%");

            int position = 0;
            while(tokenizer.hasMoreTokens()){
                String word = tokenizer.nextToken().toLowerCase();


                // Skip if the word is a stop word
                if(stopListService.isStopWord(word)) {
                    position++;
                    continue;
                }

                //Stemming
                String term = stemmerService.executeStemming(word);
                logger.debug(position+":"+word+"\t"+term);
                collectionIndex.addTerm(term,id,position);
                position++;
            }

            filesMap.put(id,file.getName());
            fileNamesMap.put(file.getName(),id);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getDescriptionField(String line) throws DocumentFormatNotValid {
        String split[] = line.split("\"");
        if(split.length<=2)
            throw new DocumentFormatNotValid("");
        String desc = split[1];
        return desc;
    }

    public CollectionIndex getCollectionIndex() {
        return collectionIndex;
    }

    public String getName() {
        return name;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public static Logger getLogger() {
        return logger;
    }

    public Map<Integer, String> getFilesMap() {
        return filesMap;
    }

    @Override
    public String toString() {
        return "Collection{" +
                "name='" + name + '\'' +
                ", absolutePath='" + absolutePath + '\'' +
                ", filesMap=" + filesMap +
                '}';
    }

    public java.util.Collection<String> getDocuments() {
        return filesMap.values();
    }

    public void removeDocument(String document) throws DocumentNotExists {
        Integer id = fileNamesMap.get(document);
        if(id==null)
            throw new DocumentNotExists(document);

        collectionIndex.removeDocument(id);
        filesMap.remove(id);
        fileNamesMap.remove(document);

    }
}

