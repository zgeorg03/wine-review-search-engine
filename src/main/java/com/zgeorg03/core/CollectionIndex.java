package com.zgeorg03.core;

import com.zgeorg03.StemmerService;
import com.zgeorg03.StopListService;
import com.zgeorg03.exceptions.QueryFormatNotValid;
import com.zgeorg03.models.PostingList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class CollectionIndex implements Serializable{
    private static final Logger logger = LoggerFactory.getLogger(CollectionIndex.class);

    /** Map of terms -> SortedSet (PostingEntry,PostingEntry,...) */
    private final TreeMap<String,PostingList> dictionary;

    private final Set<Integer> documents = new TreeSet<>();

    public CollectionIndex() {
        dictionary = new TreeMap<>();
    }


    /**
     * Add to the specified term the documentID that belongs to and the position which is found
     * @param term
     * @param documentID
     * @param position
     */
    public void addTerm(String term, int documentID, int position){

        documents.add(documentID);

        PostingList postingList = dictionary.getOrDefault(term,new PostingList());

        postingList.addDocumentRef(documentID,position);

        dictionary.putIfAbsent(term,postingList);


    }

    public TreeMap<String, PostingList> getDictionary() {
        return dictionary;
    }

    @Override
    public String toString() {
        return "CollectionIndex{" +
                "dictionary=" + dictionary +
                '}';
    }


    public Set<Integer> getNotIncludedDocs(String term){
        Set<Integer> set;
        if(dictionary.get(term)==null)
            set = new TreeSet<>();
        else
            set = dictionary.get(term).getDocuments().keySet();

        return Utils.diffSortedSets(documents,set);
    }

    public Set<Integer> search(Query q,StopListService stopListService, StemmerService stemmerService) throws QueryFormatNotValid {
        Set<Integer> lastDocs = null;
        Set<Integer> empty = new TreeSet<>();

        Query.BoolOperation lastBoolOperation = Query.BoolOperation.NONE;

        for(Query.Operation operation : q.getOperationList()){
            boolean isNeg = operation.isNegated();
            Query.BoolOperation boolOperation = operation.getOperation();

            String term = operation.getTerm();

            //1-step Lower Case
            term = term.toLowerCase();

            //Stop words
            if(stopListService.isStopWord(term)){
                logger.info(term+" is a stop word, we skip...");
                continue;
            }

            //3rd step
            term = stemmerService.executeStemming(term);

            Set<Integer> docs;
            //No negation, we retrieve it as is
            if(!isNeg) {
                if(dictionary.get(term)==null)
                    docs = empty;
                else
                    docs = dictionary.get(term).getDocuments().keySet();
            } else
                docs = getNotIncludedDocs(term);

            if(lastBoolOperation == Query.BoolOperation.NONE){
                //Nothing to do
            }else if(lastBoolOperation == Query.BoolOperation.AND){
                docs = Utils.intersectSortedSets(lastDocs,docs);
            }else if(lastBoolOperation == Query.BoolOperation.OR){
                docs = Utils.unionSortedSets(lastDocs,docs);
            }


            lastBoolOperation = boolOperation;
            lastDocs = docs;
        }
        if (lastDocs ==null)
            return new TreeSet<>();

        return lastDocs;
    }
    public Set<Integer> search2(String q, StopListService stopListService, StemmerService stemmerService) throws QueryFormatNotValid {
        String words[] = q.split(",");
        if(words.length==0)
            throw new QueryFormatNotValid(q);

        Set<Integer> lastDocs = null;
        for(int i=0;i<words.length;i++){

            String word = words[i];

            word = word.toLowerCase();

            //Stop words first
            if(stopListService.isStopWord(word)){
                logger.info(word+" is a stop word");
                return new TreeSet<>();
            }

            String term = stemmerService.executeStemming(word);
            logger.info("Querying:"+term);
            PostingList list = dictionary.get(term);
            //Because is AND
            if(list==null)
                return new TreeSet<>();
            Set<Integer> docs =  list.getDocuments().keySet();

            if(lastDocs!=null){
                docs = Utils.intersectSortedSets(lastDocs,docs);
            }
            lastDocs = docs;

        }

        return lastDocs;
    }
}
